CREATE TABLE `actionmap` (
  `id` INTEGER(8) NOT NULL AUTO_INCREMENT COMMENT '标识',
  `actionid` VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'actionid',
  `dirid` INTEGER(8) NOT NULL DEFAULT '0' COMMENT '目录ID',
  `subdir` VARCHAR(20) DEFAULT NULL COMMENT '相对于dir的下一级目录',
  `classname` VARCHAR(80) NOT NULL DEFAULT '' COMMENT '类名',
  `permissionorder` TINYINT(2) NOT NULL DEFAULT -1 COMMENT '权限位',
  `params` TEXT COMMENT '配置',
  `menu` VARCHAR(100) DEFAULT 'NA' COMMENT '菜单',
  `memo` TEXT COMMENT '备注',
  `createtime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建/修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `actionid` (`actionid`, `dirid`, `subdir`),
  KEY `classname` (`classname`),
  KEY `menu` (`menu`),
  KEY `createtime` (`createtime`)
);