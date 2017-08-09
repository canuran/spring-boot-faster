/*
Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001
*/

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for demo_address
-- ----------------------------
DROP TABLE IF EXISTS `demo_address`;
CREATE TABLE `demo_address` (
  `address_id` INT(11) NOT NULL,
  `province`   VARCHAR(128) DEFAULT NULL,
  `city`       VARCHAR(128) DEFAULT NULL,
  `county`     VARCHAR(128) DEFAULT NULL,
  PRIMARY KEY (`address_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of demo_address
-- ----------------------------
INSERT INTO `demo_address` VALUES (1, '广东省', '深圳市', '南山区');
INSERT INTO `demo_address` VALUES (2, '上海市', '上海', '浦东区');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `demo_user`;
CREATE TABLE `demo_user` (
  `user_id`    INT(11)      NOT NULL AUTO_INCREMENT,
  `username`   VARCHAR(128) NOT NULL,
  `password`   VARCHAR(128) NOT NULL,
  `gender`     INT(11)               DEFAULT NULL,
  `birthday`   DATETIME              DEFAULT NULL,
  `address_id` INT(11)               DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_demo_user_address_id` FOREIGN KEY (`address_id`)
  REFERENCES `demo_address` (`address_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `demo_user` VALUES (1, '元宝', 'yb', 1, '2000-01-01 00:00:00', 1);
INSERT INTO `demo_user` VALUES (2, '安娜', 'an', 2, '2001-02-03 00:00:00', 2);

