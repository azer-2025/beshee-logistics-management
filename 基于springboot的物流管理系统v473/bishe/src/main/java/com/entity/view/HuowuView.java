package com.entity.view;

import org.apache.tools.ant.util.DateUtils;
import com.annotation.ColumnInfo;
import com.entity.HuowuEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import com.utils.DateUtil;

/**
 * 货物
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 */
@TableName("huowu")
public class HuowuView extends HuowuEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	//当前表
	/**
	 * 货物类型的值
	 */
	@ColumnInfo(comment="货物类型的字典表值",type="varchar(200)")
	private String huowuValue;

	//级联表 商品订单
	/**
	 * 订单编号
	 */

	@ColumnInfo(comment="订单编号",type="varchar(200)")
	private String shangpinOrderUuidNumber;

	/**
	 * 商品订单 的 用户
	 */
	@ColumnInfo(comment="用户",type="int(11)")
	private Integer shangpinOrderYonghuId;
	/**
	 * 购买数量
	 */

	@ColumnInfo(comment="购买数量",type="int(11)")
	private Integer buyNumber;
	/**
	 * 实付价格
	 */
	@ColumnInfo(comment="实付价格",type="decimal(10,2)")
	private Double shangpinOrderTruePrice;
	/**
	 * 订单类型
	 */
	@ColumnInfo(comment="订单类型",type="int(11)")
	private Integer shangpinOrderTypes;
	/**
	 * 订单类型的值
	 */
	@ColumnInfo(comment="订单类型的字典表值",type="varchar(200)")
	private String shangpinOrderValue;

	/**
	 * 关联商品名称
	 */
	@ColumnInfo(comment="关联商品名称",type="varchar(200)")
	private String shangpinName;

	/**
	 * 关联商品图片
	 */
	@ColumnInfo(comment="关联商品图片",type="varchar(200)")
	private String shangpinPhoto;

	/**
	 * 关联商品详情
	 */
	@ColumnInfo(comment="关联商品详情",type="longtext")
	private String shangpinContent;

	/**
	 * 关联仓库名称
	 */
	@ColumnInfo(comment="关联仓库名称",type="varchar(200)")
	private String cangkuName;

	//新增：级联表 车辆信息
	/**
	 * 车辆ID
	 */
	@ColumnInfo(comment="车辆ID",type="int(11)")
	private Integer cheliangId;

	/**
	 * 车辆名称
	 */
	@ColumnInfo(comment="车辆名称",type="varchar(200)")
	private String cheliangName;

	/**
	 * 车辆牌照
	 */
	@ColumnInfo(comment="车辆牌照",type="varchar(200)")
	private String cheliangPaizhao;

	/**
	 * 车辆照片
	 */
	@ColumnInfo(comment="车辆照片",type="varchar(200)")
	private String cheliangPhoto;

	//新增：级联表 司机信息
	/**
	 * 司机ID
	 */
	@ColumnInfo(comment="司机ID",type="int(11)")
	private Integer sijiId;

	/**
	 * 司机姓名
	 */
	@ColumnInfo(comment="司机姓名",type="varchar(200)")
	private String sijiName;

	/**
	 * 司机手机号
	 */
	@ColumnInfo(comment="司机手机号",type="varchar(200)")
	private String sijiPhone;

	/**
	 * 司机照片
	 */
	@ColumnInfo(comment="司机照片",type="varchar(200)")
	private String sijiPhoto;

	public HuowuView() {

	}

	public HuowuView(HuowuEntity huowuEntity) {
		try {
			BeanUtils.copyProperties(this, huowuEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//当前表的
	/**
	 * 获取： 货物类型的值
	 */
	public String getHuowuValue() {
		return huowuValue;
	}
	/**
	 * 设置： 货物类型的值
	 */
	public void setHuowuValue(String huowuValue) {
		this.huowuValue = huowuValue;
	}

	//级联表的get和set 商品订单

	/**
	 * 获取： 订单编号
	 */
	public String getShangpinOrderUuidNumber() {
		return shangpinOrderUuidNumber;
	}
	/**
	 * 设置： 订单编号
	 */
	public void setShangpinOrderUuidNumber(String shangpinOrderUuidNumber) {
		this.shangpinOrderUuidNumber = shangpinOrderUuidNumber;
	}
	/**
	 * 获取：商品订单 的 用户
	 */
	public Integer getShangpinOrderYonghuId() {
		return shangpinOrderYonghuId;
	}
	/**
	 * 设置：商品订单 的 用户
	 */
	public void setShangpinOrderYonghuId(Integer shangpinOrderYonghuId) {
		this.shangpinOrderYonghuId = shangpinOrderYonghuId;
	}

	/**
	 * 获取： 购买数量
	 */
	public Integer getBuyNumber() {
		return buyNumber;
	}
	/**
	 * 设置： 购买数量
	 */
	public void setBuyNumber(Integer buyNumber) {
		this.buyNumber = buyNumber;
	}

	/**
	 * 获取： 实付价格
	 */
	public Double getShangpinOrderTruePrice() {
		return shangpinOrderTruePrice;
	}
	/**
	 * 设置： 实付价格
	 */
	public void setShangpinOrderTruePrice(Double shangpinOrderTruePrice) {
		this.shangpinOrderTruePrice = shangpinOrderTruePrice;
	}
	/**
	 * 获取： 订单类型
	 */
	public Integer getShangpinOrderTypes() {
		return shangpinOrderTypes;
	}
	/**
	 * 设置： 订单类型
	 */
	public void setShangpinOrderTypes(Integer shangpinOrderTypes) {
		this.shangpinOrderTypes = shangpinOrderTypes;
	}

	/**
	 * 获取： 订单类型的值
	 */
	public String getShangpinOrderValue() {
		return shangpinOrderValue;
	}
	/**
	 * 设置： 订单类型的值
	 */
	public void setShangpinOrderValue(String shangpinOrderValue) {
		this.shangpinOrderValue = shangpinOrderValue;
	}

	public String getShangpinName() {
		return shangpinName;
	}

	public void setShangpinName(String shangpinName) {
		this.shangpinName = shangpinName;
	}

	public String getShangpinPhoto() {
		return shangpinPhoto;
	}

	public void setShangpinPhoto(String shangpinPhoto) {
		this.shangpinPhoto = shangpinPhoto;
	}

	public String getShangpinContent() {
		return shangpinContent;
	}

	public void setShangpinContent(String shangpinContent) {
		this.shangpinContent = shangpinContent;
	}

	public String getCangkuName() {
		return cangkuName;
	}

	public void setCangkuName(String cangkuName) {
		this.cangkuName = cangkuName;
	}

	//新增：车辆信息的get和set方法
	/**
	 * 获取：车辆ID
	 */
	public Integer getCheliangId() {
		return cheliangId;
	}
	/**
	 * 设置：车辆ID
	 */
	public void setCheliangId(Integer cheliangId) {
		this.cheliangId = cheliangId;
	}

	/**
	 * 获取：车辆名称
	 */
	public String getCheliangName() {
		return cheliangName;
	}
	/**
	 * 设置：车辆名称
	 */
	public void setCheliangName(String cheliangName) {
		this.cheliangName = cheliangName;
	}

	/**
	 * 获取：车辆牌照
	 */
	public String getCheliangPaizhao() {
		return cheliangPaizhao;
	}
	/**
	 * 设置：车辆牌照
	 */
	public void setCheliangPaizhao(String cheliangPaizhao) {
		this.cheliangPaizhao = cheliangPaizhao;
	}

	/**
	 * 获取：车辆照片
	 */
	public String getCheliangPhoto() {
		return cheliangPhoto;
	}
	/**
	 * 设置：车辆照片
	 */
	public void setCheliangPhoto(String cheliangPhoto) {
		this.cheliangPhoto = cheliangPhoto;
	}

	//新增：司机信息的get和set方法
	/**
	 * 获取：司机ID
	 */
	public Integer getSijiId() {
		return sijiId;
	}
	/**
	 * 设置：司机ID
	 */
	public void setSijiId(Integer sijiId) {
		this.sijiId = sijiId;
	}

	/**
	 * 获取：司机姓名
	 */
	public String getSijiName() {
		return sijiName;
	}
	/**
	 * 设置：司机姓名
	 */
	public void setSijiName(String sijiName) {
		this.sijiName = sijiName;
	}

	/**
	 * 获取：司机手机号
	 */
	public String getSijiPhone() {
		return sijiPhone;
	}
	/**
	 * 设置：司机手机号
	 */
	public void setSijiPhone(String sijiPhone) {
		this.sijiPhone = sijiPhone;
	}

	/**
	 * 获取：司机照片
	 */
	public String getSijiPhoto() {
		return sijiPhoto;
	}
	/**
	 * 设置：司机照片
	 */
	public void setSijiPhoto(String sijiPhoto) {
		this.sijiPhoto = sijiPhoto;
	}

	@Override
	public String toString() {
		return "HuowuView{" +
				", huowuValue=" + huowuValue +
				", shangpinOrderUuidNumber=" + shangpinOrderUuidNumber +
				", buyNumber=" + buyNumber +
				", shangpinOrderTruePrice=" + shangpinOrderTruePrice +
				", shangpinName=" + shangpinName +
				", cangkuName=" + cangkuName +
				", cheliangId=" + cheliangId +
				", cheliangName=" + cheliangName +
				", cheliangPaizhao=" + cheliangPaizhao +
				", sijiId=" + sijiId +
				", sijiName=" + sijiName +
				", sijiPhone=" + sijiPhone +
				"} " + super.toString();
	}
}
