/*
Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for authority
-- ----------------------------
DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority` (
  `authority_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `code` varchar(64) NOT NULL,
  `type` varchar(64) NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`authority_id`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `authority_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `authority` (`authority_id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of authority
-- ----------------------------
INSERT INTO `authority` VALUES ('1', '用户管理', 'USER_MANAGE', 'PAGE', '/user', null, '2017-12-23 17:48:53');
INSERT INTO `authority` VALUES ('2', '用户新增', 'USER_ADD', 'ACTION', '/addUser', '1', '2017-12-23 17:49:27');
INSERT INTO `authority` VALUES ('3', '用户修改', 'USER_UPDATE', 'ACTION', '/updateUser', '1', '2017-12-23 17:50:41');
INSERT INTO `authority` VALUES ('4', '用户删除', 'USER_DELETE', 'ACTION', '/deleteUser', '1', '2017-12-23 17:51:03');

-- ----------------------------
-- Table structure for demo_address
-- ----------------------------
DROP TABLE IF EXISTS `demo_address`;
CREATE TABLE `demo_address` (
  `address_id` int(11) NOT NULL,
  `name` varchar(128) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of demo_address
-- ----------------------------
INSERT INTO `demo_address` VALUES ('1', '广东省', null);
INSERT INTO `demo_address` VALUES ('2', '上海市', null);
INSERT INTO `demo_address` VALUES ('3', '深圳市', '1');
INSERT INTO `demo_address` VALUES ('4', '广州市', '1');
INSERT INTO `demo_address` VALUES ('5', '浦东区', '2');

-- ----------------------------
-- Table structure for demo_user
-- ----------------------------
DROP TABLE IF EXISTS `demo_user`;
CREATE TABLE `demo_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(128) NOT NULL,
  `password` varchar(128) NOT NULL,
  `gender` int(11) DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `address_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `fk_demo_user_address_id` (`address_id`),
  CONSTRAINT `fk_demo_user_address_id` FOREIGN KEY (`address_id`) REFERENCES `demo_address` (`address_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of demo_user
-- ----------------------------
INSERT INTO `demo_user` VALUES ('1', '元宝', 'yb', '1', '2000-01-01 00:00:00', '3');
INSERT INTO `demo_user` VALUES ('2', '安娜', 'an', '2', '2001-02-03 00:00:00', '2');

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `permission_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL,
  `name` varchar(64) NOT NULL,
  `code` varchar(64) NOT NULL,
  `type` varchar(64) DEFAULT NULL,
  `target` varchar(512) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`permission_id`),
  KEY `permission_parent_id` (`parent_id`),
  CONSTRAINT `permission_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `permission` (`permission_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of permission
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '用户管理', '2017-08-24 12:07:53');
INSERT INTO `role` VALUES ('2', '用户查看', '2017-12-23 16:25:58');

-- ----------------------------
-- Table structure for role_authority
-- ----------------------------
DROP TABLE IF EXISTS `role_authority`;
CREATE TABLE `role_authority` (
  `role_id` bigint(20) NOT NULL,
  `authority_id` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`,`authority_id`),
  KEY `authority_id` (`authority_id`),
  CONSTRAINT `role_authority_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `role_authority_ibfk_2` FOREIGN KEY (`authority_id`) REFERENCES `authority` (`authority_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_authority
-- ----------------------------
INSERT INTO `role_authority` VALUES ('1', '1', '2017-12-23 17:51:16');
INSERT INTO `role_authority` VALUES ('1', '2', '2017-12-23 17:51:33');
INSERT INTO `role_authority` VALUES ('1', '3', '2017-12-23 17:51:40');
INSERT INTO `role_authority` VALUES ('1', '4', '2017-12-23 17:51:46');
INSERT INTO `role_authority` VALUES ('2', '1', '2017-12-23 17:51:54');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `FK_permission_role` (`permission_id`),
  CONSTRAINT `FK_permission_role` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_role_permission` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_permission
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `password` varchar(32) NOT NULL,
  `gender` varchar(16) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '元宝', 'yb', 'MALE', '2000-02-10', '2017-08-23 18:43:52');
INSERT INTO `user` VALUES ('2', '露娜', 'ln', 'FEMALE', '2002-05-20', '2017-08-24 12:06:02');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `role_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`role_id`,`user_id`),
  KEY `FK_role_user` (`role_id`),
  KEY `FK_user_role` (`user_id`),
  CONSTRAINT `FK_role_user` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_role` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('1', '1', '2017-08-24 12:11:17');
INSERT INTO `user_role` VALUES ('2', '2', '2017-12-23 18:25:46');
