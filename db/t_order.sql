DROP TABLE IF EXISTS `t_seckill_voucher`;
CREATE TABLE `t_seckill_voucher`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `voucher_id` int(11) NULL DEFAULT NULL,
  `amount` int(11) NULL DEFAULT NULL,
  `start_time` datetime(0) NULL DEFAULT NULL,
  `end_time` datetime(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `t_voucher`;
CREATE TABLE `t_voucher`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 NULL DEFAULT NULL COMMENT '代金券标题',
  `thumbnail` varchar(255) CHARACTER SET utf8 NULL DEFAULT NULL COMMENT '缩略图',
  `amount` int(11) NULL DEFAULT NULL COMMENT '抵扣金额',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '售价',
  `status` int(10) NULL DEFAULT NULL COMMENT '-1=过期 0=下架 1=上架',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `redeem_restaurant_id` int(10) NULL DEFAULT NULL COMMENT '验证餐厅',
  `stock` int(11) NULL DEFAULT 0 COMMENT '库存',
  `stock_left` int(11) NULL DEFAULT 0 COMMENT '剩余数量',
  `description` varchar(255) CHARACTER SET utf8 NULL DEFAULT NULL COMMENT '描述信息',
  `clause` varchar(255) CHARACTER SET utf8  NULL DEFAULT NULL COMMENT '使用条款',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `t_voucher_order`;
CREATE TABLE `t_voucher_order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` int(11) NULL DEFAULT NULL,
  `voucher_id` int(11) NULL DEFAULT NULL,
  `diner_id` int(11) NULL DEFAULT NULL,
  `qrcode` varchar(255) CHARACTER SET utf8mb4  NULL DEFAULT NULL COMMENT '图片地址',
  `payment` tinyint(4) NULL DEFAULT NULL COMMENT '0=微信支付 1=支付宝支付',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '订单状态：-1=已取消 0=未支付 1=已支付 2=已消费 3=已过期',
  `seckill_id` int(11) NULL DEFAULT NULL COMMENT '如果是抢购订单时，抢购订单的id',
  `order_type` int(11) NULL DEFAULT NULL COMMENT '订单类型：0=正常订单 1=抢购订单',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;
