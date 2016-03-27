
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.controller.storage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.surfs.nas.client.SurFile;
import com.surfs.nas.client.SurFileFactory;
import com.surfs.storage.user.model.Mount;
import com.surfs.storage.user.service.MountService;
import com.surfs.storage.web.utils.WebUtils;

@Controller
@RequestMapping("/storage")
public class MountController {

	@Autowired
	private MountService mountService;

	@RequestMapping(method = RequestMethod.POST, value = "/showMountPoint.do")
	public ModelAndView showMountPoint(HttpSession session) {
		try {
			List<Mount> mountList = mountService.queryAllMount(WebUtils
					.getCrrentDataCenterKey(session));
			return new ModelAndView("mount", "mountList", mountList);
		} catch (Exception e) {
			return new ModelAndView("redirect:/login.jsp?error="
					+ e.getMessage());
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/deleteMount.do")
	@ResponseBody
	public void deleteMount(@RequestBody Map<String, String> map,
			HttpSession session) throws Exception {
		SurFile dir = null;

		try {
			mountService.removeMount(WebUtils.getCrrentDataCenterKey(session),
					Integer.parseInt(map.get("mountId")));

			dir = SurFileFactory.newInstance(map.get("path"));
			dir.delete();
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/saveMount.do")
	@ResponseBody
	public void saveMount(@RequestBody Mount mount, HttpSession session)
			throws Exception {
		SurFile sourceDir = null;
		SurFile targetDir = null;
		try {
			sourceDir = SurFileFactory.newInstance(mount.getOldPath());
			targetDir = SurFileFactory.newInstance(mount.getPath());
			if (!mount.getOldPath().equals(mount.getPath()))
				sourceDir.renameTo(targetDir);
			else
				targetDir = sourceDir;
			//将当前目录设为修改的目录
			mount.setPath(targetDir.getPath());

			mountService.modifyMount(WebUtils.getCrrentDataCenterKey(session),
					mount);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			if (sourceDir != null)
				sourceDir.renameTo(sourceDir);
			throw e;
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/addMount.do")
	@ResponseBody
	public int addMount(@RequestBody Mount mount, HttpSession session)
			throws Exception {
		SurFile dir = null;
		try {
			dir = SurFileFactory.newInstance(mount.getPath());
			dir.mkdirs();
			mount.setPath(dir.getPath());

			return mountService.addMount(
					WebUtils.getCrrentDataCenterKey(session), mount);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			dir.delete();
			throw e;
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/addUsersMount.do")
	@ResponseBody
	public void addUsersMount(@RequestBody List<Map<String, String>> listMap, HttpSession session)
			throws Exception {
		try {
			mountService.addUsersMount(WebUtils.getCrrentDataCenterKey(session), listMap);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/editUsersMount.do")
	@ResponseBody
	public void editUsersMount(@RequestBody List<Map<String, String>> listMap, HttpSession session)
			throws Exception {
		try {
			mountService.editUsersMount(WebUtils.getCrrentDataCenterKey(session), listMap);
		} catch (Exception e) {
			throw e;
		}
	}

}
