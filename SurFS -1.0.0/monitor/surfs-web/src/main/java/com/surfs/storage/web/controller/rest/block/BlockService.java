/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.controller.rest.block;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.autumn.core.log.LogFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surfs.storage.block.model.Device;
import com.surfs.storage.block.model.Export;
import com.surfs.storage.block.model.ExportInfo;
import com.surfs.storage.block.model.Vol;
import com.surfs.storage.block.model.Zpool;
import com.surfs.storage.block.model.ZpoolInfo;
import com.surfs.storage.block.service.BlockConstant;
import com.surfs.storage.block.service.PoolService;
import com.surfs.storage.common.util.CmdResponse;
import com.surfs.storage.common.util.CmdUtils;
import com.surfs.storage.common.util.Constant;
import com.surfs.storage.web.utils.Stringutils;

@Controller
@RequestMapping("/service/block")
public class BlockService {
	
	private static final ConcurrentHashMap<String, String> copyStatus = new ConcurrentHashMap<>();
	
	@Autowired
	private PoolService poolService ; 
	
	private CmdResponse executeCommand(String command, Object... args) throws IOException {
		String cmd = MessageFormat.format(command, args);
		return CmdUtils.executeCommand(cmd);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/pool/list")
	@ResponseBody
	public String getPoolList() throws IOException {
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_POOL_PATH_P, Constant.PROJECT_DIR_PATH);
		ObjectMapper objectMapper = new ObjectMapper();
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		else if (resp.getStatus() == 0 && StringUtils.isBlank(resp.getResponse())) {
			resp.setResponse("Not find pool");
			resp.setStatus(404);
			return objectMapper.writeValueAsString(resp);
		}
		
		ZpoolInfo json = objectMapper.readValue(resp.getResponse(),
				new TypeReference<ZpoolInfo>() {});
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		for (Iterator<Zpool> iterator = json.getInfo().iterator(); iterator.hasNext();) {
			Zpool zpool = iterator.next();
			//zpool.setTotal(Stringutils.convertToKB(zpool.getTotal()));
			//zpool.setFree(Stringutils.convertToKB(zpool.getFree()));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pool", zpool.getZpool());
			map.put("total", Long.parseLong(zpool.getTotal()));
			map.put("free", Long.parseLong(zpool.getFree()));
			map.put("ctime", Long.parseLong(zpool.getCtime()));
			list.add(map);
		}
		
		if (list.size() == 0) {
			resp.setStatus(404);
			resp.setResponse("Not find pool");
			return objectMapper.writeValueAsString(resp);
		}
		
		Map<String, Object> mapJson = new HashMap<String, Object>();
		mapJson.put("pool", BlockConstant.SURFS_LOCAL_POOL);
		mapJson.put("ip", json.getIp());
		mapJson.put("hostname", json.getHostname());
		mapJson.put("pools", list);
		
		return objectMapper.writeValueAsString(mapJson);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/pool/list/{pool}")
	@ResponseBody
	public String getPoolInfo(@PathVariable String pool) throws IOException {
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_POOL_PATH_P, Constant.PROJECT_DIR_PATH);
		ObjectMapper objectMapper = new ObjectMapper();
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		else if (resp.getStatus() == 0 && StringUtils.isBlank(resp.getResponse())) {
			resp.setResponse("Not find pool");
			resp.setStatus(404);
			return objectMapper.writeValueAsString(resp);
		}
		
		ZpoolInfo json = objectMapper.readValue(resp.getResponse(),
				new TypeReference<ZpoolInfo>() {});
		for (Iterator<Zpool> iterator = json.getInfo().iterator(); iterator.hasNext();) {
			Zpool zpool = iterator.next();
			if (zpool.getZpool().equals(pool)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("pool", zpool.getZpool());
				//map.put("total", Stringutils.convertToKB(zpool.getTotal()));
				//map.put("free", Stringutils.convertToKB(zpool.getFree()));
				map.put("total", Long.parseLong(zpool.getTotal()));
				map.put("free", Long.parseLong(zpool.getFree()));
				map.put("ctime", Long.parseLong(zpool.getCtime()));
				map.put("ip", json.getIp());
				return objectMapper.writeValueAsString(map);
			}
		}
		
		resp.setStatus(404);
		resp.setResponse("Not find pool is " + pool);
		return objectMapper.writeValueAsString(resp);
		
	}

	/////////////////////////////////////////////////////////////////////
	///////////////////////////////pool/////////////////////////////////
	///////////////////////////////////////////////////////////////////
	/**
	 * 显示本机pool信息及状态
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/pool/status")
	@ResponseBody
	public String getPoolStatus() throws IOException {
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_POOL_PATH_P, Constant.PROJECT_DIR_PATH);
		ObjectMapper objectMapper = new ObjectMapper();
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		else if (resp.getStatus() == 0 && StringUtils.isBlank(resp.getResponse())) {
			resp.setResponse("Not find pool");
			resp.setStatus(404);
			return objectMapper.writeValueAsString(resp);
		} 
		
		ZpoolInfo json = objectMapper.readValue(resp.getResponse(),
				new TypeReference<ZpoolInfo>() {});
		long pool_ctime = 0;
		long pool_total = 0;
		long pool_free = 0;
		for (Iterator<Zpool> iterator = json.getInfo().iterator(); iterator.hasNext();) {
			Zpool zpool = iterator.next();
			/*pool_ctime = Stringutils.compareReturnBerfore(pool_ctime, zpool.getCtime());
			pool_total = Stringutils.sumSpace(pool_total, zpool.getTotal());
			pool_free = Stringutils.sumSpace(pool_free, zpool.getFree());*/
			pool_ctime = Stringutils.compareDate(pool_ctime, zpool.getCtime());
			pool_total = Stringutils.sum(pool_total, zpool.getTotal());
			pool_free = Stringutils.sum(pool_free, zpool.getFree());
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pool", BlockConstant.SURFS_LOCAL_POOL);
		map.put("total", pool_total);
		map.put("free", pool_free);
		map.put("ctime", pool_ctime);
		map.put("ip", json.getIp());
		map.put("used", pool_total-pool_free);
		return objectMapper.writeValueAsString(map);
		
		
	}

	/////////////////////////////////////////////////////////////////////
	//////////////////////////////volume////////////////////////////////
	///////////////////////////////////////////////////////////////////
	/**
	 * 创建新卷
	 * 
	 * @param pool
	 * @param volume
	 * @param size
	 * @return
	 * @throws IOException 
	 */
	//@RequestMapping(method = RequestMethod.POST, value = "/volume/create/{pool}/{volume}/{size}")
	@RequestMapping(method = RequestMethod.POST, value = "/volume/create")
	@ResponseBody
	public String createVolume(@RequestParam String pool,
			@RequestParam String volume, @RequestParam String size) throws IOException {
		
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_ADDVOL_PATH,
				pool, volume, size, Constant.PROJECT_DIR_PATH);
		
		ObjectMapper objectMapper = new ObjectMapper();
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ip", CmdUtils.getLocalhostIp());
		
		return objectMapper.writeValueAsString(map);
	}

	/**
	 * 删除指定的卷
	 * 
	 * @param pool
	 * @param volume
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/volume/delete")
	@ResponseBody
	public String deleteVolume(@RequestParam String poolvolume) throws IOException {
		String[] pool_volume = poolvolume.split("\\/");
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_DELVOL_PATH,
				pool_volume[0], pool_volume[1], Constant.PROJECT_DIR_PATH);
		
		ObjectMapper objectMapper = new ObjectMapper();
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 0);
		
		return objectMapper.writeValueAsString(map);
	}
	
	/**
	 * 列出所有的卷
	 * 
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/volume/list")
	@ResponseBody
	public String listVolume() throws IOException {
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_POOL_PATH_P, Constant.PROJECT_DIR_PATH);
		
		ObjectMapper objectMapper = new ObjectMapper();
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		else if (resp.getStatus() == 0 && StringUtils.isBlank(resp.getResponse())) {
			resp.setResponse("Not find vol list");
			resp.setStatus(404);
			return objectMapper.writeValueAsString(resp);
		}
		
		long pool_ctime = 0;
		long pool_total = 0;
		long pool_free = 0;
		ZpoolInfo json = objectMapper.readValue(resp.getResponse(),
				new TypeReference<ZpoolInfo>() {});
		List<Vol> list = new ArrayList<>();
		for (Iterator<Zpool> iterator = json.getInfo().iterator(); iterator.hasNext();) {
			Zpool zpool = iterator.next();
			pool_ctime = Stringutils.compareDate(pool_ctime, zpool.getCtime());
			pool_total = Stringutils.sum(pool_total, zpool.getTotal());
			pool_free = Stringutils.sum(pool_free, zpool.getFree());
			List<Vol> vols = zpool.getVols();
			list.addAll(vols);
		}
		
		if (list.size() == 0) {
			resp.setStatus(404);
			resp.setResponse("Not find vol list");
			return objectMapper.writeValueAsString(resp);
		}
		
		List<Map<String, Object>> listMap = new ArrayList<>();
		for (Vol vol : list) {
			Map<String, Object> volMap = new HashMap<>();
			volMap.put("vol", vol.getVol());
			volMap.put("cap", Long.parseLong(vol.getCap()));
			volMap.put("ctime", Long.parseLong(vol.getCtime()));
			volMap.put("used", vol.getUsed());
			listMap.add(volMap);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pool", BlockConstant.SURFS_LOCAL_POOL);
		map.put("total", pool_total);
		map.put("free", pool_free);
		map.put("ctime", pool_ctime);
		map.put("ip", json.getIp());
		map.put("used", pool_total-pool_free);
		map.put("vols", listMap);
		return objectMapper.writeValueAsString(map);
	}
	
	/**
	 * 卷拷贝，将源卷拷贝到目标卷上
	 * 
	 * @param sourcepool
	 * @param sourcevolume
	 * @param destpool
	 * @param destvolume
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/volume/copy")
	@ResponseBody
	public String copyVolume(@RequestParam final String sourcepoolvolume,
			@RequestParam final String destpoolvolume, @RequestParam(required = false) final boolean cover) throws IOException {
		final String[] pool_volume = destpoolvolume.split("\\/");
		LogFactory.info("destpoolvolume:" + destpoolvolume);
		LogFactory.info("copyStatus key:" + pool_volume[1]);
		// remove previous status
		copyStatus.remove(pool_volume[1]);
		// new thread execute, return
		new Thread(new Runnable() {
			@Override
			public void run() {
				copyStatus.put(pool_volume[1], "running");
				try {
					String over = "";
					if (cover)
						over = "-f";
					CmdResponse resp = executeCommand(BlockConstant.BLOCK_VOL_COPY,
							sourcepoolvolume, destpoolvolume, over, Constant.PROJECT_DIR_PATH);
					if (resp.getStatus() != 0)
						copyStatus.put(pool_volume[1], "failure");
					else
						copyStatus.put(pool_volume[1], "complete");
				} catch(IOException e) {
					copyStatus.put(pool_volume[1], "failure");
				}
			}
		}).start();
		
		return "{\"status\":0,\"progress\":\"running\"}";
		
		/*ObjectMapper objectMapper = new ObjectMapper();
		if (cover) {
			CmdResponse resp = executeCommand(BlockConstant.BLOCK_VOL_COPY,
					sourcepoolvolume, destpoolvolume, "-f",
					Constant.PROJECT_DIR_PATH);
			if (resp.getStatus() != 0)
				return objectMapper.writeValueAsString(resp);
		} else {
			CmdResponse resp = executeCommand(BlockConstant.BLOCK_VOL_COPY,
					sourcepoolvolume, destpoolvolume, Constant.PROJECT_DIR_PATH);
			if (resp.getStatus() != 0)
				return objectMapper.writeValueAsString(resp);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 0);
		
		return objectMapper.writeValueAsString(map);*/
	}
	
	/**
	 * 获得卷拷贝状态
	 * 
	 * @param volume 卷名称
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/volume/copystatus/{volume}")
	@ResponseBody
	public String copyVolumeStatus(@PathVariable final String volume) {
		String status = copyStatus.get(volume);
		if (StringUtils.isBlank(status))
			return "{\"status\":0,\"progress\":\"not find\"}";
		return "{\"status\":0,\"progress\":\"" + status + "\"}";
	}
	
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////snapshot///////////////////////////////
	///////////////////////////////////////////////////////////////////
	/**
	 * 创建快照
	 * 
	 * @param snapshot
	 * @param pool
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/snapshot/create")
	@ResponseBody
	public String createSnapshot(@RequestParam String poolvolume,
			@RequestParam String snapshot) throws IOException {
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_SNAP_CREATE,
				poolvolume, snapshot, Constant.PROJECT_DIR_PATH);
		
		ObjectMapper objectMapper = new ObjectMapper();
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 0);
		
		return objectMapper.writeValueAsString(map);
	}
	
	/**
	 * 删除快照
	 * 
	 * @param pool
	 * @param snapshot
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/snapshot/delete")
	@ResponseBody
	public String deleteSnapshot(@RequestParam String poolvolsnapshot) throws IOException {
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_SNAP_DEL,
				poolvolsnapshot, Constant.PROJECT_DIR_PATH);
		
		ObjectMapper objectMapper = new ObjectMapper();
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 0);
		
		return objectMapper.writeValueAsString(map);
	}
	
	/**
	 * 显示快照信息，snapshot未指明显示全部快照信息
	 * 
	 * @param snapshot pool/vol@snapshot
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/snapshot/list")
	@ResponseBody
	public String listSnapshot(@RequestParam(required = false) String poolvolumesnap) throws IOException {
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_SNAP_LIST, Constant.PROJECT_DIR_PATH);
		
		ObjectMapper objectMapper = new ObjectMapper();
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		else if (resp.getStatus() == 0 && StringUtils.isBlank(resp.getResponse())) {
			resp.setResponse("Not find snapshot list");
			resp.setStatus(404);
			return objectMapper.writeValueAsString(resp);
		}
		
		Map<String, Object> snapMap = new HashMap<>();
		snapMap.put("ip", CmdUtils.getLocalhostIp());
		snapMap.put("pool", BlockConstant.SURFS_LOCAL_POOL);
		
		List<Map<String, Object>> listMap = objectMapper.readValue(resp.getResponse(),
				new TypeReference<List<Map<String, Object>>>() {});
		List<Map<String, Object>> snaps = new ArrayList<>();
		
		for (Map<String, Object> map : listMap) {
			String ctime = map.get("ctime").toString();
			String size = map.get("size").toString();
			map.put("ctime", Long.parseLong(ctime));
			map.put("size", Long.parseLong(size));
			if (!StringUtils.isBlank(poolvolumesnap)) {
				String name = map.get("name").toString();
				if (poolvolumesnap.equals(name)) {
					snaps.add(map);
					snapMap.put("snaps", snaps);
					return objectMapper.writeValueAsString(snapMap);
				}
			}
		}
		
		if (!StringUtils.isBlank(poolvolumesnap) && snaps.size() < 1) {
			resp.setResponse("Not find snapshot is " + poolvolumesnap);
			resp.setStatus(404);
			return objectMapper.writeValueAsString(resp);
		}
	
		snapMap.put("snaps", listMap);
		return objectMapper.writeValueAsString(snapMap);
	}
	
	/**
	 * 快照中生成新卷，卷大小，必须不小于快照大小
	 * 
	 * @param pool
	 * @param volume
	 * @param size
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/snapshot/generate")
	@ResponseBody
	public String generateVolume(@RequestParam String poolvolumesnapshot,
			@RequestParam String poolvolume, @RequestParam String size) throws IOException {
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_SNAP_GENERATE,
				poolvolumesnapshot, poolvolume, size, Constant.PROJECT_DIR_PATH);
		ObjectMapper objectMapper = new ObjectMapper();

		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 0);
		
		return objectMapper.writeValueAsString(map);
	}
	
	/////////////////////////////////////////////////////////////////////
	/////////////////////////export/disexport///////////////////////////
	///////////////////////////////////////////////////////////////////
	/**
	 * 导出iscis
	 * 
	 * @param iqn
	 * @param initiator
	 * @param chap
	 * @param volume
	 * @param ip
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/export")
	@ResponseBody
	public String export(@RequestParam String iqn,
			@RequestParam String initiator, @RequestParam String user,
			@RequestParam String pw, @RequestParam String volume)
			throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		
		//exist iqn, not iqn execute create
		CmdResponse resp = createIqn(iqn);
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		
		//device
		CmdResponse addDevice = executeCommand(BlockConstant.BLOCK_ADDDEVICE_PATH,
				BlockConstant.SURFS_DEFAULT_DEV + volume,  iqn, Constant.PROJECT_DIR_PATH);
		if (addDevice.getStatus() != 0)
			return objectMapper.writeValueAsString(addDevice);
		//acl
		CmdResponse addAcl = executeCommand(BlockConstant.BLOCK_ADDDEVICE_PATH_ACL,
				initiator, iqn, Constant.PROJECT_DIR_PATH);
		if (addAcl.getStatus() != 0)
			return objectMapper.writeValueAsString(addAcl);
		//oauth
		CmdResponse addOauth = executeCommand(BlockConstant.BLOCK_ADDDEVICE_PATH_OAUTH,
				 iqn, user, pw, Constant.PROJECT_DIR_PATH);
		if (addOauth.getStatus() != 0)
			return objectMapper.writeValueAsString(addOauth);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 0);
		map.put("response", CmdUtils.getLocalhostIp());
		
		return objectMapper.writeValueAsString(map);
	}
	
	private CmdResponse createIqn(String iqn) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		
		//exist
		CmdResponse respExist = executeCommand(BlockConstant.BLOCK_TARGET_PATH, Constant.PROJECT_DIR_PATH);
		if (respExist.getStatus() != 0)
			return respExist;
		ExportInfo exportInfo = objectMapper.readValue(respExist.getResponse(),
				new TypeReference<ExportInfo>() {});
		boolean exist = false;
		for (Export export : exportInfo.getInfo()) {
			if (iqn.equals(export.getTarget())) {
				exist = true;
				break;
			}
		}
		
		if (!exist) {
			//create
			CmdResponse respCreate = executeCommand(BlockConstant.BLOCK_ADDTARGET_PATH,
					iqn, Constant.PROJECT_DIR_PATH);
			if (respCreate.getStatus() != 0)
				return respExist;
		}
		
		return respExist;
	}
	
	/**
	 * 取消导出
	 * 
	 * @param poolvolume
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/export/disable")
	@ResponseBody
	public String disExport(@RequestParam String poolvolume) throws IOException {
		String[] pool_volume = poolvolume.split("\\/");
		ObjectMapper objectMapper = new ObjectMapper();
		
		//get export info
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_TARGET_PATH,
				Constant.PROJECT_DIR_PATH);
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		ExportInfo exportInfo = objectMapper.readValue(resp.getResponse(),
				new TypeReference<ExportInfo>() {});
		
		//get iqn
		String iqn = null;
		/*boolean isDelTarget = false;*/
		for (Export export : exportInfo.getInfo()) {
			for (Device device : export.getDevice()) {
				String[] devs_pools_vols = device.getVol().split("\\/");
				if (pool_volume[1].equals(devs_pools_vols[4])) {
					iqn = export.getTarget();
					/*if (export.getDevice().size() == 1) 
						isDelTarget = true;*/
					break;
				}
			}
		}
		
		//del dev
		/*CmdResponse respDelDev = executeCommand(
				BlockConstant.BLOCK_DELDEVICE_PATH, BlockConstant.SURFS_DEFAULT_DEV + poolvolume, iqn,
				Constant.PROJECT_DIR_PATH);
		if (respDelDev.getStatus() != 0)
			return objectMapper.writeValueAsString(respDelDev);*/
		
		//del target
		/*if (isDelTarget) {*/
		CmdResponse respDelTarget = executeCommand(
				BlockConstant.BLOCK_DELTARGET_PATH, iqn, Constant.PROJECT_DIR_PATH);
		if (respDelTarget.getStatus() != 0)
			return objectMapper.writeValueAsString(respDelTarget);
		/*}*/
		
		//success
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 0);
		return objectMapper.writeValueAsString(map);
	}
	
	/**
	 * 检查导出信息
	 * 
	 * @param iqn
	 * @param initiator
	 * @param user
	 * @param pw
	 * @param volume
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/export/check")
	@ResponseBody
	public String checkExport(@RequestParam String iqn,
			@RequestParam String initiator, @RequestParam String user,
			@RequestParam String pw, @RequestParam String volume) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		
		//get export info
		CmdResponse resp = executeCommand(BlockConstant.BLOCK_TARGET_PATH,
				Constant.PROJECT_DIR_PATH);
		if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);
		ExportInfo exportInfo = objectMapper.readValue(resp.getResponse(),
				new TypeReference<ExportInfo>() {});
		
		boolean checkTarget = false;
		boolean checkAccount = false;
		boolean checkAcl = false;
		for (Export export : exportInfo.getInfo()) {
			String target = export.getTarget();
			//check target
			if (target.equals(iqn)) {
				for (Device device : export.getDevice()) {
					String vol = device.getVol();
					if (vol.equals(BlockConstant.SURFS_DEFAULT_DEV + volume)) {
						checkTarget = true;
					}
				}
			}
			//check account
			for (String account : export.getAccount()) {
				if (account.equals(user)) {
					checkAccount = true;
				}
			}
			//check acl
			for (String acl : export.getAcl()) {
				if (acl.equals(acl)) {
					checkAcl = true;
				}
			}
		}
		
		//return json
		Map<String, Object> map = new HashMap<String, Object>();
		
		//repair target
		if (!checkTarget) {
			String respTarget = export(iqn, initiator, user, pw, volume);
			CmdResponse cmd = objectMapper.readValue(respTarget,
					new TypeReference<CmdResponse>() {});
			if (cmd.getStatus() != 0)
				return respTarget;
			
			map.put("repair", new String[]{ "target" });
		} else {//repair account, acl
			if (!checkAccount) {
				//acl
				CmdResponse addAcl = executeCommand(BlockConstant.BLOCK_ADDDEVICE_PATH_ACL,
						initiator, iqn, Constant.PROJECT_DIR_PATH);
				
				if (addAcl.getStatus() != 0)
					return objectMapper.writeValueAsString(addAcl);
				map.put("repair", new String[]{ "initiator" });
			}
			
			if (!checkAcl) {
				//oauth
				CmdResponse addOauth = executeCommand(BlockConstant.BLOCK_ADDDEVICE_PATH_OAUTH,
						 iqn, user, pw, Constant.PROJECT_DIR_PATH);
				if (addOauth.getStatus() != 0)
					return objectMapper.writeValueAsString(addOauth);
				Object repairAccount = map.get("repair");
				
				if (repairAccount != null) {
					map.put("repair", new Object[]{ repairAccount, "ACL" });
				}
				map.put("repair", new String[]{ "ACL" });
			}
		}
		
		map.put("status", 0);
		return objectMapper.writeValueAsString(map);
		
	}
	
	/////////////////////////////////////////////////////////////////////
	//////////////////////////////others////////////////////////////////
	///////////////////////////////////////////////////////////////////
	@RequestMapping(method = RequestMethod.GET, value = "/system/surfs")
	@ResponseBody
	public String getSurfsInfo() throws IOException {
		
		Map<String, Object> surfs = new HashMap<>();
		List<Map<String, Object>> infos = new ArrayList<>();
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		String jsonLocal = surfsInfo(CmdUtils.getLocalhostIp(), BlockConstant.SURFS_LOCAL_POOL);
		if (jsonLocal != null) {
			Map<String, Object> surfsInfoLocal = objectMapper.readValue(
					jsonLocal, new TypeReference<Map<String, Object>>() {});
			infos.add(surfsInfoLocal);
		}
		
		String jsonRemote = surfsInfo(BlockConstant.SURFS_REMOTE_IP, BlockConstant.SURFS_REMOTE_POOL);
		if (jsonRemote != null) {
			Map<String, Object> surfsInfoRemote = objectMapper.readValue(
					jsonRemote, new TypeReference<Map<String, Object>>() {});
			infos.add(surfsInfoRemote);
		}
		
		if (infos.size() == 0) {
			Map<String, Object> error = new HashMap<String, Object>();
			error.put("status", 1);
			error.put("message", "Not find surfs");
			return objectMapper.writeValueAsString(error);
		}
		
		surfs.put("infos", infos);
		surfs.put("version", BlockConstant.SURFS_VERSION);
		return objectMapper.writeValueAsString(surfs);
		
	}
	
	/*@RequestMapping(method = RequestMethod.GET, value = "/system/surfsRemote")
	@ResponseBody
	public String callRemoteSurfsInfo() throws IOException {
		return getLocalSurfsInfo();
	}*/
	
	private String surfsInfo(String ip, String pool) {
		if (StringUtils.isBlank(ip) || StringUtils.isBlank(pool))
			return null;
			
		Map<String, Object> surfsInfoLocal = new HashMap<>();
		StringBuilder url = new StringBuilder();
		url.append("http://");
		url.append(CmdUtils.getLocalhostIp());
		url.append(":");
		url.append(Constant.REST_SERVICE_PORT);
		surfsInfoLocal.put("ip", url.toString());
		surfsInfoLocal.put("pools", pool.split(","));
		//surfsInfoLocal.put("version", BlockConstant.SURFS_VERSION);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(surfsInfoLocal);
		} catch (JsonProcessingException e) {
			LogFactory.trace("call getLocalSurfsInfo error", e);
		}
		return null;
	}
	
	/*private String getRemoteSurfsInfo() {
		try {
			String ip = CmdUtils.getRemoteIp();
			String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
					BlockConstant.POOL_SERVICE_PATH, "system/surfsRemote");
			return HttpUtils.invokeHttpForGet(url);
		} catch(IOException e) {
			LogFactory.trace("call getRemoteSurfsInfo error", e);
		}
		return null;
	}*/
	
	@RequestMapping(method = RequestMethod.POST, value = "/system/speed")
	@ResponseBody
	public String getSpeed(@RequestParam String ip) throws IOException {
		String cmd = MessageFormat.format(BlockConstant.PING_SPEED, ip, Constant.PROJECT_DIR_PATH);
		Map<String, Object> resp = CmdUtils.command(cmd);
		
		ObjectMapper objectMapper = new ObjectMapper();
		/*if (resp.getStatus() != 0)
			return objectMapper.writeValueAsString(resp);*/
		
		return objectMapper.writeValueAsString(resp);
	}
	
	public static void main(String[] args) {
		String a =  "Thu Jan 14 3:27 2016";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm yyyy", Locale.US);//MMM dd hh:mm:ss Z yyyy
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");//MMM dd hh:mm:ss Z yyyy
        try {
            System.out.println(sdf1.format(sdf.parse(a)));
        } catch (ParseException ex) {
        	ex.printStackTrace();
        }
        System.out.println(System.getProperty("user.dir"));
	}
	
}
