/*
Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001
*/

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `role_id` INT(11)      NOT NULL AUTO_INCREMENT,
  `code`    VARCHAR(128) NOT NULL,
  `name`    VARCHAR(128) NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE =InnoDB AUTO_INCREMENT = 2 DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', 'ROLE_USER', '用户');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id`  INT(11)      NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(128) NOT NULL,
  `password` VARCHAR(128) NOT NULL,
  `gender`   INT(11)               DEFAULT NULL,
  `birthday` DATETIME              DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE =InnoDB AUTO_INCREMENT = 3 DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '元宝', 'yb', '1', '2000-01-01 00:00:00');
INSERT INTO `user` VALUES ('2', '安娜', 'an', '2', '2001-02-03 00:00:00');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `user_id` INT(11) NOT NULL,
  `role_id` INT(11) NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  CONSTRAINT `fk_user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
  ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
  ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE =InnoDB DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('1', '1');
