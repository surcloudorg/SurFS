CREATE TABLE `webuser` (
  `id` INTEGER(8) NOT NULL AUTO_INCREMENT COMMENT '标识',
  `dirid` INTEGER(8) NOT NULL DEFAULT 0 COMMENT 'WEB服务ID，内置目录（0=console,-1=services）',
  `soapid` INTEGER(8) DEFAULT 0 COMMENT 'soap服务ID,dirid=-1（services目录）时有效',
  `username` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '密码',
  `realname` VARCHAR(30) DEFAULT NULL COMMENT '真实名称',
  `usergroup` VARCHAR(30) DEFAULT NULL COMMENT '组名，由具体web服务定义',
  `mobile` VARCHAR(30) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(60) DEFAULT NULL COMMENT '邮箱',
  `permission` VARCHAR(50) DEFAULT NULL COMMENT '权限，由具体WEB服务定义，如：console目录，由10位数字组成字符串表示',
  `stimeout` INTEGER(8) NOT NULL DEFAULT 0 COMMENT '对话超时，0默认值=servlet容器定义的值',
  `isactive` BIT(1) NOT NULL DEFAULT 1 COMMENT '1激活0停用',
  `iplist` VARCHAR(100) DEFAULT NULL COMMENT '不填不验证IP，否则只允许指定IP访问，,隔开如：192.168.*.*,211.232.*.*',
  `extparams` VARCHAR(60) DEFAULT NULL COMMENT '预留字段',
  `logintime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '最后登录时间',
  `memo` TEXT COMMENT '备注',
  `createtime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `realname` (`realname`),
  KEY `usergroup` (`usergroup`),
  KEY `mobile` (`mobile`),
  KEY `email` (`email`),
  KEY `isactive` (`isactive`),
  KEY `logintime` (`logintime`),
  KEY `extparams` (`extparams`),
  KEY `dirid` (`dirid`),
  KEY `soapid` (`soapid`)
);