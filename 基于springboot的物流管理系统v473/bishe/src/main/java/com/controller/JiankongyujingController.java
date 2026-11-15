package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.JiankongyujingEntity;
import com.entity.ShangpinOrderEntity;
import com.entity.view.JiankongyujingView;
import com.service.DictionaryService;
import com.service.JiankongyujingService;
import com.service.ShangpinOrderService;
import com.utils.CommonUtil;
import com.utils.PageUtils;
import com.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jiankongyujing")
public class JiankongyujingController {
    private static final Logger logger = LoggerFactory.getLogger(JiankongyujingController.class);

    @Autowired
    private JiankongyujingService jiankongyujingService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private ShangpinOrderService shangpinOrderService;

    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        logger.debug("page方法: {}, params: {}", this.getClass().getName(), JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("用户".equals(role)) {
            params.put("yonghuId", request.getSession().getAttribute("userId"));
        }
        CommonUtil.checkMap(params);
        PageUtils page = jiankongyujingService.queryPage(params);
        List<JiankongyujingView> list = (List<JiankongyujingView>) page.getList();
        for (JiankongyujingView view : list) {
            dictionaryService.dictionaryConvert(view, request);
        }
        return R.ok().put("data", page);
    }

    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request) {
        logger.debug("info方法: {}, id: {}", this.getClass().getName(), id);
        JiankongyujingEntity entity = jiankongyujingService.selectById(id);
        if (entity == null) {
            return R.error(511, "查不到数据");
        }
        String role = String.valueOf(request.getSession().getAttribute("role"));
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if ("用户".equals(role) && entity.getYonghuId() != null && userId != null && !userId.equals(entity.getYonghuId())) {
            return R.error(511, "无权查看该预警");
        }
        JiankongyujingView view = new JiankongyujingView();
        BeanUtils.copyProperties(entity, view);
        dictionaryService.dictionaryConvert(view, request);
        return R.ok().put("data", view);
    }

    @RequestMapping("/save")
    public R save(@RequestBody JiankongyujingEntity jiankongyujing) {
        logger.debug("save方法: {}, jiankongyujing: {}", this.getClass().getName(), jiankongyujing);
        fillDefaultFields(jiankongyujing, null);

        Wrapper<JiankongyujingEntity> queryWrapper = new EntityWrapper<JiankongyujingEntity>()
                .eq("jiankongyujing_uuid_number", jiankongyujing.getJiankongyujingUuidNumber());
        JiankongyujingEntity oldEntity = jiankongyujingService.selectOne(queryWrapper);
        if (oldEntity != null) {
            return R.error(511, "预警编号已存在");
        }

        jiankongyujingService.insert(jiankongyujing);
        return R.ok();
    }

    @RequestMapping("/update")
    public R update(@RequestBody JiankongyujingEntity jiankongyujing) {
        logger.debug("update方法: {}, jiankongyujing: {}", this.getClass().getName(), jiankongyujing);
        JiankongyujingEntity oldEntity = jiankongyujingService.selectById(jiankongyujing.getId());
        if (oldEntity == null) {
            return R.error(511, "查不到数据");
        }

        fillDefaultFields(jiankongyujing, oldEntity);

        Wrapper<JiankongyujingEntity> queryWrapper = new EntityWrapper<JiankongyujingEntity>()
                .eq("jiankongyujing_uuid_number", jiankongyujing.getJiankongyujingUuidNumber())
                .ne("id", jiankongyujing.getId());
        JiankongyujingEntity sameEntity = jiankongyujingService.selectOne(queryWrapper);
        if (sameEntity != null) {
            return R.error(511, "预警编号已存在");
        }

        jiankongyujingService.updateById(jiankongyujing);
        return R.ok();
    }

    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        logger.debug("delete方法: {}, ids: {}", this.getClass().getName(), Arrays.toString(ids));
        jiankongyujingService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    private void fillDefaultFields(JiankongyujingEntity entity, JiankongyujingEntity oldEntity) {
        if (StringUtils.isBlank(entity.getJiankongyujingUuidNumber())) {
            entity.setJiankongyujingUuidNumber(oldEntity == null ? buildUuidNumber() : oldEntity.getJiankongyujingUuidNumber());
        }
        if (entity.getJiankongTime() == null) {
            entity.setJiankongTime(oldEntity == null ? new Date() : oldEntity.getJiankongTime());
        }
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(oldEntity == null ? new Date() : oldEntity.getCreateTime());
        }
        if (entity.getYujingStatusTypes() == null) {
            entity.setYujingStatusTypes(oldEntity == null ? 1 : oldEntity.getYujingStatusTypes());
        }
        if (entity.getYujingModeTypes() == null) {
            entity.setYujingModeTypes(oldEntity == null ? 2 : oldEntity.getYujingModeTypes());
        }
        if (StringUtils.isBlank(entity.getYujingSource())) {
            entity.setYujingSource(oldEntity == null ? "人工录入" : oldEntity.getYujingSource());
        }
        if (entity.getYonghuId() == null && oldEntity != null) {
            entity.setYonghuId(oldEntity.getYonghuId());
        }
        if (entity.getShangpinOrderId() == null && oldEntity != null) {
            entity.setShangpinOrderId(oldEntity.getShangpinOrderId());
        }
        bindOrderRelation(entity);
        if (StringUtils.isBlank(entity.getChuliContent())) {
            entity.setChuliContent(null);
        }
        if (entity.getYujingStatusTypes() != null && entity.getYujingStatusTypes() == 3) {
            if (entity.getChuliTime() == null) {
                entity.setChuliTime(oldEntity != null && oldEntity.getChuliTime() != null ? oldEntity.getChuliTime() : new Date());
            }
        } else {
            entity.setChuliTime(null);
        }
    }

    private String buildUuidNumber() {
        return "YJ" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + CommonUtil.getRandomString(4).toUpperCase();
    }

    private void bindOrderRelation(JiankongyujingEntity entity) {
        if (entity == null) {
            return;
        }

        if (entity.getShangpinOrderId() != null) {
            ShangpinOrderEntity order = shangpinOrderService.selectById(entity.getShangpinOrderId());
            if (order != null && entity.getYonghuId() == null) {
                entity.setYonghuId(order.getYonghuId());
            }
            return;
        }

        ShangpinOrderEntity order = findOrderByAlert(entity);
        if (order == null) {
            return;
        }

        entity.setShangpinOrderId(order.getId());
        if (entity.getYonghuId() == null) {
            entity.setYonghuId(order.getYonghuId());
        }
    }

    private ShangpinOrderEntity findOrderByAlert(JiankongyujingEntity entity) {
        if (entity == null) {
            return null;
        }

        String[] candidates = new String[] {
                entity.getJiankongObject(),
                entity.getJiankongyujingName()
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
