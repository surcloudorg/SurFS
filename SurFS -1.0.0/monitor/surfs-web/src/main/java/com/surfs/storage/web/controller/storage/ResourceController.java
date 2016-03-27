/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.controller.storage;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surfs.nas.StorageSources;

@Controller
@RequestMapping("/resource")
public class ResourceController {
	
	@RequestMapping(method = RequestMethod.POST, value = "/getZfsMap.do")
	@ResponseBody
	public Map<String, String> getZfsMap() {
		return StorageSources.getStoragePoolMap();
	}

}
