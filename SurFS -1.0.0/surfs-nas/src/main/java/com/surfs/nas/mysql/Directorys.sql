 
CREATE TABLE `Directorys` (
  `dirid` INTEGER(11) NOT NULL AUTO_INCREMENT COMMENT '',
  `parentid` INTEGER(11) NOT NULL DEFAULT '0' COMMENT '',
  `dirname` CHAR(255) NOT NULL DEFAULT '' COMMENT '',
  `length` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '',
  `lastmodified` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '',
  PRIMARY KEY (`dirid`),
  UNIQUE KEY `filename` (`dirname`,`parentid`)
);
