CREATE TABLE `t_restaurant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL ,
  `cnname` varchar(100) DEFAULT NULL,
  `x` double DEFAULT NULL,
  `y` double DEFAULT NULL,
  `location` varchar(200) DEFAULT NULL ,
  `cnlocation` varchar(255) DEFAULT NULL,
  `area` varchar(100) DEFAULT NULL,
  `telephone` varchar(50) DEFAULT NULL ,
  `email` varchar(80) DEFAULT NULL,
  `website` varchar(100) DEFAULT NULL,
  `cuisine` varchar(80) DEFAULT NULL,
  `average_price` varchar(20) DEFAULT NULL,
  `introduction` varchar(200) DEFAULT NULL ,
  `thumbnail` varchar(120) DEFAULT NULL ,
  `like_votes` int(10) DEFAULT '0' ,
  `dislike_votes` int(10) DEFAULT '0' ,
  `city_id` int(11) DEFAULT '21' ,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

INSERT INTO `t_restaurant` (`id`, `name`, `cnname`, `x`, `y`, `location`, `cnlocation`, `area`, `telephone`, `email`, `website`, `cuisine`, `average_price`, `introduction`, `thumbnail`, `like_votes`, `dislike_votes`, `city_id`, `create_time`, `update_time`) VALUES
(1, 'aaO Cafe', 'ef44d7e409b1900d06d534eb5f76ddea', 0, 0, NULL, NULL, 'Delivery Only', '1d62cc07fcf38cc95b79f23f6e69daa5', NULL, 'http://www.millenniumhongqiao.com/', '3b261136e3c33f35e0a58611b1f344cb', '¥¥¥¥', 'only by ask', 'restaurant/1/restaurant/160_160/14007358490915826.JPG', 10, 3, 21, '2014-05-04 19:26:28', NULL),
(2, '1 Oz 3', 'eec00cf85502a9c9ea438e5d06e76801', 31.19063, 121.38893, 'd7baf5ca725f83be9f850dfd850b9715', 'b69a4453dab0bfa395f7296ff585e007', 'Changning.Gubei/Hongqiao', '0d4733fec502609ac9437c8fce61f593', NULL, 'http://www.1oz3.com', '604f00b2a37ed296dc0ac7772133f910', '¥', '', 'restaurant/2/restaurant/160_160/13978952798502774.jpg', 1, 2, 21, '2014-05-04 19:26:28', NULL),
(3, '1% Free', '4150072cfb00c81dfb428b5dbe93b5cb', 31.20411, 121.42981, '6da1cc934037650c4a27b377086ca423', 'dabf19fcb4dd9494a750f6589af7ca46', 'Changning', '43294c2e4a694d9ee1bd2f49d5c1f584', NULL, 'http://www.onepercentchina.com', 'adc69293e8fd256b2609664f1e11cb53', '¥¥', '', 'restaurant/3/restaurant/160_160/14158771596727247.PNG', 1, 0, 21, '2014-05-04 19:26:28', NULL),
(4, '100 Century Avenue Restaurant', '626a499b61cb2a9cfe52a96359f718d0', 31.234301, 121.508003, 'e653dffdab83593822e78922ae00106d', '7446a950be436c45c757d27f49771809', 'Pudong.Lujiazui', '5d79504e1fbfd2389debf301ebf759de', NULL, 'http://www.shanghai.park.hyatt.com/en/hotel/dining/100CenturyAvenueRestaurant.html', 'adc69293e8fd256b2609664f1e11cb53', '¥¥', '', 'restaurant/4/restaurant/1456205671899', 35, 10, 21, '2014-05-04 19:26:28', NULL),
(5, '1001 Nights', '46ad3d305e428b65e277d1eb8357fb7e', 31.205593, 121.446617, '8eec6330344eb3af80f87c227d3943a4', '0d6545ebe49c8b93de15951cfae24107', 'Xuhui.Fmr French Concession', 'd44b714ad72f4f1fe95486f6ab857384', NULL, 'www.1001nights.com.cn', 'd69e720f88f84d8f1375b4cb407953e3', '¥¥', '', 'restaurant/5/restaurant/160_160/14047090874476778.JPG', 49, 10, 21, '2014-05-04 19:26:28', NULL),
(6, 'Twelve Hengshan', '1cf1b64a5de0f3af76f2b261226f456b', 31.2048510271162, 121.446577384097, '94dfb01430efbbe3d3c9156afc1c1843', 'c25840e41f3bbd58cb5cbdcc7751ad45', 'Xuhui.Fmr French Concession', 'f03c3b98bb79a27fc8abad946b07dc1c', NULL, '', 'bbe39f69eae2c7b5d0c9cc5d97d1b07e', '¥¥¥', '', 'restaurant/6/restaurant/160_160/14068028597053701.JPG', 13, 3, 21, '2014-05-04 19:26:28', NULL),
(7, '1221', '1d72310edc006dadf2190caad5802983', 31.210114, 121.429588, '298f1b3af7bc74f5c56724f631d26646', '8ea0bfdcf6b30eeb7576146f6273a562', 'Changning.Zhongshan Park', '493ea3faec55e28d6b4f29555e4b46aa', NULL, '', '7c13bdec80f2fa947cd4eeffef3af496', '¥¥', '', '/restaurant/7/restaurant/160_160/14089538911233432.JPG', 83, 18, 21, '2014-05-04 19:26:28', NULL),
(8, '1228', 'eb86d510361fc23b59f18c1bc9802cc6', 31.22928, 121.45137, '7459b19ebd2d07ad090e8027e12dbe20', '220b2a2de2ed8d9eaa3b21c0a265c84b', 'Jing\'an', 'b8e6e662b0f6f614d787743fdd7834ae', NULL, '', '8c7c345ce2a67662e02244778dae2f59', '¥¥¥', '', '', 0, 0, 21, '2014-05-04 19:26:28', NULL),
(9, '12th Day House', '42e161d18dd1c0a85f8441f0043cfc82', 31.2217269959389, 121.456173103007, 'ff5538fe4cf9ecbae901decc1ba19b71', '4f5a2edbb3370336e26379612e111753', 'Jing\'an', '8d13d05ee942a2fdde96c1c876d6c120', NULL, 'site.douban.com/twelveth-day', '604f00b2a37ed296dc0ac7772133f910', '¥', '', 'restaurant/9/restaurant/160_160/1399622675708.jpg', 1, 0, 21, '2014-05-04 19:26:28', NULL),
(10, '1515 West Chophouse & Bar', 'fd7e1d136e6828b9de57c2bb4a023589', 31.2231263769307, 121.450764942217, 'e4cf488d76ace8ebe9931d8bb3ecbb3b', 'a4320f7f08e9719284c31dd0d7a45aed', 'Jing\'an', 'b20b13db629cceea9b7fbe8aec8c482a', NULL, 'www.jinganshangdining.com', 'b3d9d34478a74ae8d56bd8e005c19b03', '¥¥¥¥', ' ', 'restaurant/10/restaurant1501163737942', 120, 32, 21, '2014-05-04 19:26:28', NULL);

