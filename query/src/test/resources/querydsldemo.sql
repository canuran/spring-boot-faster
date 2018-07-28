-- 初始化测试脚本
CREATE TABLE IF NOT EXISTS demo_address (
  address_id INT(11) PRIMARY KEY,
  name       VARCHAR(128) NOT NULL,
  parent_id  INT(11) DEFAULT NULL
);

INSERT INTO demo_address VALUES ('1', '广东省', NULL);
INSERT INTO demo_address VALUES ('2', '上海市', NULL);
INSERT INTO demo_address VALUES ('3', '深圳市', '1');
INSERT INTO demo_address VALUES ('4', '广州市', '1');
INSERT INTO demo_address VALUES ('5', '浦东区', '2');

CREATE TABLE IF NOT EXISTS demo_user (
  user_id     INT(11) AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(128) NOT NULL,
  password    VARCHAR(128) NOT NULL,
  gender      INT(11) DEFAULT NULL,
  address_id  INT(11) DEFAULT NULL,
  create_time DATETIME     NOT NULL
);

INSERT INTO demo_user VALUES ('1', '元宝', 'yb', '1', '3', '2000-01-01 00:00:00');
INSERT INTO demo_user VALUES ('2', '安娜', 'an', '2', '2', '2001-02-03 00:00:00');