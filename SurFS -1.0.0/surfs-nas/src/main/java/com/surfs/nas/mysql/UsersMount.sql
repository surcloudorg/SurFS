CREATE TABLE `UsersMount` (
  `usersMountId` INTEGER(11) NOT NULL AUTO_INCREMENT COMMENT '',
  `usersId` INTEGER(11) DEFAULT NULL COMMENT '',
  `mountId` INTEGER(11) DEFAULT NULL COMMENT '',
  `permission` VARCHAR(10) DEFAULT NULL COMMENT '',
  PRIMARY KEY (`usersMountId`),
  UNIQUE KEY `usersId` (`usersId`, `mountId`)
);