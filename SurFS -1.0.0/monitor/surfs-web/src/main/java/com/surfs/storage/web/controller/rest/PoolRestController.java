/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.controller.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surfs.storage.block.service.PoolService;

@Controller
@RequestMapping("/service/block")
public class PoolRestController {

	@Autowired
	private PoolService poolService;

	@RequestMapping(method = RequestMethod.GET, value = "/getRemoteZpoolInfoJson")
	@ResponseBody
	public String getRemoteZpoolInfoJson() {
		return poolService.getZpoolInfoJson();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/deleteRemoteVol/{zpool}/{vol:.*}")
	@ResponseBody
	public String deleteRemoteVol(@PathVariable String zpool,
			@PathVariable String vol) throws UnsupportedEncodingException {
		return poolService.deleteVol(URLDecoder.decode(zpool, "UTF-8"),
				URLDecoder.decode(vol, "UTF-8"));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addRemoteVol/{zpool}/{vol}/{size:.*}")
	@ResponseBody
	public String addRemoteVol(@PathVariable String zpool,
			@PathVariable String vol, @PathVariable String size)
			throws UnsupportedEncodingException {
		return poolService.addVol(URLDecoder.decode(zpool, "UTF-8"),
				URLDecoder.decode(vol, "UTF-8"),
				URLDecoder.decode(size, "UTF-8"));
	}

}
