package com.entity;

import com.annotation.ColumnInfo;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.utils.DateUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

@TableName("jiankongyujing")
public class JiankongyujingEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public JiankongyujingEntity() {
    }

    public JiankongyujingEntity(T t) {
        try {
            BeanUtils.copyProperties(this, t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @TableId(type = IdType.AUTO)
    @ColumnInfo(comment = "主键", type = "int(11)")
    @TableField(value = "id")
    private Integer id;

    @ColumnInfo(comment = "预警编号", type = "varchar(200)")
    @TableField(value = "jiankongyujing_uuid_number")
    private String jiankongyujingUuidNumber;

    @ColumnInfo(comment = "预警标题", type = "varchar(200)")
    @TableField(value = "jiankongyujing_name")
    private String jiankongyujingName;

    @ColumnInfo(comment = "监控类型", type = "int(11)")
    @TableField(value = "jiankong_types")
    private Integer jiankongTypes;

    @ColumnInfo(comment = "预警级别", type = "int(11)")
    @TableField(value = "yujing_level_types")
    private Integer yujingLevelTypes;

    @ColumnInfo(comment = "预警状态", type = "int(11)")
    @TableField(value = "yujing_status_types")
    private Integer yujingStatusTypes;

    @ColumnInfo(comment = "预警方式", type = "int(11)")
    @TableField(value = "yujing_mode_types")
    private Integer yujingModeTypes;

    @ColumnInfo(comment = "预警来源", type = "varchar(200)")
    @TableField(value = "yujing_source")
    private String yujingSource;

    @ColumnInfo(comment = "关联用户", type = "int(11)")
    @TableField(value = "yonghu_id")
    private Integer yonghuId;

    @ColumnInfo(comment = "关联订单", type = "int(11)")
    @TableField(value = "shangpin_order_id")
    private Integer shangpinOrderId;

    @ColumnInfo(comment = "监控对象", type = "varchar(200)")
    @TableField(value = "jiankong_object")
    private String jiankongObject;

    @ColumnInfo(comment = "监控数据", type = "varchar(200)")
    @TableField(value = "jiankong_data")
    private String jiankongData;

    @ColumnInfo(comment = "预警阈值", type = "varchar(200)")
    @TableField(value = "yujing_threshold")
    private String yujingThreshold;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    @ColumnInfo(comment = "监控时间", type = "timestamp")
    @TableField(value = "jiankong_time")
    private Date jiankongTime;

    @ColumnInfo(comment = "预警说明", type = "longtext")
    @TableField(value = "yujing_content")
    private String yujingContent;

    @ColumnInfo(comment = "处理说明", type = "longtext")
    @TableField(value = "chuli_content")
    private String chuliContent;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    @ColumnInfo(comment = "处理时间", type = "timestamp")
    @TableField(value = "chuli_time")
    private Date chuliTime;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    @ColumnInfo(comment = "创建时间", type = "timestamp")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJiankongyujingUuidNumber() {
        return jiankongyujingUuidNumber;
    }

    public void setJiankongyujingUuidNumber(String jiankongyujingUuidNumber) {
        this.jiankongyujingUuidNumber = jiankongyujingUuidNumber;
    }

    public String getJiankongyujingName() {
        return jiankongyujingName;
    }

    public void setJiankongyujingName(String jiankongyujingName) {
        this.jiankongyujingName = jiankongyujingName;
    }

    public Integer getJiankongTypes() {
        return jiankongTypes;
    }

    public void setJiankongTypes(Integer jiankongTypes) {
        this.jiankongTypes = jiankongTypes;
    }

    public Integer getYujingLevelTypes() {
        return yujingLevelTypes;
    }

    public void setYujingLevelTypes(Integer yujingLevelTypes) {
        this.yujingLevelTypes = yujingLevelTypes;
    }

    public Integer getYujingStatusTypes() {
        return yujingStatusTypes;
    }

    public void setYujingStatusTypes(Integer yujingStatusTypes) {
        this.yujingStatusTypes = yujingStatusTypes;
    }

    public Integer getYujingModeTypes() {
        return yujingModeTypes;
    }

    public void setYujingModeTypes(Integer yujingModeTypes) {
        this.yujingModeTypes = yujingModeTypes;
    }

    public String getYujingSource() {
        return yujingSource;
    }

    public void setYujingSource(String yujingSource) {
        this.yujingSource = yujingSource;
    }

    public Integer getYonghuId() {
        return yonghuId;
    }

    public void setYonghuId(Integer yonghuId) {
        this.yonghuId = yonghuId;
    }

    public Integer getShangpinOrderId() {
        return shangpinOrderId;
    }

    public void setShangpinOrderId(Integer shangpinOrderId) {
        this.shangpinOrderId = shangpinOrderId;
    }

    public String getJiankongObject() {
        return jiankongObject;
    }

    public void setJiankongObject(String jiankongObject) {
        this.jiankongObject = jiankongObject;
    }

    public String getJiankongData() {
        return jiankongData;
    }

    public void setJiankongData(String jiankongData) {
        this.jiankongData = jiankongData;
    }

    public String getYujingThreshold() {
        return yujingThreshold;
    }

    public void setYujingThreshold(String yujingThreshold) {
        this.yujingThreshold = yujingThreshold;
    }

    public Date getJiankongTime() {
        return jiankongTime;
    }

    public void setJiankongTime(Date jiankongTime) {
        this.jiankongTime = jiankongTime;
    }

    public String getYujingContent() {
        return yujingContent;
    }

    public void setYujingContent(String yujingContent) {
        this.yujingContent = yujingContent;
    }

    public String getChuliContent() {
        return chuliContent;
    }

    public void setChuliContent(String chuliContent) {
        this.chuliContent = chuliContent;
    }

    public Date getChuliTime() {
        return chuliTime;
    }

    public void setChuliTime(Date chuliTime) {
        this.chuliTime = chuliTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Jiankongyujing{" +
                "id=" + id +
                ", jiankongyujingUuidNumber='" + jiankongyujingUuidNumber + '\'' +
                ", jiankongyujingName='" + jiankongyujingName + '\'' +
                ", jiankongTypes=" + jiankongTypes +
                ", yujingLevelTypes=" + yujingLevelTypes +
                ", yujingStatusTypes=" + yujingStatusTypes +
                ", yujingModeTypes=" + yujingModeTypes +
                ", yujingSource='" + yujingSource + '\'' +
                ", yonghuId=" + yonghuId +
                ", shangpinOrderId=" + shangpinOrderId +
                ", jiankongObject='" + jiankongObject + '\'' +
                ", jiankongData='" + jiankongData + '\'' +
                ", yujingThreshold='" + yujingThreshold + '\'' +
                ", jiankongTime=" + DateUtil.convertString(jiankongTime, "yyyy-MM-dd HH:mm:ss") +
                ", yujingContent='" + yujingContent + '\'' +
                ", chuliContent='" + chuliContent + '\'' +
                ", chuliTime=" + DateUtil.convertString(chuliTime, "yyyy-MM-dd HH:mm:ss") +
                ", createTime=" + DateUtil.convertString(createTime, "yyyy-MM-dd HH:mm:ss") +
                '}';
    }
}
