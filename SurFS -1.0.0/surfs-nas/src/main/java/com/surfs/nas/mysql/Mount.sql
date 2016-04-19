/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
CREATE TABLE `Mount` (
  `mountId` INTEGER(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `path` VARCHAR(50) DEFAULT NULL COMMENT '',
  `quota` BIGINT(20) DEFAULT NULL COMMENT '',
  `createTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  PRIMARY KEY (`mountId`),
  UNIQUE KEY `path` (`path`)
);
