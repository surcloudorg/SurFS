CREATE TABLE `logcfg` (
  `logname` VARCHAR(20) NOT NULL DEFAULT 'system' COMMENT '日志目录',
  `host` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '主机',
  `dateformatter` VARCHAR(20) NOT NULL DEFAULT '[MM-dd HH:mm:ss]' COMMENT '时间格式',
  `filter` VARCHAR(400) DEFAULT NULL COMMENT '过滤器，正则表达式,符合条件一定输出',
  `warnclass` VARCHAR(80) DEFAULT NULL COMMENT '报警接口实现类名',
  `warninteral` INTEGER(8) DEFAULT 0 COMMENT '报警间隔，0不间隔，只要产生fatal日志就报警',
  `level` TINYINT(2) DEFAULT 1 COMMENT '日志级别，0debug,1info,2warn,3error,4fatal',
  `addlevel` BIT(1) DEFAULT 1 COMMENT '日志里添加输出级别',
  `addclassname` BIT(1) DEFAULT 1 COMMENT '日志里添加类名',
  `outconsole` BIT(1) DEFAULT 0 COMMENT '是否输出到控制台',
  `sysloghost` VARCHAR(50) DEFAULT NULL COMMENT 'linux.syslog服务器名',
  `syslogfacility` VARCHAR(10) DEFAULT NULL COMMENT 'linux.syslog设备名',
  `params` Text COMMENT '配置信息',
  PRIMARY KEY (`logname`, `host`)
);