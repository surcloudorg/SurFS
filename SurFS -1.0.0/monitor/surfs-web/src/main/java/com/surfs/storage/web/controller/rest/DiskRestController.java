/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surfs.storage.monitor.model.Disk;
import com.surfs.storage.monitor.service.DiskService;

@Controller
@RequestMapping("/service/disk")
public class DiskRestController {
	
	@Autowired
	private DiskService diskService;

	@RequestMapping(method = RequestMethod.GET, value = "/getRemoteDiskInfos")
	@ResponseBody
	public List<Disk> getRemoteDiskInfos() {
		return diskService.getLocalDiskInfos();
	}
	
}
