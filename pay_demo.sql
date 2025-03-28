/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50741 (5.7.41)
 Source Host           : localhost:3306
 Source Schema         : pay_demo

 Target Server Type    : MySQL
 Target Server Version : 50741 (5.7.41)
 File Encoding         : 65001

 Date: 14/10/2024 19:19:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for pay_order_info
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_info`;
CREATE TABLE `pay_order_info` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `order_no` varchar(50) NOT NULL COMMENT '商户订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `product_id` bigint(20) NOT NULL COMMENT '支付产品id',
  `quantity` decimal(10,0) NOT NULL COMMENT '数量',
  `code_url` varchar(255) DEFAULT NULL COMMENT '二维码链接(微信支付)',
  `original_total_fee` decimal(10,0) NOT NULL COMMENT '订单金额（分）原价',
  `total_fee` decimal(10,0) NOT NULL COMMENT '订单金额(分)',
  `product_name` varchar(20) DEFAULT NULL COMMENT '支付产品名称',
  `prepay_id` varchar(50) DEFAULT NULL COMMENT '预支付交易会话标识（微信支付）',
  `valid_ts` bigint(50) DEFAULT NULL COMMENT '有效截止时间 毫秒时间戳',
  `order_status` int(10) NOT NULL COMMENT '订单状态',
  `payer_mode` varchar(24) NOT NULL COMMENT '支付方式 微信支付wxPay,支付宝支付aliPay等等',
  `trade_type` varchar(64) NOT NULL COMMENT '交易类型',
  `client_type` int(10) NOT NULL DEFAULT '0' COMMENT '下单客户端 1:Web端 2:移动端',
  `is_handle` int(10) NOT NULL DEFAULT '0' COMMENT ' 0未处理 1已处理',
  `pay_load` varchar(100) DEFAULT NULL COMMENT '订单携带参数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '修改者',
  `del_flag` bigint(50) NOT NULL DEFAULT '0' COMMENT '删除标记 0表示存在 非0标识删除',
  `tenant_id` varchar(50) NOT NULL COMMENT '租户标识',
  `delivery_fail_cause` text COMMENT '发货失败原因',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no_unique` (`order_no`,`del_flag`) USING BTREE COMMENT '订单号唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- Records of pay_order_info
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for pay_payment_info
-- ----------------------------
DROP TABLE IF EXISTS `pay_payment_info`;
CREATE TABLE `pay_payment_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '支付记录id',
  `order_no` varchar(50) NOT NULL COMMENT '商户订单编号',
  `transaction_id` varchar(50) DEFAULT NULL COMMENT '微信支付订单号(微信支付)',
  `trade_type` varchar(20) DEFAULT NULL COMMENT '交易类型',
  `trade_state` int(10) NOT NULL DEFAULT '0' COMMENT '交易状态',
  `payer_total` decimal(10,0) DEFAULT NULL COMMENT '支付金额(分)',
  `payer_time` bigint(50) DEFAULT NULL COMMENT '支付动作时间',
  `success_time` datetime DEFAULT NULL COMMENT '支付完成时间',
  `payer_mode` varchar(24) NOT NULL COMMENT '微信支付wxPay,支付宝支付alipay',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '修改者',
  `del_flag` bigint(50) NOT NULL DEFAULT '0' COMMENT '删除标记 0表示存在 非0标识删除',
  `tenant_id` varchar(255) DEFAULT NULL COMMENT '租户标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no_unique_index` (`order_no`,`del_flag`) USING BTREE COMMENT '订单唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录';

-- ----------------------------
-- Records of pay_payment_info
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for pay_payment_log
-- ----------------------------
DROP TABLE IF EXISTS `pay_payment_log`;
CREATE TABLE `pay_payment_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一Id',
  `payment_id` bigint(20) DEFAULT NULL COMMENT '支付记录Id',
  `content` text COMMENT '日志内容',
  `process_txt` varchar(64) DEFAULT NULL COMMENT '流程',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '修改者',
  `del_flag` bigint(50) DEFAULT '0' COMMENT '删除标记 0表示存在 非0标识删除',
  `tenant_id` varchar(50) DEFAULT NULL COMMENT '租户标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付日志表';

-- ----------------------------
-- Records of pay_payment_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for pay_refund_info
-- ----------------------------
DROP TABLE IF EXISTS `pay_refund_info`;
CREATE TABLE `pay_refund_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '退款单id',
  `order_no` varchar(50) NOT NULL COMMENT '商户订单编号',
  `refund_no` varchar(50) DEFAULT NULL COMMENT '商户退款单编号',
  `refund_id` varchar(50) DEFAULT NULL COMMENT '支付系统退款单号',
  `total_fee` int(11) DEFAULT NULL COMMENT '原订单金额(分)',
  `refund` int(11) DEFAULT NULL COMMENT '退款金额(分)',
  `reason` varchar(50) DEFAULT NULL COMMENT '退款原因',
  `refund_status` varchar(10) DEFAULT NULL COMMENT '退款状态',
  `content_return` text COMMENT '申请退款返回参数',
  `content_notify` text COMMENT '退款结果通知参数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint(50) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint(50) DEFAULT NULL COMMENT '修改者',
  `del_flag` bigint(50) NOT NULL DEFAULT '0' COMMENT '删除标记 0表示存在 非0标识删除',
  `tenant_id` varchar(50) NOT NULL COMMENT '租户标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款表';

-- ----------------------------
-- Records of pay_refund_info
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_pay_mode_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_pay_mode_config`;
CREATE TABLE `sys_pay_mode_config` (
  `id` int(11) NOT NULL COMMENT '唯一标识符',
  `mode` varchar(24) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '支付方式,例如:wxPay、aliPay...',
  `mode_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '支付方式名称,例如:微信支付/支付宝支付….',
  `mode_icon` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '支付方式logo',
  `is_open` int(1) NOT NULL DEFAULT '0' COMMENT '支付渠道状态: 0开启 1关闭',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '修改者',
  `del_flag` bigint(50) NOT NULL DEFAULT '0' COMMENT '删除标记 0表示存在 非0标识删除',
  `handler_bean_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '处理器Bean',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `mode_unique_index` (`mode`,`del_flag`) USING BTREE COMMENT '支付方式唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='支付方式配置表';

-- ----------------------------
-- Records of sys_pay_mode_config
-- ----------------------------
BEGIN;
INSERT INTO `sys_pay_mode_config` (`id`, `mode`, `mode_name`, `mode_icon`, `is_open`, `create_time`, `create_by`, `update_time`, `update_by`, `del_flag`, `handler_bean_name`) VALUES (1, 'wxPay', '微信支付', NULL, 0, '2023-08-14 15:53:14', NULL, NULL, NULL, 0, 'weChatPayHandler');
COMMIT;

-- ----------------------------
-- Table structure for sys_pay_mode_handler_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_pay_mode_handler_config`;
CREATE TABLE `sys_pay_mode_handler_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一标识符',
  `mode` varchar(12) COLLATE utf8_bin NOT NULL COMMENT '支付方式,例如:wxPay、aliPay...',
  `operation` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '操作',
  `operation_desc` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '操作描述',
  `method_name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '方法名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '修改者',
  `del_flag` bigint(50) NOT NULL DEFAULT '0' COMMENT '删除标记 0表示存在 非0标识删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `mode_operation_unique_index` (`mode`,`operation`,`del_flag`) USING BTREE COMMENT '支付场景操作处理器唯一'
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='支付操作处理器配置表';

-- ----------------------------
-- Records of sys_pay_mode_handler_config
-- ----------------------------
BEGIN;
INSERT INTO `sys_pay_mode_handler_config` (`id`, `mode`, `operation`, `operation_desc`, `method_name`, `create_time`, `create_by`, `update_time`, `update_by`, `del_flag`) VALUES (1, 'wxPay', 'CREATE', '创建预支付订单', 'createPreOrder', '2023-08-14 16:17:45', NULL, NULL, NULL, 0);
INSERT INTO `sys_pay_mode_handler_config` (`id`, `mode`, `operation`, `operation_desc`, `method_name`, `create_time`, `create_by`, `update_time`, `update_by`, `del_flag`) VALUES (2, 'wxPay', 'CLOSE', '关闭订单', 'cancelOrder', '2023-08-15 15:48:09', NULL, NULL, NULL, 0);
INSERT INTO `sys_pay_mode_handler_config` (`id`, `mode`, `operation`, `operation_desc`, `method_name`, `create_time`, `create_by`, `update_time`, `update_by`, `del_flag`) VALUES (3, 'wxPay', 'QUERY_STATE', '查询订单状态', 'queryOrderStateInfo', '2023-08-18 18:02:31', NULL, NULL, NULL, 0);
INSERT INTO `sys_pay_mode_handler_config` (`id`, `mode`, `operation`, `operation_desc`, `method_name`, `create_time`, `create_by`, `update_time`, `update_by`, `del_flag`) VALUES (4, 'wxPay', 'CALL_UP', '调起支付', 'callUpPayment', '2023-08-18 18:02:34', NULL, NULL, NULL, 0);
INSERT INTO `sys_pay_mode_handler_config` (`id`, `mode`, `operation`, `operation_desc`, `method_name`, `create_time`, `create_by`, `update_time`, `update_by`, `del_flag`) VALUES (5, 'wxPay', 'CHECK', '检查订单状态', 'checkOrderState', '2023-08-18 18:03:00', NULL, NULL, NULL, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_pay_param_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_pay_param_config`;
CREATE TABLE `sys_pay_param_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一id',
  `name` varchar(255) NOT NULL COMMENT '配置项名称',
  `value` varchar(255) NOT NULL COMMENT '配置项值',
  `mode` varchar(64) NOT NULL COMMENT '微信支付wechatpay,支付宝支付alipay',
  `del_flag` bigint(50) NOT NULL DEFAULT '0' COMMENT '删除标记 0表示存在 非0标识删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='支付参数配置表';

-- ----------------------------
-- Records of sys_pay_param_config
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_pay_product
-- ----------------------------
DROP TABLE IF EXISTS `sys_pay_product`;
CREATE TABLE `sys_pay_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `name` varchar(20) DEFAULT NULL COMMENT '商品名称',
  `introduce` varchar(255) DEFAULT NULL COMMENT '介绍',
  `introduce_subsidiary` varchar(255) DEFAULT NULL COMMENT '产品附属介绍',
  `is_link` int(1) NOT NULL DEFAULT '2' COMMENT '1:超链接 2非超链接',
  `link_url` varchar(255) DEFAULT NULL COMMENT '超链接地址',
  `type` int(11) DEFAULT NULL COMMENT '产品所属模块 1:产品大类1 2:产品大类2 3:产品大类3',
  `delivery_pay_load` varchar(255) DEFAULT NULL COMMENT '发货时会用到的参数',
  `unit` varchar(255) DEFAULT NULL COMMENT '单位（展示作用）比如1年/1月/1日/1个',
  `children_type` int(1) DEFAULT NULL COMMENT '子类型',
  `price_fen` decimal(10,0) DEFAULT NULL COMMENT '价格（分）实际付款价格',
  `original_price_fen` decimal(10,0) DEFAULT NULL COMMENT '价格(分)营销价格 - 仅做展示',
  `sort` int(10) NOT NULL COMMENT '排序',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '修改者',
  `is_show` int(10) NOT NULL COMMENT '1展示 2隐藏',
  `del_flag` bigint(50) NOT NULL DEFAULT '0' COMMENT '删除标记 0表示存在 非0标识删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- Records of sys_pay_product
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_pay_product_module
-- ----------------------------
DROP TABLE IF EXISTS `sys_pay_product_module`;
CREATE TABLE `sys_pay_product_module` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一Id',
  `type` int(10) DEFAULT NULL COMMENT '产品模块 1:产品大类1 2:产品大类2 3:产品大类3',
  `module_name` varchar(50) DEFAULT NULL COMMENT '产品模块名称',
  `title` varchar(50) DEFAULT NULL COMMENT '产品模块名称',
  `introduce` varchar(50) DEFAULT NULL COMMENT '产品模块介绍',
  `sort` int(10) DEFAULT NULL COMMENT '排序',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '修改者',
  `is_show` int(1) NOT NULL COMMENT '1:展示 2隐藏',
  `del_flag` bigint(50) DEFAULT '0' COMMENT '删除标记 0表示存在 非0标识删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='商品模块表';

-- ----------------------------
-- Records of sys_pay_product_module
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
