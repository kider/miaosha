-- miaosha.goods definition

CREATE TABLE `goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `goods_name` varchar(16) DEFAULT NULL COMMENT '商品名称',
  `goods_title` varchar(64) DEFAULT NULL COMMENT '商品标题',
  `goods_img` varchar(64) DEFAULT NULL COMMENT '商品的图片',
  `goods_detail` longtext COMMENT '商品的详情介绍',
  `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
  `goods_stock` int(11) DEFAULT '0' COMMENT '商品库存，-1表示没有限制',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;


INSERT INTO miaosha.goods
(id, goods_name, goods_title, goods_img, goods_detail, goods_price, goods_stock)
VALUES(1, 'iphoneX', 'Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机', '/img/iphonex.png', 'Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机', 8765.00, 10000);
INSERT INTO miaosha.goods
(id, goods_name, goods_title, goods_img, goods_detail, goods_price, goods_stock)
VALUES(2, '华为Meta9', '华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '/img/meta10.png', '华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', 3212.00, -1);
INSERT INTO miaosha.goods
(id, goods_name, goods_title, goods_img, goods_detail, goods_price, goods_stock)
VALUES(3, 'iphone8', 'Apple iPhone 8 (A1865) 64GB 银色 移动联通电信4G手机', '/img/iphone8.png', 'Apple iPhone 8 (A1865) 64GB 银色 移动联通电信4G手机', 5589.00, 10000);
INSERT INTO miaosha.goods
(id, goods_name, goods_title, goods_img, goods_detail, goods_price, goods_stock)
VALUES(4, '小米6', '小米6 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '/img/mi6.png', '小米6 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', 3212.00, 10000);



-- miaosha.miaosha_goods definition

CREATE TABLE `miaosha_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品Id',
  `miaosha_price` decimal(10,2) DEFAULT '0.00' COMMENT '秒杀价',
  `stock_count` int(11) DEFAULT NULL COMMENT '库存数量',
  `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;


INSERT INTO miaosha.miaosha_goods
(id, goods_id, miaosha_price, stock_count, start_date, end_date)
VALUES(1, 1, 0.01, 8, '2017-12-04 21:51:23', '2025-12-31 21:51:27');
INSERT INTO miaosha.miaosha_goods
(id, goods_id, miaosha_price, stock_count, start_date, end_date)
VALUES(2, 2, 0.01, 9, '2017-12-04 21:40:14', '2025-12-31 14:00:24');
INSERT INTO miaosha.miaosha_goods
(id, goods_id, miaosha_price, stock_count, start_date, end_date)
VALUES(3, 3, 0.01, 9, '2017-12-04 21:40:14', '2025-12-31 14:00:24');
INSERT INTO miaosha.miaosha_goods
(id, goods_id, miaosha_price, stock_count, start_date, end_date)
VALUES(4, 4, 0.01, 9, '2017-12-04 21:40:14', '2025-12-31 14:00:24');



-- miaosha.miaosha_message definition

CREATE TABLE `miaosha_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息主键',
  `messageid` bigint(20) NOT NULL COMMENT '分布式id',
  `content` text COMMENT '消息内容',
  `create_time` date DEFAULT NULL COMMENT '创建时间',
  `status` int(1) NOT NULL COMMENT '1 有效 2 失效 ',
  `over_time` datetime DEFAULT NULL COMMENT '结束时间',
  `message_type` int(1) DEFAULT '3' COMMENT '0 秒杀消息 1 购买消息 2 推送消息',
  `send_type` int(1) DEFAULT '3' COMMENT '发送类型 0 app 1 pc 2 ios',
  `good_name` varchar(50) DEFAULT '' COMMENT '商品名称',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '商品价格',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- miaosha.miaosha_message_user definition

CREATE TABLE `miaosha_message_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userid` bigint(20) NOT NULL,
  `messageid` bigint(50) NOT NULL,
  `goodid` int(20) DEFAULT NULL,
  `orderid` int(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- miaosha.miaosha_order definition

CREATE TABLE `miaosha_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_uid_gid` (`user_id`,`goods_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- miaosha.miaosha_user definition

CREATE TABLE `miaosha_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID，手机号码',
  `nickname` varchar(255) NOT NULL,
  `password` varchar(32) DEFAULT NULL COMMENT 'MD5(MD5(pass明文+固定salt) + salt)',
  `salt` varchar(10) DEFAULT NULL,
  `head` varchar(128) DEFAULT NULL COMMENT '头像，云存储的ID',
  `register_date` datetime DEFAULT NULL COMMENT '注册时间',
  `last_login_date` datetime DEFAULT NULL COMMENT '上蔟登录时间',
  `login_count` int(11) DEFAULT '0' COMMENT '登录次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18912341246 DEFAULT CHARSET=utf8mb4;

INSERT INTO miaosha.miaosha_user
(id, nickname, password, salt, head, register_date, last_login_date, login_count)
VALUES(18912341238, '18612766138', 'f96894b685dc0126cb5181a9a2659589', '1a2b3c4d', NULL, '2019-01-09 17:08:16', NULL, 0);
INSERT INTO miaosha.miaosha_user
(id, nickname, password, salt, head, register_date, last_login_date, login_count)
VALUES(18912341239, '18612766139', 'f96894b685dc0126cb5181a9a2659589', '1a2b3c4d', NULL, '2019-01-09 17:17:21', NULL, 0);
INSERT INTO miaosha.miaosha_user
(id, nickname, password, salt, head, register_date, last_login_date, login_count)
VALUES(18912341240, '18612766139', 'f96894b685dc0126cb5181a9a2659589', '1a2b3c4d', NULL, '2019-01-11 11:35:39', NULL, 0);
INSERT INTO miaosha.miaosha_user
(id, nickname, password, salt, head, register_date, last_login_date, login_count)
VALUES(18912341241, '18612766141', 'f96894b685dc0126cb5181a9a2659589', '1a2b3c4d', NULL, '2019-01-11 11:36:23', NULL, 0);
INSERT INTO miaosha.miaosha_user
(id, nickname, password, salt, head, register_date, last_login_date, login_count)
VALUES(18912341242, '18612766145', 'f96894b685dc0126cb5181a9a2659589', '1a2b3c4d', NULL, '2019-01-11 11:38:29', NULL, 0);
INSERT INTO miaosha.miaosha_user
(id, nickname, password, salt, head, register_date, last_login_date, login_count)
VALUES(18912341243, '18612766122', 'f96894b685dc0126cb5181a9a2659589', '1a2b3c4d', NULL, '2019-01-11 11:41:52', NULL, 0);
INSERT INTO miaosha.miaosha_user
(id, nickname, password, salt, head, register_date, last_login_date, login_count)
VALUES(18912341244, '18612766133', 'f96894b685dc0126cb5181a9a2659589', '1a2b3c4d', NULL, '2019-01-11 11:43:24', NULL, 0);
INSERT INTO miaosha.miaosha_user
(id, nickname, password, salt, head, register_date, last_login_date, login_count)
VALUES(18912341245, '18612766444', 'f96894b685dc0126cb5181a9a2659589', '1a2b3c4d', NULL, '2019-01-11 13:44:29', NULL, 0);


-- miaosha.order_info definition

CREATE TABLE `order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  `delivery_addr_id` bigint(20) DEFAULT NULL COMMENT '收获地址ID',
  `goods_name` varchar(16) DEFAULT NULL COMMENT '冗余过来的商品名称',
  `goods_count` int(11) DEFAULT '0' COMMENT '商品数量',
  `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
  `order_channel` tinyint(4) DEFAULT '0' COMMENT '1pc，2android，3ios',
  `status` tinyint(4) DEFAULT '0' COMMENT '订单状态，0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
  `create_date` datetime DEFAULT NULL COMMENT '订单的创建时间',
  `pay_date` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;