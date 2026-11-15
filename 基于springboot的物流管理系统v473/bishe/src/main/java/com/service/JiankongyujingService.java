package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.entity.JiankongyujingEntity;
import com.utils.PageUtils;

import java.util.Map;

public interface JiankongyujingService extends IService<JiankongyujingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}
