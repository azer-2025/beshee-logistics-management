package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JiankongyujingDao;
import com.entity.JiankongyujingEntity;
import com.entity.ShangpinOrderEntity;
import com.entity.view.JiankongyujingView;
import com.service.JiankongyujingService;
import com.service.ShangpinOrderService;
import com.utils.PageUtils;
import com.utils.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("jiankongyujingService")
@Transactional
public class JiankongyujingServiceImpl extends ServiceImpl<JiankongyujingDao, JiankongyujingEntity> implements JiankongyujingService {

    private static final String PAY_TIMEOUT_ALERT_NAME = "订单发货超时预警";
    private static final String DELIVERY_TIMEOUT_ALERT_NAME = "订单配送超时预警";

    @Autowired
    private ShangpinOrderService shangpinOrderService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        synchronizeOrderAlertStatus();
        synchronizeManualOrderRelation();
        Page<JiankongyujingView> page = new Query<JiankongyujingView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, params));
        return new PageUtils(page);
    }

    private void synchronizeOrderAlertStatus() {
        List<JiankongyujingEntity> activeAlerts = this.selectList(
                new EntityWrapper<JiankongyujingEntity>()
                        .in("jiankongyujing_name", Arrays.asList(PAY_TIMEOUT_ALERT_NAME, DELIVERY_TIMEOUT_ALERT_NAME))
                        .in("yujing_status_types", Arrays.asList(1, 2))
        );
        Date now = new Date();
        for (JiankongyujingEntity alert : activeAlerts) {
            if (alert.getShangpinOrderId() == null) {
                continue;
            }
            ShangpinOrderEntity order = shangpinOrderService.selectById(alert.getShangpinOrderId());
            if (order == null) {
                continue;
            }
            if (PAY_TIMEOUT_ALERT_NAME.equals(alert.getJiankongyujingName()) && !Integer.valueOf(101).equals(order.getShangpinOrderTypes())) {
                alert.setYujingStatusTypes(3);
                alert.setChuliContent("订单状态已变化，系统自动解除该发货超时预警。");
                alert.setChuliTime(now);
                this.updateById(alert);
            }
            if (DELIVERY_TIMEOUT_ALERT_NAME.equals(alert.getJiankongyujingName()) && !Integer.valueOf(103).equals(order.getShangpinOrderTypes())) {
                alert.setYujingStatusTypes(3);
                alert.setChuliContent("订单状态已变化，系统自动解除该配送超时预警。");
                alert.setChuliTime(now);
                this.updateById(alert);
            }
        }
    }

    private void synchronizeManualOrderRelation() {
        List<JiankongyujingEntity> alerts = this.selectList(
                new EntityWrapper<JiankongyujingEntity>()
                        .eq("yujing_mode_types", 2)
        );

        for (JiankongyujingEntity alert : alerts) {
            if (alert.getShangpinOrderId() != null && alert.getYonghuId() != null) {
                continue;
            }

            ShangpinOrderEntity order = resolveOrder(alert);
            if (order == null) {
                continue;
            }

            boolean changed = false;
            if (alert.getShangpinOrderId() == null) {
                alert.setShangpinOrderId(order.getId());
                changed = true;
            }
            if (alert.getYonghuId() == null && order.getYonghuId() != null) {
                alert.setYonghuId(order.getYonghuId());
                changed = true;
            }

            if (changed) {
                this.updateById(alert);
            }
        }
    }

    private ShangpinOrderEntity resolveOrder(JiankongyujingEntity alert) {
        if (alert == null) {
            return null;
        }

        if (alert.getShangpinOrderId() != null) {
            return shangpinOrderService.selectById(alert.getShangpinOrderId());
        }

        String[] candidates = new String[] {
                alert.getJiankongObject(),
                alert.getJiankongyujingName()
        };

        for (String candidate : candidates) {
            String normalized = normalizeOrderUuid(candidate);
            if (StringUtils.isBlank(normalized)) {
                continue;
            }

            ShangpinOrderEntity order = shangpinOrderService.selectOne(
                    new EntityWrapper<ShangpinOrderEntity>()
                            .eq("shangpin_order_uuid_number", normalized)
            );
            if (order != null) {
                return order;
            }
        }

        return null;
    }

    private String normalizeOrderUuid(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        String normalized = value.replace(" ", "").trim();
        int index = normalized.lastIndexOf("订单");
        if (index >= 0) {
            normalized = normalized.substring(index + 2);
        }

        return StringUtils.isBlank(normalized) ? null : normalized;
    }
}
