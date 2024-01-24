DROP TABLE IF EXISTS `t_follow`;
CREATE TABLE `t_follow`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `diner_id` int(11) NULL DEFAULT NULL,
  `follow_diner_id` int(11) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_follow_diner`(`follow_diner_id`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;


DROP TABLE IF EXISTS `t_feed`;
CREATE TABLE `t_feed`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(255)  NULL DEFAULT NULL COMMENT '内容',
  `diner_id` int(11) NULL DEFAULT NULL,
  `praise_amount` int(11) NULL DEFAULT NULL COMMENT '点赞数量',
  `comment_amount` int(11) NULL DEFAULT NULL COMMENT '评论数量',
  `restaurant_id` int(11) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = DYNAMIC;
