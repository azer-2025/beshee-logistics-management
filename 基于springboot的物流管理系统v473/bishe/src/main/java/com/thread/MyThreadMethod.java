package com.thread;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.CangkuEntity;
import com.entity.HuowuEntity;
import com.entity.JiankongyujingEntity;
import com.entity.ShangpinEntity;
import com.entity.ShangpinOrderEntity;
import com.service.CangkuService;
import com.service.HuowuService;
import com.service.JiankongyujingService;
import com.service.ShangpinOrderService;
import com.service.ShangpinService;
import com.utils.CommonUtil;
import com.utils.SpringContextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 项目启动后执行的后台巡检线程。
 */
public class MyThreadMethod extends Thread {
    private static final long SCAN_INTERVAL_MS = 60_000L;
    private static final int LOW_STOCK_THRESHOLD = 103;
    private static final int LOW_STOCK_SEVERE_THRESHOLD = 80;
    private static final int TEMPERATURE_ALERT_THRESHOLD = 8;
    private static final long PAID_TIMEOUT_MS = 2 * 60 * 60 * 1000L;
    private static final long DELIVERY_TIMEOUT_MS = 6 * 60 * 60 * 1000L;

    private static final int ALERT_STATUS_PENDING = 1;
    private static final int ALERT_STATUS_PROCESSING = 2;
    private static final int ALERT_STATUS_RESOLVED = 3;
    private static final int ALERT_MODE_AUTO = 1;

    private static final String STOCK_ALERT_NAME = "库存低于安全线预警";
    private static final String PAY_TIMEOUT_ALERT_NAME = "订单发货超时预警";
    private static final String DELIVERY_TIMEOUT_ALERT_NAME = "订单配送超时预警";
    private static final String TEMPERATURE_ALERT_NAME = "冷库温度异常预警";
    private static final String ORDER_TEMPERATURE_ALERT_NAME = "订单冷链温控预警";

    public MyThreadMethod() {
        setName("monitor-alert-thread");
        setDaemon(true);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                if (!executeMonitorTasks()) {
                    break;
                }
                Thread.sleep(SCAN_INTERVAL_MS);
            } catch (InterruptedException e) {
                interrupt();
                break;
            } catch (IllegalStateException e) {
                if (!SpringContextUtils.isContextActive()) {
                    break;
                }
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        interrupt();
    }

    private boolean executeMonitorTasks() {
        if (!SpringContextUtils.isContextActive()) {
            return false;
        }

        JiankongyujingService alertService = SpringContextUtils.getBeanIfActive("jiankongyujingService", JiankongyujingService.class);
        ShangpinService shangpinService = SpringContextUtils.getBeanIfActive("shangpinService", ShangpinService.class);
        ShangpinOrderService shangpinOrderService = SpringContextUtils.getBeanIfActive("shangpinOrderService", ShangpinOrderService.class);
        CangkuService cangkuService = SpringContextUtils.getBeanIfActive("cangkuService", CangkuService.class);
        HuowuService huowuService = SpringContextUtils.getBeanIfActive("huowuService", HuowuService.class);

        if (alertService == null || shangpinService == null || shangpinOrderService == null || cangkuService == null || huowuService == null) {
            return false;
        }

        scanLowStockAlerts(alertService, shangpinService);
        scanOrderTimeoutAlerts(alertService, shangpinOrderService, huowuService);
        scanWarehouseTemperatureAlerts(alertService, cangkuService, shangpinOrderService, shangpinService);
        return true;
    }

    private void scanLowStockAlerts(JiankongyujingService alertService, ShangpinService shangpinService) {
        List<ShangpinEntity> lowStockList = shangpinService.selectList(
                new EntityWrapper<ShangpinEntity>()
                        .eq("shangpin_delete", 1)
                        .eq("shangxia_types", 1)
                        .lt("shangpin_kucun_number", LOW_STOCK_THRESHOLD)
        );

        Set<String> activeObjects = new HashSet<>();
        for (ShangpinEntity shangpin : lowStockList) {
            String object = shangpin.getShangpinName() + "（" + shangpin.getShangpinUuidNumber() + "）";
            activeObjects.add(object);
            Integer currentStock = shangpin.getShangpinKucunNumber() == null ? 0 : shangpin.getShangpinKucunNumber();
            Integer level = currentStock <= LOW_STOCK_SEVERE_THRESHOLD ? 3 : 2;

            saveOrRefreshAlert(
                    alertService,
                    STOCK_ALERT_NAME,
                    object,
                    3,
                    level,
                    currentStock + "件",
                    "安全库存" + LOW_STOCK_THRESHOLD + "件",
                    "库存巡检",
                    "系统巡检发现商品库存已低于安全库存线，建议尽快补货并关注冷链存储状态。",
                    null,
                    null
            );
        }

        resolveMissingAlerts(
                alertService,
                STOCK_ALERT_NAME,
                activeObjects,
                "库存已恢复至安全范围，系统自动解除该库存预警。"
        );
    }

    private void scanOrderTimeoutAlerts(
            JiankongyujingService alertService,
            ShangpinOrderService shangpinOrderService,
            HuowuService huowuService
    ) {
        List<ShangpinOrderEntity> orderList = shangpinOrderService.selectList(
                new EntityWrapper<ShangpinOrderEntity>()
                        .in("shangpin_order_types", Arrays.asList(101, 103))
        );

        Set<String> payTimeoutObjects = new HashSet<>();
        Set<String> deliveryTimeoutObjects = new HashSet<>();
        long now = System.currentTimeMillis();

        for (ShangpinOrderEntity order : orderList) {
            String object = "订单" + order.getShangpinOrderUuidNumber();

            Date createTime = order.getInsertTime() != null ? order.getInsertTime() : order.getCreateTime();
            if (Integer.valueOf(101).equals(order.getShangpinOrderTypes()) && createTime != null) {
                long age = now - createTime.getTime();
                if (age >= PAID_TIMEOUT_MS) {
                    payTimeoutObjects.add(object);
                    saveOrRefreshAlert(
                            alertService,
                            PAY_TIMEOUT_ALERT_NAME,
                            object,
                            4,
                            2,
                            buildDurationText(age),
                            "2小时内应完成发货",
                            "订单时效巡检",
                            "系统巡检发现订单支付后长时间未发货，可能影响生鲜冷链时效，请尽快安排出库发运。",
                            order.getYonghuId(),
                            order.getId()
                    );
                }
            }

            Date deliveryStartTime = findDeliveryStartTime(order, huowuService);
            if (Integer.valueOf(103).equals(order.getShangpinOrderTypes()) && deliveryStartTime != null) {
                long age = now - deliveryStartTime.getTime();
                if (age >= DELIVERY_TIMEOUT_MS) {
                    deliveryTimeoutObjects.add(object);
                    saveOrRefreshAlert(
                            alertService,
                            DELIVERY_TIMEOUT_ALERT_NAME,
                            object,
                            4,
                            3,
                            buildDurationText(age),
                            "6小时内应完成配送",
                            "订单时效巡检",
                            "系统巡检发现订单已发货但配送耗时过长，可能存在冷链断链风险，请及时核查车辆与司机状态。",
                            order.getYonghuId(),
                            order.getId()
                    );
                }
            }
        }

        resolveMissingAlerts(
                alertService,
                PAY_TIMEOUT_ALERT_NAME,
                payTimeoutObjects,
                "订单已完成发货或已不满足超时条件，系统自动解除该发货超时预警。"
        );
        resolveMissingAlerts(
                alertService,
                DELIVERY_TIMEOUT_ALERT_NAME,
                deliveryTimeoutObjects,
                "订单已完成配送或已不满足超时条件，系统自动解除该配送超时预警。"
        );
    }

    private Date findDeliveryStartTime(ShangpinOrderEntity order, HuowuService huowuService) {
        if (order == null || order.getId() == null) {
            return null;
        }
        HuowuEntity deliveryRecord = huowuService.selectOne(
                new EntityWrapper<HuowuEntity>()
                        .eq("shangpin_order_id", order.getId())
                        .eq("huowu_types", 1)
        );
        if (deliveryRecord != null) {
            Date deliveryTime = deliveryRecord.getInsertTime() != null ? deliveryRecord.getInsertTime() : deliveryRecord.getCreateTime();
            if (deliveryTime != null) {
                return deliveryTime;
            }
        }
        return order.getInsertTime() != null ? order.getInsertTime() : order.getCreateTime();
    }

    private void scanWarehouseTemperatureAlerts(
            JiankongyujingService alertService,
            CangkuService cangkuService,
            ShangpinOrderService shangpinOrderService,
            ShangpinService shangpinService
    ) {
        List<CangkuEntity> cangkuList = cangkuService.selectList(
                new EntityWrapper<CangkuEntity>().eq("cangku_delete", 1)
        );
        List<ShangpinOrderEntity> paidOrders = shangpinOrderService.selectList(
                new EntityWrapper<ShangpinOrderEntity>().eq("shangpin_order_types", 101)
        );

        Set<String> activeObjects = new HashSet<>();
        Set<String> orderActiveObjects = new HashSet<>();
        for (CangkuEntity cangku : cangkuList) {
            int simulatedTemperature = 7 + (cangku.getId() % 4);
            if (simulatedTemperature > TEMPERATURE_ALERT_THRESHOLD) {
                String object = cangku.getCangkuName();
                activeObjects.add(object);
                saveOrRefreshAlert(
                        alertService,
                        TEMPERATURE_ALERT_NAME,
                        object,
                        1,
                        simulatedTemperature >= 10 ? 3 : 2,
                        simulatedTemperature + "℃",
                        "冷链建议范围2℃-8℃",
                        "仓库温控巡检",
                        "系统按仓库温控巡检规则检测到温度超出冷链建议范围，请及时核查制冷设备、库门开闭和批次停留时长。",
                        null,
                        null
                );

                for (ShangpinOrderEntity order : paidOrders) {
                    ShangpinEntity shangpin = shangpinService.selectById(order.getShangpinId());
                    if (shangpin == null || shangpin.getCangkuId() == null || !shangpin.getCangkuId().equals(cangku.getId())) {
                        continue;
                    }
                    String orderObject = "订单" + order.getShangpinOrderUuidNumber();
                    orderActiveObjects.add(orderObject);
                    saveOrRefreshAlert(
                            alertService,
                            ORDER_TEMPERATURE_ALERT_NAME,
                            orderObject,
                            1,
                            simulatedTemperature >= 10 ? 3 : 2,
                            simulatedTemperature + "℃",
                            "冷链建议范围2℃-8℃",
                            "仓库温控巡检",
                            "系统检测到订单待发货仓库“" + cangku.getCangkuName() + "”温度异常，当前监测值为" + simulatedTemperature + "℃，请关注订单冷链状态并等待仓储处理。",
                            order.getYonghuId(),
                            order.getId()
                    );
                }
            }
        }

        resolveMissingAlerts(
                alertService,
                TEMPERATURE_ALERT_NAME,
                activeObjects,
                "仓库温控已恢复至安全区间，系统自动解除该温度异常预警。"
        );
        resolveMissingAlerts(
                alertService,
                ORDER_TEMPERATURE_ALERT_NAME,
                orderActiveObjects,
                "关联仓库温控已恢复正常或订单已不再处于待发货状态，系统自动解除该订单冷链温控预警。"
        );
    }

    private void saveOrRefreshAlert(
            JiankongyujingService alertService,
            String alertName,
            String object,
            Integer monitorType,
            Integer level,
            String data,
            String threshold,
            String source,
            String content,
            Integer yonghuId,
            Integer shangpinOrderId
    ) {
        JiankongyujingEntity currentAutoAlert = findAutoAlert(alertService, alertName, object);
        Date now = new Date();

        if (currentAutoAlert == null) {
            if (existsActiveAlert(alertService, alertName, object)) {
                return;
            }
            JiankongyujingEntity alert = new JiankongyujingEntity();
            alert.setJiankongyujingUuidNumber(buildAlertUuid());
            alert.setJiankongyujingName(alertName);
            alert.setJiankongTypes(monitorType);
            alert.setYujingLevelTypes(level);
            alert.setYujingStatusTypes(ALERT_STATUS_PENDING);
            alert.setYujingModeTypes(ALERT_MODE_AUTO);
            alert.setYujingSource(source);
            alert.setYonghuId(yonghuId);
            alert.setShangpinOrderId(shangpinOrderId);
            alert.setJiankongObject(object);
            alert.setJiankongData(data);
            alert.setYujingThreshold(threshold);
            alert.setJiankongTime(now);
            alert.setYujingContent(content);
            alert.setCreateTime(now);
            alertService.insert(alert);
            return;
        }

        currentAutoAlert.setJiankongTypes(monitorType);
        currentAutoAlert.setYujingLevelTypes(level);
        currentAutoAlert.setYujingModeTypes(ALERT_MODE_AUTO);
        currentAutoAlert.setYujingSource(source);
        currentAutoAlert.setYonghuId(yonghuId);
        currentAutoAlert.setShangpinOrderId(shangpinOrderId);
        currentAutoAlert.setJiankongData(data);
        currentAutoAlert.setYujingThreshold(threshold);
        currentAutoAlert.setJiankongTime(now);
        currentAutoAlert.setYujingContent(content);
        currentAutoAlert.setChuliTime(null);
        currentAutoAlert.setChuliContent(null);
        alertService.updateById(currentAutoAlert);
    }

    private void resolveMissingAlerts(
            JiankongyujingService alertService,
            String alertName,
            Set<String> activeObjects,
            String resolveContent
    ) {
        List<JiankongyujingEntity> activeAlerts = alertService.selectList(
                new EntityWrapper<JiankongyujingEntity>()
                        .eq("jiankongyujing_name", alertName)
                        .eq("yujing_mode_types", ALERT_MODE_AUTO)
                        .in("yujing_status_types", new ArrayList<Integer>(Arrays.asList(ALERT_STATUS_PENDING, ALERT_STATUS_PROCESSING)))
        );

        for (JiankongyujingEntity alert : activeAlerts) {
            if (!activeObjects.contains(alert.getJiankongObject())) {
                alert.setYujingStatusTypes(ALERT_STATUS_RESOLVED);
                alert.setChuliContent(resolveContent);
                alert.setChuliTime(new Date());
                alertService.updateById(alert);
            }
        }
    }

    private boolean existsActiveAlert(JiankongyujingService alertService, String alertName, String object) {
        Integer count = alertService.selectCount(
                new EntityWrapper<JiankongyujingEntity>()
                        .eq("jiankongyujing_name", alertName)
                        .eq("jiankong_object", object)
                        .in("yujing_status_types", new ArrayList<Integer>(Arrays.asList(ALERT_STATUS_PENDING, ALERT_STATUS_PROCESSING)))
        );
        return count != null && count > 0;
    }

    private JiankongyujingEntity findAutoAlert(JiankongyujingService alertService, String alertName, String object) {
        List<JiankongyujingEntity> list = alertService.selectList(
                new EntityWrapper<JiankongyujingEntity>()
                        .eq("jiankongyujing_name", alertName)
                        .eq("jiankong_object", object)
                        .eq("yujing_mode_types", ALERT_MODE_AUTO)
                        .in("yujing_status_types", new ArrayList<Integer>(Arrays.asList(ALERT_STATUS_PENDING, ALERT_STATUS_PROCESSING)))
        );
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    private String buildAlertUuid() {
        return "AUTO" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + CommonUtil.getRandomString(4).toUpperCase();
    }

    private String buildDurationText(long durationMs) {
        long totalMinutes = durationMs / (60 * 1000L);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        if (hours <= 0) {
            return minutes + "分钟";
        }
        return hours + "小时" + minutes + "分钟";
    }
}
