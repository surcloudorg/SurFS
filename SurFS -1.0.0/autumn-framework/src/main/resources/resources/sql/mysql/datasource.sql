CREATE TABLE `datasource` (
  `jndiname` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '数据库连接池名称',
  `host` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '主机',
  `driver` VARCHAR(60) NOT NULL DEFAULT 'com.mysql.jdbc.Driver' COMMENT '数据库驱动类名',
  `dburl` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '数据库URL地址',
  `username` VARCHAR(15) NOT NULL DEFAULT '' COMMENT '账号',
  `pwd` VARCHAR(20) DEFAULT '' COMMENT '密码',
  `maxconnection` INTEGER(4) NOT NULL DEFAULT 100 COMMENT '最大连接数',
  `minconnection` INTEGER(4) NOT NULL DEFAULT 5 COMMENT '空闲时最小连接数',
  `timeoutvalue` INTEGER(8) NOT NULL DEFAULT 600000 COMMENT '登录超时',
  `testsql` VARCHAR(30) NOT NULL DEFAULT 'select 1' COMMENT '测试指令，交付程序的连接首先执行此激活指令，不填则不进行激活测试',
  `maxstatement` INTEGER(4) NOT NULL DEFAULT 50 COMMENT '允许单个连接创建的最大声明数,0不限制',
  PRIMARY KEY (`jndiname`, `host`)
);