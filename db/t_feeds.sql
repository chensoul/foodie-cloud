DROP TABLE IF EXISTS `t_feed`;
CREATE TABLE `t_feed`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(255)  NULL DEFAULT NULL COMMENT '内容',
  `fk_diner_id` int(11) NULL DEFAULT NULL,
  `praise_amount` int(11) NULL DEFAULT NULL COMMENT '点赞数量',
  `comment_amount` int(11) NULL DEFAULT NULL COMMENT '评论数量',
  `fk_restaurant_id` int(11) NULL DEFAULT NULL,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  `is_valid` tinyint(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;
