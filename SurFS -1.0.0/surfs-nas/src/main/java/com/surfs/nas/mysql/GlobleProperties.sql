/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
CREATE TABLE `?` (
  `config_key` VARCHAR(40) NOT NULL DEFAULT '' COMMENT '',
  `config_value` VARCHAR(4000) NOT NULL DEFAULT '' COMMENT '',
  PRIMARY KEY (`config_key`)
);
