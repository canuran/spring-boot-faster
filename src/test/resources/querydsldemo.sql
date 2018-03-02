/*
Navicat MySQL Data Transfer

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2018-02-06 23:52:26
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for demo_address
-- ----------------------------
DROP TABLE IF EXISTS `demo_address`;
CREATE TABLE `demo_address` (
  `address_id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
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
  `address_id` int(11) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`user_id`),
  KEY `fk_demo_user_address_id` (`address_id`),
  CONSTRAINT `fk_demo_user_address_id` FOREIGN KEY (`address_id`) REFERENCES `demo_address` (`address_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of demo_user
-- ----------------------------
INSERT INTO `demo_user` VALUES ('1', '元宝', 'yb', '1', '3', '2000-01-01 00:00:00');
INSERT INTO `demo_user` VALUES ('2', '安娜', 'an', '2', '2', '2001-02-03 00:00:00');
