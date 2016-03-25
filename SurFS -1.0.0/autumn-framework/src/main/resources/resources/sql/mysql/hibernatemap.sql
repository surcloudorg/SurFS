CREATE TABLE `hibernatemap` (
  `id` INTEGER(8) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `host` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '服务器机器名',
  `title` VARCHAR(60) NOT NULL DEFAULT '' COMMENT '标题',
  `classname` VARCHAR(80) NOT NULL DEFAULT '' COMMENT '映射类名',
  `datasource` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '数据库连接池名',
  `tablename` VARCHAR(20) DEFAULT NULL COMMENT '映射的表名',
  `catalogname` VARCHAR(20) DEFAULT NULL COMMENT '数据库目录',
  `xmlmap` TEXT COMMENT '映射配置',
  `createtime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建/最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `classname` (`host`, `classname`, `datasource`),
  KEY `title` (`title`),
  KEY `tablename` (`tablename`),
  KEY `createtime` (`createtime`),
  KEY `catalogname` (`catalogname`)
);