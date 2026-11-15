/*
SQLyog Ultimate v11.3 (64 bit)
MySQL - 5.7.32-log : Database - wuliuguanlixitong
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`wuliuguanlixitong` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `shengxianguanlixitong`;

/*Table structure for table `address` */

DROP TABLE IF EXISTS `address`;

CREATE TABLE `address` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
  `yonghu_id` int(20) NOT NULL COMMENT '创建用户',
  `address_name` varchar(200) NOT NULL COMMENT '收货人 ',
  `address_phone` varchar(200) NOT NULL COMMENT '电话 ',
  `address_dizhi` varchar(200) NOT NULL COMMENT '地址 ',
  `isdefault_types` int(11) NOT NULL COMMENT '是否默认地址 ',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 show3',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='收货地址';

/*Data for the table `address` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `cangku` */

DROP TABLE IF EXISTS `cangku`;

CREATE TABLE `cangku` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
  `cangku_name` varchar(200) DEFAULT NULL COMMENT '仓库名称  Search111 ',
  `cangku_uuid_number` varchar(200) DEFAULT NULL COMMENT '仓库编号',
  `cangku_photo` varchar(200) DEFAULT NULL COMMENT '仓库照片',
  `cangku_address` varchar(200) DEFAULT NULL COMMENT '仓库地点',
  `cangku_types` int(11) DEFAULT NULL COMMENT '仓库类型 Search111',
  `cangku_content` longtext COMMENT '仓库介绍 ',
  `cangku_delete` int(11) DEFAULT NULL COMMENT '逻辑删除',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '录入时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间  show1 show2 photoShow',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='仓库';

/*Data for the table `cangku` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `chat` */

DROP TABLE IF EXISTS `chat`;

CREATE TABLE `chat` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `yonghu_id` int(11) DEFAULT NULL COMMENT '提问用户',
  `chat_issue` varchar(200) DEFAULT NULL COMMENT '问题',
  `issue_time` timestamp NULL DEFAULT NULL COMMENT '问题时间 Search111',
  `chat_reply` varchar(200) DEFAULT NULL COMMENT '回复',
  `reply_time` timestamp NULL DEFAULT NULL COMMENT '回复时间 Search111',
  `zhuangtai_types` int(255) DEFAULT NULL COMMENT '状态',
  `chat_types` int(11) DEFAULT NULL COMMENT '数据类型',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='反馈信息';

/*Data for the table `chat` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `cheliang` */

DROP TABLE IF EXISTS `cheliang`;

CREATE TABLE `cheliang` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
  `cheliang_name` varchar(200) DEFAULT NULL COMMENT '车辆名称  Search111 ',
  `cheliang_uuid_number` varchar(200) DEFAULT NULL COMMENT '车辆编号',
  `cheliang_photo` varchar(200) DEFAULT NULL COMMENT '车辆照片',
  `cheliang_paizhao` varchar(200) DEFAULT NULL COMMENT '车辆牌照',
  `cheliang_types` int(11) DEFAULT NULL COMMENT '车辆类型 Search111',
  `cheliang_content` longtext COMMENT '车辆介绍 ',
  `cheliang_delete` int(11) DEFAULT NULL COMMENT '逻辑删除',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '录入时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间  ',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='车辆';

/*Data for the table `cheliang` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `config` */

DROP TABLE IF EXISTS `config`;

CREATE TABLE `config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '配置参数名称',
  `value` varchar(100) DEFAULT NULL COMMENT '配置参数值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='配置文件';

/*Data for the table `config` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `dictionary` */

DROP TABLE IF EXISTS `dictionary`;

CREATE TABLE `dictionary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dic_code` varchar(200) DEFAULT NULL COMMENT '字段',
  `dic_name` varchar(200) DEFAULT NULL COMMENT '字段名',
  `code_index` int(11) DEFAULT NULL COMMENT '编码',
  `index_name` varchar(200) DEFAULT NULL COMMENT '编码名字  Search111 ',
  `super_id` int(11) DEFAULT NULL COMMENT '父字段id',
  `beizhu` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8 COMMENT='字典';

/*Data for the table `dictionary` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `gonggao` */

DROP TABLE IF EXISTS `gonggao`;

CREATE TABLE `gonggao` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
  `gonggao_name` varchar(200) DEFAULT NULL COMMENT '公告名称 Search111  ',
  `gonggao_photo` varchar(200) DEFAULT NULL COMMENT '公告图片 ',
  `gonggao_types` int(11) NOT NULL COMMENT '公告类型 Search111 ',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '发布时间',
  `gonggao_content` longtext COMMENT '公告详情 ',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间 show1 show2 nameShow',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='公告';

/*Data for the table `gonggao` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `huowu` */

DROP TABLE IF EXISTS `huowu`;

CREATE TABLE `huowu` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
  `shangpin_order_id` int(11) DEFAULT NULL COMMENT '商家',
  `huowu_name` varchar(200) DEFAULT NULL COMMENT '货物名称  Search111 ',
  `huowu_uuid_number` varchar(200) DEFAULT NULL COMMENT '货物编号',
  `huowu_types` int(11) DEFAULT NULL COMMENT '货物类型 Search111',
  `huowu_content` longtext COMMENT '货物介绍 ',
  `huowu_delete` int(11) DEFAULT NULL COMMENT '逻辑删除',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '录入时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间  show3 listShow',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='货物';

/*Data for the table `huowu` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `shangpin` */

DROP TABLE IF EXISTS `shangpin`;

CREATE TABLE `shangpin` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
  `cangku_id` int(11) DEFAULT NULL COMMENT '仓库',
  `shangpin_name` varchar(200) DEFAULT NULL COMMENT '商品名称  Search111 ',
  `shangpin_uuid_number` varchar(200) DEFAULT NULL COMMENT '商品编号',
  `shangpin_photo` varchar(200) DEFAULT NULL COMMENT '商品照片',
  `shangpin_types` int(11) DEFAULT NULL COMMENT '商品类型 Search111',
  `shangpin_kucun_number` int(11) DEFAULT NULL COMMENT '商品库存',
  `shangpin_new_money` decimal(10,2) DEFAULT NULL COMMENT '现价/积分 ',
  `shangpin_content` longtext COMMENT '商品介绍 ',
  `shangxia_types` int(11) DEFAULT NULL COMMENT '是否上架 ',
  `shangpin_delete` int(11) DEFAULT NULL COMMENT '逻辑删除',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '录入时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间  show1 show2 photoShow',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='商品';

/*Data for the table `shangpin` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `shangpin_churu_inout` */

DROP TABLE IF EXISTS `shangpin_churu_inout`;

CREATE TABLE `shangpin_churu_inout` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shangpin_churu_inout_uuid_number` varchar(200) DEFAULT NULL COMMENT '出入库流水号',
  `shangpin_churu_inout_name` varchar(200) DEFAULT NULL COMMENT '出入库名称  Search111 ',
  `shangpin_churu_inout_types` int(11) DEFAULT NULL COMMENT '出入库类型  Search111 ',
  `shangpin_churu_inout_content` longtext COMMENT '备注',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '添加时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='出入库';

/*Data for the table `shangpin_churu_inout` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `shangpin_churu_inout_list` */

DROP TABLE IF EXISTS `shangpin_churu_inout_list`;

CREATE TABLE `shangpin_churu_inout_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shangpin_churu_inout_id` int(11) DEFAULT NULL COMMENT '出入库',
  `shangpin_id` int(11) DEFAULT NULL COMMENT '商品',
  `cangku_id` int(11) DEFAULT NULL COMMENT '仓库',
  `shangpin_churu_inout_list_number` int(11) DEFAULT NULL COMMENT '操作数量',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '操作时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='出入库详情';

/*Data for the table `shangpin_churu_inout_list` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `shangpin_collection` */

DROP TABLE IF EXISTS `shangpin_collection`;

CREATE TABLE `shangpin_collection` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shangpin_id` int(11) DEFAULT NULL COMMENT '商品',
  `yonghu_id` int(11) DEFAULT NULL COMMENT '用户',
  `shangpin_collection_types` int(11) DEFAULT NULL COMMENT '类型',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '收藏时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间 show3 photoShow',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='商品收藏';

/*Data for the table `shangpin_collection` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `shangpin_order` */

DROP TABLE IF EXISTS `shangpin_order`;

CREATE TABLE `shangpin_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shangpin_order_uuid_number` varchar(200) DEFAULT NULL COMMENT '订单编号 Search111 ',
  `address_id` int(11) DEFAULT NULL COMMENT '收货地址 ',
  `shangpin_id` int(11) DEFAULT NULL COMMENT '商品',
  `cheliang_id` int(11) DEFAULT NULL COMMENT '车辆',
  `siji_id` int(11) DEFAULT NULL COMMENT '司机',
  `yonghu_id` int(11) DEFAULT NULL COMMENT '用户',
  `buy_number` int(11) DEFAULT NULL COMMENT '购买数量',
  `shangpin_order_true_price` decimal(10,2) DEFAULT NULL COMMENT '实付价格',
  `shangpin_order_types` int(11) DEFAULT NULL COMMENT '订单类型 Search111 ',
  `insert_time` timestamp NULL DEFAULT NULL COMMENT '订单创建时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间 show3 listShow',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='商品订单';

/*Data for the table `shangpin_order` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `siji` */

DROP TABLE IF EXISTS `siji`;

CREATE TABLE `siji` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `siji_uuid_number` varchar(200) DEFAULT NULL COMMENT '司机编号 Search111 ',
  `siji_name` varchar(200) DEFAULT NULL COMMENT '司机姓名 Search111 ',
  `siji_phone` varchar(200) DEFAULT NULL COMMENT '司机手机号',
  `siji_id_number` varchar(200) DEFAULT NULL COMMENT '司机身份证号',
  `siji_photo` varchar(200) DEFAULT NULL COMMENT '司机头像',
  `sex_types` int(11) DEFAULT NULL COMMENT '性别',
  `siji_email` varchar(200) DEFAULT NULL COMMENT '司机邮箱',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='司机';

/*Data for the table `siji` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `token` */

DROP TABLE IF EXISTS `token`;

CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userid` bigint(20) NOT NULL COMMENT '儿童id',
  `username` varchar(100) NOT NULL COMMENT '儿童名',
  `tablename` varchar(100) DEFAULT NULL COMMENT '表名',
  `role` varchar(100) DEFAULT NULL COMMENT '角色',
  `token` varchar(200) NOT NULL COMMENT '密码',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `expiratedtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='token表';

/*Data for the table `token` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(100) NOT NULL COMMENT '儿童名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `role` varchar(100) DEFAULT '管理员' COMMENT '角色',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='管理员';

/*Data for the table `users` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `yonghu` */

DROP TABLE IF EXISTS `yonghu`;

CREATE TABLE `yonghu` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(200) DEFAULT NULL COMMENT '账户',
  `password` varchar(200) DEFAULT NULL COMMENT '密码',
  `yonghu_uuid_number` varchar(200) DEFAULT NULL COMMENT '用户编号 Search111 ',
  `yonghu_name` varchar(200) DEFAULT NULL COMMENT '用户姓名 Search111 ',
  `yonghu_phone` varchar(200) DEFAULT NULL COMMENT '用户手机号',
  `yonghu_id_number` varchar(200) DEFAULT NULL COMMENT '用户身份证号',
  `yonghu_photo` varchar(200) DEFAULT NULL COMMENT '用户头像',
  `sex_types` int(11) DEFAULT NULL COMMENT '性别',
  `new_money` decimal(10,2) DEFAULT NULL COMMENT '余额',
  `yonghu_email` varchar(200) DEFAULT NULL COMMENT '用户邮箱',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='用户';

/*Data for the table `yonghu` */

-- Data rows omitted from the public source snapshot.


/*Table structure for table `yuangong` */

DROP TABLE IF EXISTS `yuangong`;

CREATE TABLE `yuangong` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(200) DEFAULT NULL COMMENT '账户',
  `password` varchar(200) DEFAULT NULL COMMENT '密码',
  `yuangong_uuid_number` varchar(200) DEFAULT NULL COMMENT '员工编号 Search111 ',
  `yuangong_name` varchar(200) DEFAULT NULL COMMENT '员工姓名 Search111 ',
  `yuangong_phone` varchar(200) DEFAULT NULL COMMENT '员工手机号',
  `yuangong_id_number` varchar(200) DEFAULT NULL COMMENT '员工身份证号',
  `yuangong_photo` varchar(200) DEFAULT NULL COMMENT '员工头像',
  `sex_types` int(11) DEFAULT NULL COMMENT '性别',
  `yuangong_email` varchar(200) DEFAULT NULL COMMENT '员工邮箱',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='员工';

/*Data for the table `yuangong` */

-- Data rows omitted from the public source snapshot.


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;



USE `shengxianguanlixitong`;

START TRANSACTION;

UPDATE `config`
SET `value` = CASE `id`
                  WHEN 1 THEN 'upload/cangku12.jpg'
                  WHEN 2 THEN 'upload/cangku13.jpg'
                  WHEN 3 THEN 'upload/cangku14.jpg'
                  ELSE `value`
    END
WHERE `id` IN (1, 2, 3);

UPDATE `shangpin`
SET `shangpin_photo` = CASE `id`
                           WHEN 1 THEN 'upload/cangku1.jpg'
                           WHEN 2 THEN 'upload/cangku2.jpg'
                           WHEN 3 THEN 'upload/cangku3.jpg'
                           WHEN 4 THEN 'upload/cangku4.jpg'
                           WHEN 5 THEN 'upload/cangku5.jpg'
                           WHEN 6 THEN 'upload/cangku6.jpg'
                           WHEN 7 THEN 'upload/cangku7.jpg'
                           WHEN 8 THEN 'upload/cangku8.jpg'
                           WHEN 9 THEN 'upload/cangku9.jpg'
                           WHEN 10 THEN 'upload/cangku10.jpg'
                           WHEN 11 THEN 'upload/cangku11.jpg'
                           WHEN 12 THEN 'upload/cangku12.jpg'
                           WHEN 13 THEN 'upload/cangku13.jpg'
                           WHEN 14 THEN 'upload/cangku14.jpg'
                           ELSE `shangpin_photo`
    END
WHERE `id` BETWEEN 1 AND 14;

UPDATE `gonggao`
SET `gonggao_photo` = CASE `id`
                          WHEN 1 THEN 'upload/cangku14.jpg'
                          WHEN 2 THEN 'upload/cangku13.jpg'
                          WHEN 3 THEN 'upload/cangku12.jpg'
                          WHEN 4 THEN 'upload/cangku11.jpg'
                          WHEN 5 THEN 'upload/cangku10.jpg'
                          WHEN 6 THEN 'upload/cangku9.jpg'
                          WHEN 7 THEN 'upload/cangku8.jpg'
                          WHEN 8 THEN 'upload/cangku7.jpg'
                          WHEN 9 THEN 'upload/cangku6.jpg'
                          WHEN 10 THEN 'upload/cangku5.jpg'
                          WHEN 11 THEN 'upload/cangku4.jpg'
                          WHEN 12 THEN 'upload/cangku3.jpg'
                          WHEN 13 THEN 'upload/cangku2.jpg'
                          WHEN 14 THEN 'upload/cangku1.jpg'
                          ELSE `gonggao_photo`
    END
WHERE `id` BETWEEN 1 AND 14;

UPDATE `cheliang`
SET `cheliang_photo` = CASE `id`
                           WHEN 1 THEN 'upload/cangku9.jpg'
                           WHEN 2 THEN 'upload/cangku10.jpg'
                           WHEN 3 THEN 'upload/cangku11.jpg'
                           WHEN 4 THEN 'upload/cangku12.jpg'
                           WHEN 5 THEN 'upload/cangku13.jpg'
                           WHEN 6 THEN 'upload/cangku14.jpg'
                           WHEN 7 THEN 'upload/cangku1.jpg'
                           WHEN 8 THEN 'upload/cangku2.jpg'
                           WHEN 9 THEN 'upload/cangku3.jpg'
                           WHEN 10 THEN 'upload/cangku4.jpg'
                           WHEN 11 THEN 'upload/cangku5.jpg'
                           WHEN 12 THEN 'upload/cangku6.jpg'
                           WHEN 13 THEN 'upload/cangku7.jpg'
                           WHEN 14 THEN 'upload/cangku8.jpg'
                           ELSE `cheliang_photo`
    END
WHERE `id` BETWEEN 1 AND 14;

COMMIT;



USE `shengxianguanlixitong`;

START TRANSACTION;


UPDATE `config`
SET `value` = CASE `id`
                  WHEN 1 THEN 'upload/cangku12.jpg'
                  WHEN 2 THEN 'upload/cangku13.jpg'
                  WHEN 3 THEN 'upload/cangku14.jpg'
                  ELSE `value`
    END
WHERE `id` IN (1,2,3);

UPDATE `cangku`
SET
    `cangku_name` = CASE `id`
                        WHEN 1 THEN '华东生鲜预冷库'
                        WHEN 2 THEN '冷冻水产周转库'
                        WHEN 3 THEN '果蔬气调保鲜库'
                        WHEN 4 THEN '乳制品低温分拣库'
                        WHEN 5 THEN '冷鲜肉恒温暂存库'
                        WHEN 6 THEN '中央冷链集配库'
                        WHEN 7 THEN '区域冷藏中转库'
                        WHEN 8 THEN '低温包装作业库'
                        WHEN 9 THEN '冷链夜配出货库'
                        WHEN 10 THEN '生鲜电商前置仓'
                        WHEN 11 THEN '冷藏应急保障库'
                        WHEN 12 THEN '温湿度监控示范库'
                        WHEN 13 THEN '全程追溯标准库'
                        WHEN 14 THEN '冷链预警联动中心库'
                        ELSE `cangku_name`
        END,
    `cangku_address` = CASE `id`
                           WHEN 1 THEN '上海冷链园区A区'
                           WHEN 2 THEN '宁波港冷链物流区'
                           WHEN 3 THEN '杭州果蔬冷储中心'
                           WHEN 4 THEN '苏州乳品配送园'
                           WHEN 5 THEN '南京鲜肉冷藏基地'
                           WHEN 6 THEN '合肥综合冷链园'
                           WHEN 7 THEN '嘉兴区域中转站'
                           WHEN 8 THEN '无锡低温包装中心'
                           WHEN 9 THEN '湖州夜配运营中心'
                           WHEN 10 THEN '常州前置仓片区'
                           WHEN 11 THEN '南通保障仓储区'
                           WHEN 12 THEN '绍兴监测示范园'
                           WHEN 13 THEN '金华追溯运营区'
                           WHEN 14 THEN '上海冷链调度中心'
                           ELSE `cangku_address`
        END,
    `cangku_content` = CASE `id`
                           WHEN 1 THEN '用于生鲜入库后的快速预冷处理，重点监测入库温度、停留时长和批次状态。'
                           WHEN 2 THEN '用于冷冻水产的低温周转与发运，适合展示冷链温控与库存追踪场景。'
                           WHEN 3 THEN '用于果蔬保鲜和气调存储，支持温湿度趋势监控和异常预警。'
                           WHEN 4 THEN '用于乳制品低温分拣与复核，强调冷链作业标准化和出库校验。'
                           WHEN 5 THEN '用于冷鲜肉短时暂存和品质保障，适配库内温控告警展示。'
                           WHEN 6 THEN '承担多品类生鲜集中入库、分拨和配送调度任务。'
                           WHEN 7 THEN '服务区域线路中转，适合展示冷链干线到城配的衔接流程。'
                           WHEN 8 THEN '主要完成低温包装、标签追溯和发运前复核。'
                           WHEN 9 THEN '面向夜间配送波次出货，适合展示预警联动和订单出库效率。'
                           WHEN 10 THEN '贴近终端门店和社区配送，突出前置仓库存与保鲜监控。'
                           WHEN 11 THEN '用于保供和异常订单兜底处理，支持紧急调拨展示。'
                           WHEN 12 THEN '用于演示仓内温湿度采集、曲线展示和阈值告警。'
                           WHEN 13 THEN '用于演示批次追踪、扫码出入库和全链路追溯。'
                           WHEN 14 THEN '用于冷链监控大屏、异常预警处置和联动调度。'
                           ELSE `cangku_content`
        END
WHERE `id` BETWEEN 1 AND 14;

UPDATE `cheliang`
SET
    `cheliang_name` = CASE `id`
                          WHEN 1 THEN '冷链配送车A01'
                          WHEN 2 THEN '冷藏厢式车A02'
                          WHEN 3 THEN '生鲜干线车A03'
                          WHEN 4 THEN '社区冷配车A04'
                          WHEN 5 THEN '水产冷运车A05'
                          WHEN 6 THEN '果蔬保鲜车A06'
                          WHEN 7 THEN '乳品配送车A07'
                          WHEN 8 THEN '夜配冷链车A08'
                          WHEN 9 THEN '区域中转车A09'
                          WHEN 10 THEN '应急保障车A10'
                          WHEN 11 THEN '低温转运车A11'
                          WHEN 12 THEN '冷库接驳车A12'
                          WHEN 13 THEN '标准冷藏车A13'
                          WHEN 14 THEN '预警联动车A14'
                          ELSE `cheliang_name`
        END,
    `cheliang_paizhao` = CASE `id`
                             WHEN 1 THEN '沪A-COLD01'
                             WHEN 2 THEN '沪A-COLD02'
                             WHEN 3 THEN '浙B-COLD03'
                             WHEN 4 THEN '苏C-COLD04'
                             WHEN 5 THEN '浙D-COLD05'
                             WHEN 6 THEN '皖A-COLD06'
                             WHEN 7 THEN '苏B-COLD07'
                             WHEN 8 THEN '沪B-COLD08'
                             WHEN 9 THEN '浙A-COLD09'
                             WHEN 10 THEN '苏A-COLD10'
                             WHEN 11 THEN '皖B-COLD11'
                             WHEN 12 THEN '沪C-COLD12'
                             WHEN 13 THEN '浙C-COLD13'
                             WHEN 14 THEN '苏D-COLD14'
                             ELSE `cheliang_paizhao`
        END,
    `cheliang_content` = CASE `id`
                             WHEN 1 THEN '服务门店与前置仓，重点监测车厢温度和到仓时效。'
                             WHEN 2 THEN '适用于低温短驳与城际配送，支持冷链途中异常预警。'
                             WHEN 3 THEN '承担跨区域干线运输任务，关注全程温控与轨迹回传。'
                             WHEN 4 THEN '主要负责社区订单冷链配送，适合展示末端配送监管。'
                             WHEN 5 THEN '用于冷冻水产运输，强调低温稳定与交接签收。'
                             WHEN 6 THEN '用于果蔬保鲜配送，关注湿度波动与装载规范。'
                             WHEN 7 THEN '主要配送巴氏奶和酸奶等乳品。'
                             WHEN 8 THEN '服务夜间波次订单，适合展示夜配预警场景。'
                             WHEN 9 THEN '用于中转站到区域仓的短线运输。'
                             WHEN 10 THEN '用于预警异常时的应急补位与紧急调拨。'
                             WHEN 11 THEN '适用于库间调拨和短距离低温转运。'
                             WHEN 12 THEN '负责冷库到分拣区的接驳运输。'
                             WHEN 13 THEN '标准化冷藏运输车辆，适合系统演示。'
                             WHEN 14 THEN '与监控预警联动，展示异常处置闭环。'
                             ELSE `cheliang_content`
        END,
    `cheliang_photo` = CASE `id`
                           WHEN 1 THEN 'upload/cangku9.jpg'
                           WHEN 2 THEN 'upload/cangku10.jpg'
                           WHEN 3 THEN 'upload/cangku11.jpg'
                           WHEN 4 THEN 'upload/cangku12.jpg'
                           WHEN 5 THEN 'upload/cangku13.jpg'
                           WHEN 6 THEN 'upload/cangku14.jpg'
                           WHEN 7 THEN 'upload/cangku1.jpg'
                           WHEN 8 THEN 'upload/cangku2.jpg'
                           WHEN 9 THEN 'upload/cangku3.jpg'
                           WHEN 10 THEN 'upload/cangku4.jpg'
                           WHEN 11 THEN 'upload/cangku5.jpg'
                           WHEN 12 THEN 'upload/cangku6.jpg'
                           WHEN 13 THEN 'upload/cangku7.jpg'
                           WHEN 14 THEN 'upload/cangku8.jpg'
                           ELSE `cheliang_photo`
        END
WHERE `id` BETWEEN 1 AND 14;

UPDATE `shangpin`
SET
    `shangpin_name` = CASE `id`
                          WHEN 1 THEN '三文鱼冷链批次'
                          WHEN 2 THEN '冷鲜牛肉批次'
                          WHEN 3 THEN '蓝莓保鲜批次'
                          WHEN 4 THEN '车厘子冷藏批次'
                          WHEN 5 THEN '巴氏鲜奶批次'
                          WHEN 6 THEN '冷冻虾仁批次'
                          WHEN 7 THEN '鲜切蔬菜批次'
                          WHEN 8 THEN '冷鲜鸡胸肉批次'
                          WHEN 9 THEN '鳕鱼段冷冻批次'
                          WHEN 10 THEN '酸奶配送批次'
                          WHEN 11 THEN '荔枝保鲜批次'
                          WHEN 12 THEN '冷链豆制品批次'
                          WHEN 13 THEN '冰鲜带鱼批次'
                          WHEN 14 THEN '预警演示货品批次'
                          ELSE `shangpin_name`
        END,
    `shangpin_content` = CASE `id`
                             WHEN 1 THEN '用于展示进口冷链水产从入库到分拨的温控监管流程。'
                             WHEN 2 THEN '用于展示冷鲜肉在0到4摄氏度区间的冷藏周转。'
                             WHEN 3 THEN '用于展示浆果类生鲜的保鲜时效和温湿度监测。'
                             WHEN 4 THEN '用于展示高值水果的低温运输和异常预警。'
                             WHEN 5 THEN '用于展示乳品冷链配送过程中的温度连续监测。'
                             WHEN 6 THEN '用于展示冷冻海鲜的库内周转和出入库记录。'
                             WHEN 7 THEN '用于展示净菜加工后的冷藏配送流程。'
                             WHEN 8 THEN '用于展示冷鲜禽肉的短保质期冷链作业。'
                             WHEN 9 THEN '用于展示冷冻鱼类产品的批次追溯管理。'
                             WHEN 10 THEN '用于展示乳品订单配送和签收过程。'
                             WHEN 11 THEN '用于展示易损水果在冷链中的保鲜监控。'
                             WHEN 12 THEN '用于展示豆制品低温配送与库存预警。'
                             WHEN 13 THEN '用于展示水产类货品从冷库到门店的全程追踪。'
                             WHEN 14 THEN '用于系统首页和订单页的冷链预警演示数据。'
                             ELSE `shangpin_content`
        END,
    `shangpin_photo` = CASE `id`
                           WHEN 1 THEN 'upload/cangku1.jpg'
                           WHEN 2 THEN 'upload/cangku2.jpg'
                           WHEN 3 THEN 'upload/cangku3.jpg'
                           WHEN 4 THEN 'upload/cangku4.jpg'
                           WHEN 5 THEN 'upload/cangku5.jpg'
                           WHEN 6 THEN 'upload/cangku6.jpg'
                           WHEN 7 THEN 'upload/cangku7.jpg'
                           WHEN 8 THEN 'upload/cangku8.jpg'
                           WHEN 9 THEN 'upload/cangku9.jpg'
                           WHEN 10 THEN 'upload/cangku10.jpg'
                           WHEN 11 THEN 'upload/cangku11.jpg'
                           WHEN 12 THEN 'upload/cangku12.jpg'
                           WHEN 13 THEN 'upload/cangku13.jpg'
                           WHEN 14 THEN 'upload/cangku14.jpg'
                           ELSE `shangpin_photo`
        END
WHERE `id` BETWEEN 1 AND 14;

UPDATE `gonggao`
SET
    `gonggao_name` = CASE `id`
                         WHEN 1 THEN '冷库温度异常预警通知'
                         WHEN 2 THEN '夜间配送车辆超时提醒'
                         WHEN 3 THEN '生鲜批次追溯演练公告'
                         WHEN 4 THEN '冷链设备巡检计划发布'
                         WHEN 5 THEN '门店签收温度复核通知'
                         WHEN 6 THEN '乳品配送温控标准更新'
                         WHEN 7 THEN '水产到仓时效预警通报'
                         WHEN 8 THEN '前置仓库存阈值提醒'
                         WHEN 9 THEN '节假日冷链保供安排'
                         WHEN 10 THEN '冷库除霜作业安全提示'
                         WHEN 11 THEN '运输轨迹回传异常说明'
                         WHEN 12 THEN '冷链订单高峰期调度公告'
                         WHEN 13 THEN '温湿度采集设备维护通知'
                         WHEN 14 THEN '预警处置闭环复盘通报'
                         ELSE `gonggao_name`
        END,
    `gonggao_content` = CASE `id`
                            WHEN 1 THEN '监控平台检测到部分冷库温度接近阈值，请值班人员及时排查制冷设备运行状态。'
                            WHEN 2 THEN '部分冷链车辆在夜间波次配送中存在超时风险，请调度中心关注到店签收时效。'
                            WHEN 3 THEN '本周将开展生鲜冷链批次追溯演练，验证扫码、出入库、预警联动流程。'
                            WHEN 4 THEN '请各仓库按计划完成冷库门封、温度探头和制冷机组巡检并上传结果。'
                            WHEN 5 THEN '门店签收时请核验货品中心温度、外包装状态和到货时间。'
                            WHEN 6 THEN '乳品类订单执行2到6摄氏度配送标准，异常情况需及时上报。'
                            WHEN 7 THEN '水产批次到仓时间已接近预警阈值，请中转仓做好接车准备。'
                            WHEN 8 THEN '前置仓部分生鲜SKU库存偏低，请及时补货并关注温控状态。'
                            WHEN 9 THEN '节假日订单高峰期间启用保供车辆和备用冷库，确保冷链不断链。'
                            WHEN 10 THEN '冷库除霜期间请做好货品转移和温度补采，避免数据断点。'
                            WHEN 11 THEN '运输轨迹回传异常的车辆需在收车后进行终端检查和补传。'
                            WHEN 12 THEN '订单高峰期请优先保障冷鲜、乳品和水产类配送时效。'
                            WHEN 13 THEN '温湿度采集设备维护期间请启用备用探头，保障监测连续性。'
                            WHEN 14 THEN '本期复盘聚焦温度告警、车辆超时和签收异常三类问题的处置闭环。'
                            ELSE `gonggao_content`
        END,
    `gonggao_photo` = CASE `id`
                          WHEN 1 THEN 'upload/cangku14.jpg'
                          WHEN 2 THEN 'upload/cangku13.jpg'
                          WHEN 3 THEN 'upload/cangku12.jpg'
                          WHEN 4 THEN 'upload/cangku11.jpg'
                          WHEN 5 THEN 'upload/cangku10.jpg'
                          WHEN 6 THEN 'upload/cangku9.jpg'
                          WHEN 7 THEN 'upload/cangku8.jpg'
                          WHEN 8 THEN 'upload/cangku7.jpg'
                          WHEN 9 THEN 'upload/cangku6.jpg'
                          WHEN 10 THEN 'upload/cangku5.jpg'
                          WHEN 11 THEN 'upload/cangku4.jpg'
                          WHEN 12 THEN 'upload/cangku3.jpg'
                          WHEN 13 THEN 'upload/cangku2.jpg'
                          WHEN 14 THEN 'upload/cangku1.jpg'
                          ELSE `gonggao_photo`
        END
WHERE `id` BETWEEN 1 AND 14;

UPDATE `siji`
SET `siji_photo` = CASE `id`
                       WHEN 1 THEN 'upload/cangku1.jpg'
                       WHEN 2 THEN 'upload/cangku2.jpg'
                       WHEN 3 THEN 'upload/cangku3.jpg'
                       WHEN 4 THEN 'upload/cangku4.jpg'
                       WHEN 5 THEN 'upload/cangku5.jpg'
                       WHEN 6 THEN 'upload/cangku6.jpg'
                       WHEN 7 THEN 'upload/cangku7.jpg'
                       WHEN 8 THEN 'upload/cangku8.jpg'
                       WHEN 9 THEN 'upload/cangku9.jpg'
                       WHEN 10 THEN 'upload/cangku10.jpg'
                       WHEN 11 THEN 'upload/cangku11.jpg'
                       WHEN 12 THEN 'upload/cangku12.jpg'
                       WHEN 13 THEN 'upload/cangku13.jpg'
                       WHEN 14 THEN 'upload/cangku14.jpg'
                       ELSE `siji_photo`
    END
WHERE `id` BETWEEN 1 AND 14;

UPDATE `yuangong`
SET `yuangong_photo` = CASE `id`
                           WHEN 1 THEN 'upload/cangku12.jpg'
                           WHEN 2 THEN 'upload/cangku13.jpg'
                           WHEN 3 THEN 'upload/cangku14.jpg'
                           ELSE `yuangong_photo`
    END
WHERE `id` BETWEEN 1 AND 3;

UPDATE `yonghu`
SET `yonghu_photo` = CASE `id`
                         WHEN 1 THEN 'upload/cangku9.jpg'
                         WHEN 2 THEN 'upload/cangku10.jpg'
                         WHEN 3 THEN 'upload/cangku11.jpg'
                         ELSE `yonghu_photo`
    END
WHERE `id` BETWEEN 1 AND 3;

COMMIT;

-- If cached images remain, clear browser cache and restart backend service.
/*
  监控预警功能补充 SQL（全量版）
  使用方式：
  1. 先执行原始 db.sql
  2. 再执行本文件
*/

USE `shengxianguanlixitong`;

DROP TABLE IF EXISTS `jiankongyujing`;

CREATE TABLE `jiankongyujing` (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `jiankongyujing_uuid_number` varchar(200) DEFAULT NULL COMMENT '预警编号',
                                  `jiankongyujing_name` varchar(200) DEFAULT NULL COMMENT '预警标题',
                                  `jiankong_types` int(11) DEFAULT NULL COMMENT '监控类型',
                                  `yujing_level_types` int(11) DEFAULT NULL COMMENT '预警级别',
                                  `yujing_status_types` int(11) DEFAULT NULL COMMENT '预警状态',
                                  `yujing_mode_types` int(11) DEFAULT NULL COMMENT '预警方式',
                                  `yujing_source` varchar(200) DEFAULT NULL COMMENT '预警来源',
                                  `jiankong_object` varchar(200) DEFAULT NULL COMMENT '监控对象',
                                  `jiankong_data` varchar(200) DEFAULT NULL COMMENT '监控数据',
                                  `yujing_threshold` varchar(200) DEFAULT NULL COMMENT '预警阈值',
                                  `jiankong_time` timestamp NULL DEFAULT NULL COMMENT '监控时间',
                                  `yujing_content` longtext COMMENT '预警说明',
                                  `chuli_content` longtext COMMENT '处理说明',
                                  `chuli_time` timestamp NULL DEFAULT NULL COMMENT '处理时间',
                                  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='监控预警';

-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


-- Data rows omitted from the public source snapshot.


USE `shengxianguanlixitong`;

ALTER TABLE `jiankongyujing`
    ADD COLUMN `yonghu_id` int(11) DEFAULT NULL COMMENT '关联用户' AFTER `yujing_source`;

ALTER TABLE `jiankongyujing`
    ADD COLUMN `shangpin_order_id` int(11) DEFAULT NULL COMMENT '关联订单' AFTER `yonghu_id`;

ALTER TABLE `jiankongyujing`
    ADD INDEX `idx_jiankongyujing_yonghu_id` (`yonghu_id`);

ALTER TABLE `jiankongyujing`
    ADD INDEX `idx_jiankongyujing_shangpin_order_id` (`shangpin_order_id`);

UPDATE `jiankongyujing` j
    INNER JOIN `shangpin_order` o
    ON REPLACE(j.`jiankong_object`, '订单', '') = o.`shangpin_order_uuid_number`
SET j.`shangpin_order_id` = o.`id`,
    j.`yonghu_id` = o.`yonghu_id`
WHERE j.`jiankong_types` = 4
  AND (j.`shangpin_order_id` IS NULL OR j.`yonghu_id` IS NULL);



