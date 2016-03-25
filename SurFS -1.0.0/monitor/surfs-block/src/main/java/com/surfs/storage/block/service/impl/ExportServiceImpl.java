package com.surfs.storage.block.service.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autumn.core.log.LogFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surfs.storage.block.dao.mapper.BlockUserMapper;
import com.surfs.storage.block.model.BlockUser;
import com.surfs.storage.block.model.ExportInfo;
import com.surfs.storage.block.service.BlockConstant;
import com.surfs.storage.block.service.ExportService;
import com.surfs.storage.common.datasource.jdbc.JdbcDao;
import com.surfs.storage.common.util.CmdUtils;
import com.surfs.storage.common.util.Constant;
import com.surfs.storage.common.util.HttpUtils;

@Service("exportService")
public class ExportServiceImpl implements ExportService {
	@Autowired
	private JdbcDao jdbcDao;
	@Override
	public List<ExportInfo> getExportInfos() {
		List<ExportInfo> list = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			// local
			String jsonLocal = CmdUtils
					.executeCmdForString(BlockConstant.BLOCK_TARGET_PATH);
			if (!StringUtils.isBlank(jsonLocal)) {
				ExportInfo infoLocal = objectMapper.readValue(jsonLocal,
						new TypeReference<ExportInfo>() {});
				LogFactory.info(jsonLocal);
				list.add(infoLocal);
			}

			// remote
			String jsonRemote = getRemoteExportInfoJson();
			if (!StringUtils.isBlank(jsonRemote)) {
				ExportInfo infoRemote = objectMapper.readValue(jsonRemote,
						new TypeReference<ExportInfo>() {});
				LogFactory.info(jsonRemote);
				list.add(infoRemote);
			}
		} catch (Exception e) {
			LogFactory.trace("call getExportInfos error", e);
		}
		return list;
	}
	
	@Override
	public String getExportInfoJson() {
		return CmdUtils.executeCmdForString(BlockConstant.BLOCK_TARGET_PATH);
	}
	
	private String getRemoteExportInfoJson() throws Exception {
		String ip = CmdUtils.getRemoteIp();
		//String ip = "10.0.33.52";
		String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
				BlockConstant.POOL_SERVICE_PATH,
				BlockConstant.EXPORT_SERVICE_TARGET_NAME);
		return HttpUtils.invokeHttpForGet(url);
	}
	
	@Override
	public String addTarget(String target) {
		String cmd = MessageFormat.format(BlockConstant.BLOCK_ADDTARGET_PATH, target);
		LogFactory.info(cmd);
		//return cmd;
		return CmdUtils.executeCmdForString(cmd);
	}

	@Override
	public String addRemoteTarget(Map<String, String> args) {
		String ip = args.get("ip");
		String target = args.get("target");
		
		try {
			String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
					BlockConstant.POOL_SERVICE_PATH,
					BlockConstant.EXPORT_SERVICE_ADDTARGET_NAME, target);
			return HttpUtils.invokeHttpForGet(url);
		} catch (IOException e) {
			LogFactory.error(e.getMessage());
		}
		return null;
	}
	
	@Override
	public String delTarget(String target) {
		String cmd = MessageFormat.format(BlockConstant.BLOCK_DELTARGET_PATH, target);
		LogFactory.info(cmd);
		//return cmd;
		return CmdUtils.executeCmdForString(cmd);
	}
	
	@Override
	public String delRemoteTarget(Map<String, String> args) {
		String ip = args.get("ip");
		String target = args.get("target");
		try {
			String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
					BlockConstant.POOL_SERVICE_PATH,
					BlockConstant.EXPORT_SERVICE_DELTARGET_NAME, target);
			return HttpUtils.invokeHttpForGet(url);
		} catch (IOException e) {
			LogFactory.error(e.getMessage());
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addDevice(Map<String, Object> args) throws IOException {
		String target = args.get("target").toString();
		ArrayList<String> volDevList = (ArrayList<String>) args.get("volDevList");
		
		for (String obj : volDevList) {
			String cmd = MessageFormat.format(BlockConstant.BLOCK_ADDDEVICE_PATH, obj, target);
			LogFactory.info(cmd);
			//return cmd;
			CmdUtils.executeCmdForString(cmd);
		}
		
	}
	
	@Override
	public String addRemoteDevice(Map<String, Object> args) {
		String ip = args.get("ip").toString();
		/*String device = args.get("device");
		String target = args.get("target");*/
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(args);
			LogFactory.info(json);
			String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
					BlockConstant.POOL_SERVICE_PATH,
					BlockConstant.EXPORT_SERVICE_ADDDEVICE_NAME);
			return HttpUtils.invokeHttpForGet(url, json);
		} catch (IOException e) {
			LogFactory.error(e.getMessage());
		}
		return null;
	}
	
	@Override
	public String delDevice(String device, String target) {
		String cmd = MessageFormat.format(BlockConstant.BLOCK_DELDEVICE_PATH, device, target);
		LogFactory.info(cmd);
		//return cmd;
		return CmdUtils.executeCmdForString(cmd);
	}
	
	@Override
	public String delRemoteDevice(Map<String, String> args) {
		String ip = args.get("ip");
		/*String device = args.get("device");
		String target = args.get("target");*/
		
		try {
			String url = HttpUtils.getUrlForParams(ip, Constant.REST_SERVICE_PORT,
					BlockConstant.POOL_SERVICE_PATH,
					BlockConstant.EXPORT_SERVICE_DELDEVICE_NAME, args);
			return HttpUtils.invokeHttpForGet(url);
		} catch (IOException e) {
			LogFactory.error(e.getMessage());
		}
		return null;
	}

	@Override
	public String getVolDevJson() {
		return CmdUtils.executeCmdForString(BlockConstant.BLOCK_VOLDEV_PATH);
	}
	
	@Override
	public String getRemoteVolDev(String ip) {
		try {
			String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
					BlockConstant.POOL_SERVICE_PATH,
					BlockConstant.EXPORT_SERVICE_VOLDEV_NAME);
			return HttpUtils.invokeHttpForGet(url);
		} catch (IOException e) {
			LogFactory.error(e.getMessage());
		}
		return null;
	}
	
	@Override
	public List<BlockUser> queryListBLockUserNames(String poolName,String target) throws Exception {
		// TODO Auto-generated method stub
		String sql = "select bu.userId, bu.userName,bu.passWord,bu.realName,bu.createTime,bu.comment from blockUser bu,blockUserTarget bt where bu.userId=bt.userId and bt.target=?";
		return jdbcDao.queryForList(poolName, sql, new BlockUserMapper(),target);
	}
	
}
