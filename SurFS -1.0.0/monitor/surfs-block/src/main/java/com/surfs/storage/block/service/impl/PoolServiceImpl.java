package com.surfs.storage.block.service.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.autumn.core.log.LogFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surfs.storage.block.model.ZpoolInfo;
import com.surfs.storage.block.service.BlockConstant;
import com.surfs.storage.block.service.PoolService;
import com.surfs.storage.common.util.CmdUtils;
import com.surfs.storage.common.util.Constant;
import com.surfs.storage.common.util.HttpUtils;

@Service("poolService")
public class PoolServiceImpl implements PoolService {
	
	@Override
	public String getZpoolInfoJson() {
		return CmdUtils.executeCmdForString(BlockConstant.BLOCK_POOL_PATH);
	}
	
	@Override
	public String getZpoolStatus() {
		return CmdUtils.executeCmdForString(BlockConstant.BLOCK_POOLSTATUS_PATH);
	}
	
	@Override
	public List<ZpoolInfo> getZpoolInfos() {
		List<ZpoolInfo> list = new ArrayList<>();
		try {
			// local
			String jsonLocal = CmdUtils
					.executeCmdForString(BlockConstant.BLOCK_POOL_PATH);
			ObjectMapper objectMapper = new ObjectMapper();
			ZpoolInfo infoLocal = objectMapper.readValue(jsonLocal,
					new TypeReference<ZpoolInfo>() {});
			list.add(infoLocal);

			// remote
			String jsonRemote = getRemoteStoragePoolJson();
			ZpoolInfo infoRemote = objectMapper.readValue(jsonRemote,
					new TypeReference<ZpoolInfo>() {});
			list.add(infoRemote);
		} catch (Exception e) {
			LogFactory.error(e.getMessage());
		}
		return list;
	}
	
	private String getRemoteStoragePoolJson() throws Exception {
		String ip = CmdUtils.getRemoteIp();
		//String ip = "10.0.33.52";
		String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
				BlockConstant.POOL_SERVICE_PATH,
				BlockConstant.POOL_SERVICE_POOLJSON_NAME);
		return HttpUtils.invokeHttpForGet(url);
	}
	
	@Override
	public String deleteRemoteVol(Map<String, String> args) {
		String ip = args.get("ip");
		String[] zpoolvol = args.get("zpoolvol").split("/");
		
		try {
			String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
					BlockConstant.POOL_SERVICE_PATH,
					BlockConstant.POOL_SERVICE_DELETEVOL_NAME, zpoolvol[0], zpoolvol[1]);
			return HttpUtils.invokeHttpForGet(url);
		} catch (IOException e) {
			LogFactory.trace("deleteRemoteVol error", e);
		}
		return null;
	}

	@Override
	public String deleteVol(String zpool, String vol) {
		LogFactory.info("serivce zpool:" + zpool);
		LogFactory.info("service vol:" + vol);
		String cmd = MessageFormat.format(BlockConstant.BLOCK_DELVOL_PATH, zpool, vol);
		LogFactory.info(cmd);
		//return cmd;
		return CmdUtils.executeCmdForString(cmd);
	}
	
	@Override
	public String addVol(String zpool, String vol, String size) {
		String cmd = MessageFormat.format(BlockConstant.BLOCK_ADDVOL_PATH, zpool, vol, size);
		LogFactory.info(cmd);
		//return cmd;
		return CmdUtils.executeCmdForString(cmd);
	}

	@Override
	public String addRemoteVol(Map<String, String> args) {
		String ip = args.get("ip");
		String zpool = args.get("zpool");
		String vol = args.get("vol");
		String size = args.get("size");
		try {
			String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
					BlockConstant.POOL_SERVICE_PATH,
					BlockConstant.POOL_SERVICE_ADDVOL_NAME, zpool, vol, size);
			return HttpUtils.invokeHttpForGet(url);
		} catch (IOException e) {
			LogFactory.error(e.getMessage());
		}
		return null;
	}
	
	public static void main(String[] args) {
		String cmd = MessageFormat.format("python /root/op_zpool.py --remove {0}/{1}", "test", "sd");
		System.out.println(cmd);
	}
	
}
