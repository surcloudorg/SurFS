/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
CREATE TABLE `?` (
  `serverHost` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '',
  `properties` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '',
  PRIMARY KEY (`serverHost`)
);
