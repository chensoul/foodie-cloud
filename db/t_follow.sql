DROP TABLE IF EXISTS `t_follow`;
CREATE TABLE `t_follow`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `diner_id` int(11) NULL DEFAULT NULL,
  `follow_diner_id` int(11) NULL DEFAULT NULL,
  `is_valid` tinyint(1) NULL DEFAULT NULL,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_follow_diner_valid`(`follow_diner_id`, `is_valid`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;
