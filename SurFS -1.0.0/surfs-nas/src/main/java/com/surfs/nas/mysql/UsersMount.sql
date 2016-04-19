/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
CREATE TABLE `UsersMount` (
  `usersMountId` INTEGER(11) NOT NULL AUTO_INCREMENT COMMENT '',
  `usersId` INTEGER(11) DEFAULT NULL COMMENT '',
  `mountId` INTEGER(11) DEFAULT NULL COMMENT '',
  `permission` VARCHAR(10) DEFAULT NULL COMMENT '',
  PRIMARY KEY (`usersMountId`),
  UNIQUE KEY `usersId` (`usersId`, `mountId`)
);
