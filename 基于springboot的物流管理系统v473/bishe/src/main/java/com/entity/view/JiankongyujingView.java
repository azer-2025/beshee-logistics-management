package com.entity.view;

import com.annotation.ColumnInfo;
import com.baomidou.mybatisplus.annotations.TableName;
import com.entity.JiankongyujingEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

@TableName("jiankongyujing")
public class JiankongyujingView extends JiankongyujingEntity<JiankongyujingEntity> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ColumnInfo(comment = "监控类型值", type = "varchar(200)")
    private String jiankongValue;

    @ColumnInfo(comment = "预警级别值", type = "varchar(200)")
    private String yujingLevelValue;

    @ColumnInfo(comment = "预警状态值", type = "varchar(200)")
    private String yujingStatusValue;

    @ColumnInfo(comment = "预警方式值", type = "varchar(200)")
    private String yujingModeValue;

    @ColumnInfo(comment = "关联订单编号", type = "varchar(200)")
    private String shangpinOrderUuidNumber;

    @ColumnInfo(comment = "关联用户名", type = "varchar(200)")
    private String yonghuName;

    public JiankongyujingView() {
    }

    public JiankongyujingView(JiankongyujingEntity jiankongyujingEntity) {
        try {
            BeanUtils.copyProperties(this, jiankongyujingEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public String getJiankongValue() {
        return jiankongValue;
    }

    public void setJiankongValue(String jiankongValue) {
        this.jiankongValue = jiankongValue;
    }

    public String getYujingLevelValue() {
        return yujingLevelValue;
    }

    public void setYujingLevelValue(String yujingLevelValue) {
        this.yujingLevelValue = yujingLevelValue;
    }

    public String getYujingStatusValue() {
        return yujingStatusValue;
    }

    public void setYujingStatusValue(String yujingStatusValue) {
        this.yujingStatusValue = yujingStatusValue;
    }

    public String getYujingModeValue() {
        return yujingModeValue;
    }

    public void setYujingModeValue(String yujingModeValue) {
        this.yujingModeValue = yujingModeValue;
    }

    public String getShangpinOrderUuidNumber() {
        return shangpinOrderUuidNumber;
    }

    public void setShangpinOrderUuidNumber(String shangpinOrderUuidNumber) {
        this.shangpinOrderUuidNumber = shangpinOrderUuidNumber;
    }

    public String getYonghuName() {
        return yonghuName;
    }

    public void setYonghuName(String yonghuName) {
        this.yonghuName = yonghuName;
    }

    @Override
    public String toString() {
        return "JiankongyujingView{" +
                "jiankongValue='" + jiankongValue + '\'' +
                ", yujingLevelValue='" + yujingLevelValue + '\'' +
                ", yujingStatusValue='" + yujingStatusValue + '\'' +
                ", yujingModeValue='" + yujingModeValue + '\'' +
                ", shangpinOrderUuidNumber='" + shangpinOrderUuidNumber + '\'' +
                ", yonghuName='" + yonghuName + '\'' +
                "} " + super.toString();
    }
}
