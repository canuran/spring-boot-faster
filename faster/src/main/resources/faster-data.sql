/*
Navicat MySQL Data Transfer

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2019-07-18 12:37:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for authority
-- ----------------------------
DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority` (
  `authority_id` bigint(20) NOT NULL,
  `name` varchar(128) NOT NULL,
  `code` varchar(64) NOT NULL,
  `type` varchar(64) NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`authority_id`),
  KEY `fk_authority_parent_id` (`parent_id`),
  CONSTRAINT `fk_authority_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `authority` (`authority_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of authority
-- ----------------------------
INSERT INTO `authority` VALUES (123456323456123451, '用户权限', 'USER_AUTHORITY', 'MENU', '<a class=\"waves-effect\">\r\n  <i class=\"fa fa-user-secret\"></i>用户权限\r\n  <i class=\"expand-menu fa fa-caret-right\"></i>\r\n</a>', null, '2017-12-23 17:51:03');
INSERT INTO `authority` VALUES (123456323456123452, '用户管理', 'USER_MANAGE', 'PAGE', '<a class=\"waves-effect\" onclick=\"addTab(\'用户管理\', \'user/user.html\')\">\r\n  <i class=\"fa fa-users\"></i>用户管理\r\n</a>', 123456323456123451, '2017-12-23 17:48:53');
INSERT INTO `authority` VALUES (123456323456123453, '用户新增', 'USER_ADD', 'ACTION', null, 123456323456123452, '2017-12-23 17:49:27');
INSERT INTO `authority` VALUES (123456323456123454, '用户修改', 'USER_UPDATE', 'ACTION', null, 123456323456123452, '2017-12-23 17:50:41');
INSERT INTO `authority` VALUES (123456323456123455, '用户删除', 'USER_DELETE', 'ACTION', null, 123456323456123452, '2017-12-23 17:51:03');
INSERT INTO `authority` VALUES (123456323456123456, '角色管理', 'ROLE_MANAGE', 'PAGE', '<a class=\"waves-effect\" onclick=\"addTab(\'角色管理\', \'security/role.html\')\">\n  <i class=\"fa fa-user-circle\"></i>角色管理\n</a>', 123456323456123451, '2017-12-23 17:48:53');
INSERT INTO `authority` VALUES (123456323456123457, '角色新增', 'ROLE_ADD', 'ACTION', null, 123456323456123456, '2017-12-23 17:49:27');
INSERT INTO `authority` VALUES (123456323456123458, '角色修改', 'ROLE_UPDATE', 'ACTION', null, 123456323456123456, '2017-12-23 17:50:41');
INSERT INTO `authority` VALUES (123456323456123459, '角色删除', 'ROLE_DELETE', 'ACTION', null, 123456323456123456, '2017-12-23 17:51:03');
INSERT INTO `authority` VALUES (123456323456123460, '公共资源', 'COMMON_RESOURCE', 'MENU', '<a class=\"waves-effect\">\r\n  <i class=\"fa fa-th-large\"></i>公共资源\r\n  <i class=\"expand-menu fa fa-caret-right\"></i>\r\n</a>', null, '2018-01-16 18:25:55');
INSERT INTO `authority` VALUES (123456323456123461, '数据字典', 'DICTIONARY_MANAGE', 'PAGE', '<a class=\"waves-effect\" onclick=\"addTab(\'数据字典\', \'common/dictionary.html\')\">\r\n  <i class=\"fa fa-th-list\"></i>数据字典\r\n</a>', 123456323456123460, '2018-01-16 21:26:55');
INSERT INTO `authority` VALUES (123456323456123462, '接口文档', 'API_DOCUMENT', 'PAGE', '<a class=\"waves-effect\" href=\"doc.html\" target=\"_blank\">\r\n  <i class=\"fa fa-file-text\"></i>接口文档\r\n</a>', null, '2018-01-16 22:28:04');
INSERT INTO `authority` VALUES (123456323456123463, '权限管理', 'AUTHORITY_MANAGE', 'PAGE', '<a class=\"waves-effect\" onclick=\"addTab(\'权限管理\', \'security/authority.html\')\">\n  <i class=\"fa fa-key\"></i>权限管理\n</a>', 123456323456123451, '2017-12-23 17:48:55');
INSERT INTO `authority` VALUES (123456323456123464, '权限新增', 'AUTHORITY_ADD', 'ACTION', null, 123456323456123463, '2018-01-22 22:24:53');
INSERT INTO `authority` VALUES (123456323456123465, '权限修改', 'AUTHORITY_UPDATE', 'ACTION', null, 123456323456123463, '2018-01-22 21:27:00');
INSERT INTO `authority` VALUES (123456323456123466, '权限删除', 'AUTHORITY_DELETE', 'ACTION', null, 123456323456123463, '2018-01-22 22:28:16');
INSERT INTO `authority` VALUES (123456323456123467, '字典项新增', 'DICTIONARY_ADD', 'ACTION', null, 123456323456123461, '2018-01-22 22:42:55');
INSERT INTO `authority` VALUES (123456323456123468, '字典项修改', 'DICTIONARY_UPDATE', 'ACTION', null, 123456323456123461, '2018-01-22 22:43:30');
INSERT INTO `authority` VALUES (123456323456123469, '字典项删除', 'DICTIONARY_DELETE', 'ACTION', null, 123456323456123461, '2018-01-22 22:43:52');

-- ----------------------------
-- Table structure for dictionary
-- ----------------------------
DROP TABLE IF EXISTS `dictionary`;
CREATE TABLE `dictionary` (
  `dictionary_id` bigint(20) NOT NULL,
  `name` varchar(128) NOT NULL,
  `value` varchar(128) NOT NULL,
  `detail` varchar(512) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `root_id` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`dictionary_id`),
  KEY `fk_dictionary_parent_id` (`parent_id`),
  KEY `fk_dictionary_root_id` (`root_id`),
  CONSTRAINT `fk_dictionary_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `dictionary` (`dictionary_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_dictionary_root_id` FOREIGN KEY (`root_id`) REFERENCES `dictionary` (`dictionary_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dictionary
-- ----------------------------
INSERT INTO `dictionary` VALUES (123456423456123451, '性别', 'GENDER', '性别是公用字典', null, 123456423456123451, '2018-01-22 21:48:38');
INSERT INTO `dictionary` VALUES (123456423456123452, '保密', 'SECRET', '保密或未知', 123456423456123451, 123456423456123451, '2018-01-22 22:53:21');
INSERT INTO `dictionary` VALUES (123456423456123453, '男', 'MALE', '男孩、男士、雄性', 123456423456123451, 123456423456123451, '2018-01-22 22:53:35');
INSERT INTO `dictionary` VALUES (123456423456123454, '女', 'FEMALE', '女孩、女士、雌性', 123456423456123451, 123456423456123451, '2018-01-22 22:53:45');
INSERT INTO `dictionary` VALUES (123456423456123455, '权限类型', 'AUTHORITY_TYPE', '权限的类型', null, 123456423456123455, '2018-01-22 22:54:26');
INSERT INTO `dictionary` VALUES (123456423456123456, '菜单', 'MENU', '一级菜单项及相关接口权限', 123456423456123455, 123456423456123455, '2018-01-22 22:54:55');
INSERT INTO `dictionary` VALUES (123456423456123457, '页面', 'PAGE', '页面菜单项及相关接口权限', 123456423456123455, 123456423456123455, '2018-01-22 22:55:28');
INSERT INTO `dictionary` VALUES (123456423456123458, '操作', 'ACTION', '操作及相关接口权限', 123456423456123455, 123456423456123455, '2018-01-22 22:55:40');

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `permission_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `action` varchar(64) NOT NULL,
  `target_type` varchar(64) NOT NULL,
  `target_id` varchar(64) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `uk_action_target_type_target_id` (`action`,`target_type`,`target_id`) USING BTREE,
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
  `role_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (123456223456123451, '超级管理员', '2017-08-24 12:07:53');
INSERT INTO `role` VALUES (123456223456123452, '前端开发人员', '2017-12-23 16:25:58');
INSERT INTO `role` VALUES (123456223456123453, '用户管理员', '2018-01-21 17:19:07');
INSERT INTO `role` VALUES (123456223456123454, '角色管理员', '2018-01-22 22:47:05');
INSERT INTO `role` VALUES (123456223456123455, '权限管理员', '2018-01-22 21:47:28');

-- ----------------------------
-- Table structure for role_authority
-- ----------------------------
DROP TABLE IF EXISTS `role_authority`;
CREATE TABLE `role_authority` (
  `role_id` bigint(20) NOT NULL,
  `authority_id` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`,`authority_id`),
  KEY `fk_role_authority_authority_id` (`authority_id`),
  CONSTRAINT `fk_role_authority_authority_id` FOREIGN KEY (`authority_id`) REFERENCES `authority` (`authority_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_authority_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_authority
-- ----------------------------
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123451, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123452, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123453, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123454, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123455, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123456, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123457, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123458, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123459, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123460, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123461, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123462, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123463, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123464, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123465, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123466, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123467, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123468, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123451, 123456323456123469, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123452, 123456323456123460, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123452, 123456323456123461, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123452, 123456323456123462, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123453, 123456323456123451, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123453, 123456323456123452, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123453, 123456323456123453, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123453, 123456323456123454, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123453, 123456323456123455, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123454, 123456323456123451, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123454, 123456323456123456, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123454, 123456323456123457, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123454, 123456323456123458, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123454, 123456323456123459, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123455, 123456323456123451, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123455, 123456323456123463, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123455, 123456323456123464, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123455, 123456323456123465, '2018-07-19 19:34:33');
INSERT INTO `role_authority` VALUES (123456223456123455, 123456323456123466, '2018-07-19 19:34:33');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL,
  `username` varchar(64) NOT NULL,
  `password` varchar(32) NOT NULL,
  `nickname` varchar(64) NOT NULL,
  `gender` varchar(16) NOT NULL,
  `birthday` date DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (123456123456123451, 'Ewing', 'yb', '元宝', 'MALE', '2000-02-10', '2017-08-23 18:43:52');
INSERT INTO `user` VALUES (123456123456123452, 'Rose', 'zx', '紫霞', 'FEMALE', '2002-05-20', '2017-08-24 12:06:02');
INSERT INTO `user` VALUES (123456123456123453, 'Jay', 'zjl', '周杰伦', 'MALE', '2018-01-21', '2018-01-21 17:19:51');
INSERT INTO `user` VALUES (123456123456123454, 'Zanilia', 'zly', '赵丽颖', 'FEMALE', '2018-01-21', '2018-01-21 17:21:40');
INSERT INTO `user` VALUES (123456123456123455, 'Mini', 'ym', '杨幂', 'FEMALE', '2018-01-21', '2018-01-21 17:27:05');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `role_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`role_id`,`user_id`),
  KEY `fk_user_role_user_id` (`user_id`),
  CONSTRAINT `fk_role_user_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (123456223456123451, 123456123456123451, '2018-01-21 17:21:54');
INSERT INTO `user_role` VALUES (123456223456123452, 123456123456123455, '2018-01-23 00:47:42');
INSERT INTO `user_role` VALUES (123456223456123453, 123456123456123452, '2018-01-22 22:50:48');
INSERT INTO `user_role` VALUES (123456223456123453, 123456123456123454, '2018-01-22 22:49:55');
INSERT INTO `user_role` VALUES (123456223456123454, 123456123456123452, '2018-01-22 22:50:48');
INSERT INTO `user_role` VALUES (123456223456123455, 123456123456123452, '2018-01-22 22:50:50');
INSERT INTO `user_role` VALUES (123456223456123455, 123456123456123453, '2018-01-22 22:51:30');
