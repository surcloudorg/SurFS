CREATE TABLE `services` (
  `id` INTEGER(8) NOT NULL AUTO_INCREMENT COMMENT '服务标识',
  `host` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '主机名',
  `title` VARCHAR(60) DEFAULT NULL COMMENT '注释',
  `classname` VARCHAR(80) DEFAULT NULL COMMENT '类名',
  `params` TEXT COMMENT '配置',
  `logname` VARCHAR(20) NOT NULL DEFAULT 'system' COMMENT '日志目录',
  `status` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '0随服务启动 1手动 2禁用',
  `memo` TEXT COMMENT '备注',
  `createtime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `title` (`title`, `host`),
  KEY `classname` (`classname`),
  KEY `status` (`status`),
  KEY `createtime` (`createtime`)
);