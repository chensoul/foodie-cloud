DROP TABLE IF EXISTS `t_diner_point`;
CREATE TABLE `t_diner_point`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `diner_id` int(11) NULL DEFAULT NULL,
  `point` int(11) NULL DEFAULT NULL COMMENT '积分',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '类型',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;
