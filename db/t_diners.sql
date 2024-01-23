
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_diners
-- ----------------------------
DROP TABLE IF EXISTS `t_diners`;
CREATE TABLE `t_diners` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像',
  `roles` varchar(255) DEFAULT '' COMMENT '角色',
  `is_valid` tinyint(1) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_diners
-- ----------------------------
INSERT INTO `t_diners` VALUES ('1', 'abc', 'test', '13888888888', 'abc@imooc.com', 'e10adc3949ba59abbe56e057f20f883e', null, 'ROLE_USER', '1', '2020-11-06 16:17:52', '2020-11-06 16:17:55');
INSERT INTO `t_diners` VALUES ('2', 'test', 'test', '13666666666', null, 'e10adc3949ba59abbe56e057f20f883e', null, 'User_ROLE', '1', '2020-11-12 12:01:13', '2020-11-12 12:01:13');

