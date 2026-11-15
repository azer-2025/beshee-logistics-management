package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.JiankongyujingEntity;
import com.entity.view.JiankongyujingView;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface JiankongyujingDao extends BaseMapper<JiankongyujingEntity> {

    List<JiankongyujingView> selectListView(Pagination page, @Param("params") Map<String, Object> params);
}
