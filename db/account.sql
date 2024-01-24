DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像',
  `roles` varchar(255) DEFAULT '' COMMENT '角色',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

INSERT INTO `account` VALUES ('1', 'abc', 'test', '13888888888', 'abc@imooc.com', '{noop}123456', null, 'USER',  '2020-11-06 16:17:52', '2020-11-06 16:17:55');
INSERT INTO `account` VALUES ('2', 'test', 'test', '13666666666', null, '{noop}123456', null, 'USER', '2020-11-12 12:01:13', '2020-11-12 12:01:13');
