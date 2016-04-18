/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */ 
CREATE TABLE `Directorys` (
  `dirid` INTEGER(11) NOT NULL AUTO_INCREMENT COMMENT '',
  `parentid` INTEGER(11) NOT NULL DEFAULT '0' COMMENT '',
  `dirname` CHAR(255) NOT NULL DEFAULT '' COMMENT '',
  `length` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '',
  `lastmodified` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '',
  PRIMARY KEY (`dirid`),
  UNIQUE KEY `filename` (`dirname`,`parentid`)
);
