CREATE TABLE `svncodes` (
  `id` INTEGER(8) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `host` VARCHAR(25) NOT NULL DEFAULT 'localhost' COMMENT '所属机器名',
  `title` VARCHAR(50) NOT NULL DEFAULT 'newproject' COMMENT '工程代码标题',
  `url` VARCHAR(200) DEFAULT NULL COMMENT 'svn资源地址',
  `dirname` VARCHAR(35) NOT NULL DEFAULT 'NewDir' COMMENT '本地目录',
  `username` VARCHAR(20) DEFAULT NULL COMMENT 'SVN认证帐号',
  `password` VARCHAR(50) DEFAULT NULL COMMENT 'SVN认证密码',
  `dirtype` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '目录类型，0.java源码 1web目录（jsp）',
  `message` TEXT COMMENT '更新信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `url` (`host`, `url`),
  KEY `title` (`title`),
  KEY `dirname` (`dirname`),
  KEY `dirtype` (`dirtype`)
);