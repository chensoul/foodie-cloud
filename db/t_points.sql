DROP TABLE IF EXISTS `t_diner_points`;
CREATE TABLE `t_diner_points`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fk_diner_id` int(11) NULL DEFAULT NULL,
  `points` int(11) NULL DEFAULT NULL COMMENT '积分',
  `types` tinyint(1) NULL DEFAULT NULL COMMENT '类型',
  `create_date` datetime(0) NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  `is_valid` tinyint(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;
