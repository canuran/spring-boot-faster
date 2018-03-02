/*
Navicat MySQL Data Transfer

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2018-02-06 23:52:10
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of authority
-- ----------------------------
INSERT INTO `authority` VALUES ('1', '用户权限', 'USER_AUTHORITY', 'MENU', '<a class=\"waves-effect\">\r\n  <i class=\"fa fa-user-secret\"></i>用户权限\r\n  <i class=\"expand-menu fa fa-caret-right\"></i>\r\n</a>', null, '2017-12-23 17:51:03');
INSERT INTO `authority` VALUES ('2', '用户管理', 'USER_MANAGE', 'PAGE', '<a class=\"waves-effect\" onclick=\"addTab(\'用户管理\', \'user/user.html\')\">\r\n  <i class=\"fa fa-users\"></i>用户管理\r\n</a>', '1', '2017-12-23 17:48:53');
INSERT INTO `authority` VALUES ('3', '用户新增', 'USER_ADD', 'ACTION', null, '2', '2017-12-23 17:49:27');
INSERT INTO `authority` VALUES ('4', '用户修改', 'USER_UPDATE', 'ACTION', null, '2', '2017-12-23 17:50:41');
INSERT INTO `authority` VALUES ('5', '用户删除', 'USER_DELETE', 'ACTION', null, '2', '2017-12-23 17:51:03');
INSERT INTO `authority` VALUES ('6', '角色管理', 'ROLE_MANAGE', 'PAGE', '<a class=\"waves-effect\" onclick=\"addTab(\'角色管理\', \'security/role.html\')\">\n  <i class=\"fa fa-user-circle\"></i>角色管理\n</a>', '1', '2017-12-23 17:48:53');
INSERT INTO `authority` VALUES ('7', '角色新增', 'ROLE_ADD', 'ACTION', null, '6', '2017-12-23 17:49:27');
INSERT INTO `authority` VALUES ('8', '角色修改', 'ROLE_UPDATE', 'ACTION', null, '6', '2017-12-23 17:50:41');
INSERT INTO `authority` VALUES ('9', '角色删除', 'ROLE_DELETE', 'ACTION', null, '6', '2017-12-23 17:51:03');
INSERT INTO `authority` VALUES ('10', '公共资源', 'COMMON_RESOURCE', 'MENU', '<a class=\"waves-effect\">\r\n  <i class=\"fa fa-th-large\"></i>公共资源\r\n  <i class=\"expand-menu fa fa-caret-right\"></i>\r\n</a>', null, '2018-01-16 18:25:55');
INSERT INTO `authority` VALUES ('11', '数据字典', 'DICTIONARY_MANAGE', 'PAGE', '<a class=\"waves-effect\" onclick=\"addTab(\'数据字典\', \'common/dictionary.html\')\">\r\n  <i class=\"fa fa-th-list\"></i>数据字典\r\n</a>', '10', '2018-01-16 21:26:55');
INSERT INTO `authority` VALUES ('12', '接口文档', 'API_DOCUMENT', 'PAGE', '<a class=\"waves-effect\" href=\"swagger-ui.html\" target=\"_blank\">\r\n  <i class=\"fa fa-file-text\"></i>接口文档\r\n</a>', null, '2018-01-16 22:28:04');
INSERT INTO `authority` VALUES ('13', '权限管理', 'AUTHORITY_MANAGE', 'PAGE', '<a class=\"waves-effect\" onclick=\"addTab(\'权限管理\', \'security/authority.html\')\">\n  <i class=\"fa fa-key\"></i>权限管理\n</a>', '1', '2017-12-23 17:48:55');
INSERT INTO `authority` VALUES ('14', '权限新增', 'AUTHORITY_ADD', 'ACTION', null, '13', '2018-01-22 22:24:53');
INSERT INTO `authority` VALUES ('15', '权限修改', 'AUTHORITY_UPDATE', 'ACTION', null, '13', '2018-01-22 21:27:00');
INSERT INTO `authority` VALUES ('16', '权限删除', 'AUTHORITY_DELETE', 'ACTION', null, '13', '2018-01-22 22:28:16');
INSERT INTO `authority` VALUES ('17', '字典项新增', 'DICTIONARY_ADD', 'ACTION', null, '11', '2018-01-22 22:42:55');
INSERT INTO `authority` VALUES ('18', '字典项修改', 'DICTIONARY_UPDATE', 'ACTION', null, '11', '2018-01-22 22:43:30');
INSERT INTO `authority` VALUES ('19', '字典项删除', 'DICTIONARY_DELETE', 'ACTION', null, '11', '2018-01-22 22:43:52');

-- ----------------------------
-- Table structure for dictionary
-- ----------------------------
DROP TABLE IF EXISTS `dictionary`;
CREATE TABLE `dictionary` (
  `dictionary_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `value` varchar(128) NOT NULL,
  `detail` varchar(512) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `root_id` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`dictionary_id`),
  KEY `parent_id` (`parent_id`),
  KEY `fk_dictionary_root_id` (`root_id`),
  CONSTRAINT `fk_dictionary_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `dictionary` (`dictionary_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_dictionary_root_id` FOREIGN KEY (`root_id`) REFERENCES `dictionary` (`dictionary_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dictionary
-- ----------------------------
INSERT INTO `dictionary` VALUES ('1', '性别', 'GENDER', '性别是公用字典', null, '1', '2018-01-22 21:48:38');
INSERT INTO `dictionary` VALUES ('2', '保密', 'SECRET', '保密或未知', '1', '1', '2018-01-22 22:53:21');
INSERT INTO `dictionary` VALUES ('3', '男', 'MALE', '男孩、男士、雄性', '1', '1', '2018-01-22 22:53:35');
INSERT INTO `dictionary` VALUES ('4', '女', 'FEMALE', '女孩、女士、雌性', '1', '1', '2018-01-22 22:53:45');
INSERT INTO `dictionary` VALUES ('5', '权限类型', 'AUTHORITY_TYPE', '权限的类型', null, '5', '2018-01-22 22:54:26');
INSERT INTO `dictionary` VALUES ('6', '菜单', 'MENU', '一级菜单项及相关接口权限', '5', '5', '2018-01-22 22:54:55');
INSERT INTO `dictionary` VALUES ('7', '页面', 'PAGE', '页面菜单项及相关接口权限', '5', '5', '2018-01-22 22:55:28');
INSERT INTO `dictionary` VALUES ('8', '操作', 'ACTION', '操作及相关接口权限', '5', '5', '2018-01-22 22:55:40');

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `permission_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `action` varchar(64) NOT NULL,
  `target_type` varchar(64) NOT NULL,
  `target_id` varchar(64) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `idx_action_target_type_target_id` (`action`,`target_type`,`target_id`) USING BTREE,
  KEY `fk_permission_user_id` (`user_id`),
  CONSTRAINT `fk_permission_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
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
  `name` varchar(64) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '超级管理员', '2017-08-24 12:07:53');
INSERT INTO `role` VALUES ('2', '前端开发人员', '2017-12-23 16:25:58');
INSERT INTO `role` VALUES ('3', '用户管理员', '2018-01-21 17:19:07');
INSERT INTO `role` VALUES ('4', '角色管理员', '2018-01-22 22:47:05');
INSERT INTO `role` VALUES ('5', '权限管理员', '2018-01-22 21:47:28');

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
INSERT INTO `role_authority` VALUES ('1', '1', '2018-01-21 17:13:30');
INSERT INTO `role_authority` VALUES ('1', '2', '2018-01-21 18:58:30');
INSERT INTO `role_authority` VALUES ('1', '3', '2018-01-21 18:15:02');
INSERT INTO `role_authority` VALUES ('1', '4', '2018-01-21 17:42:39');
INSERT INTO `role_authority` VALUES ('1', '5', '2018-01-21 17:11:09');
INSERT INTO `role_authority` VALUES ('1', '6', '2018-01-21 18:57:31');
INSERT INTO `role_authority` VALUES ('1', '7', '2018-01-21 18:17:07');
INSERT INTO `role_authority` VALUES ('1', '8', '2018-01-21 17:56:03');
INSERT INTO `role_authority` VALUES ('1', '9', '2018-01-21 18:11:55');
INSERT INTO `role_authority` VALUES ('1', '10', '2018-01-21 17:47:47');
INSERT INTO `role_authority` VALUES ('1', '11', '2018-01-21 17:46:12');
INSERT INTO `role_authority` VALUES ('1', '12', '2018-01-21 18:50:39');
INSERT INTO `role_authority` VALUES ('1', '13', '2018-01-21 18:44:22');
INSERT INTO `role_authority` VALUES ('1', '14', '2018-01-21 17:46:15');
INSERT INTO `role_authority` VALUES ('1', '15', '2018-01-21 18:47:46');
INSERT INTO `role_authority` VALUES ('1', '16', '2018-01-21 18:29:50');
INSERT INTO `role_authority` VALUES ('1', '17', '2018-01-21 16:42:10');
INSERT INTO `role_authority` VALUES ('1', '18', '2018-01-21 16:57:40');
INSERT INTO `role_authority` VALUES ('1', '19', '2018-01-21 18:04:53');
INSERT INTO `role_authority` VALUES ('2', '10', '2018-01-21 15:22:59');
INSERT INTO `role_authority` VALUES ('2', '11', '2018-01-21 16:42:26');
INSERT INTO `role_authority` VALUES ('2', '12', '2018-01-21 17:11:04');
INSERT INTO `role_authority` VALUES ('3', '1', '2018-01-21 15:35:28');
INSERT INTO `role_authority` VALUES ('3', '2', '2018-01-21 17:20:14');
INSERT INTO `role_authority` VALUES ('3', '3', '2018-01-21 16:56:17');
INSERT INTO `role_authority` VALUES ('3', '4', '2018-01-21 15:15:38');
INSERT INTO `role_authority` VALUES ('3', '5', '2018-01-21 16:24:09');
INSERT INTO `role_authority` VALUES ('4', '1', '2018-01-21 16:03:05');
INSERT INTO `role_authority` VALUES ('4', '6', '2018-01-21 16:20:36');
INSERT INTO `role_authority` VALUES ('4', '7', '2018-01-21 16:07:40');
INSERT INTO `role_authority` VALUES ('4', '8', '2018-01-21 16:57:08');
INSERT INTO `role_authority` VALUES ('4', '9', '2018-01-21 16:09:54');
INSERT INTO `role_authority` VALUES ('5', '1', '2018-01-21 15:19:03');
INSERT INTO `role_authority` VALUES ('5', '13', '2018-01-21 16:11:17');
INSERT INTO `role_authority` VALUES ('5', '14', '2018-01-21 14:46:10');
INSERT INTO `role_authority` VALUES ('5', '15', '2018-01-21 16:10:28');
INSERT INTO `role_authority` VALUES ('5', '16', '2018-01-21 16:20:45');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password` varchar(32) NOT NULL,
  `nickname` varchar(64) NOT NULL,
  `gender` varchar(16) NOT NULL,
  `birthday` date DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'Ewing', 'yb', '元宝', 'MALE', '2000-02-10', '2017-08-23 18:43:52');
INSERT INTO `user` VALUES ('2', 'Rose', 'zx', '紫霞', 'FEMALE', '2002-05-20', '2017-08-24 12:06:02');
INSERT INTO `user` VALUES ('3', 'Jay', 'zjl', '周杰伦', 'MALE', '2018-01-21', '2018-01-21 17:19:51');
INSERT INTO `user` VALUES ('4', 'Zanilia', 'zly', '赵丽颖', 'FEMALE', '2018-01-21', '2018-01-21 17:21:40');
INSERT INTO `user` VALUES ('5', 'Mini', 'ym', '杨幂', 'FEMALE', '2018-01-21', '2018-01-21 17:27:05');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `role_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`role_id`,`user_id`),
  KEY `FK_role_user` (`role_id`),
  KEY `FK_user_role` (`user_id`),
  CONSTRAINT `FK_role_user` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_role` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('1', '1', '2018-01-21 17:21:54');
INSERT INTO `user_role` VALUES ('2', '5', '2018-01-23 00:47:42');
INSERT INTO `user_role` VALUES ('3', '2', '2018-01-22 22:50:48');
INSERT INTO `user_role` VALUES ('3', '4', '2018-01-22 22:49:55');
INSERT INTO `user_role` VALUES ('4', '2', '2018-01-22 22:50:48');
INSERT INTO `user_role` VALUES ('5', '2', '2018-01-22 22:50:50');
INSERT INTO `user_role` VALUES ('5', '3', '2018-01-22 22:51:30');
