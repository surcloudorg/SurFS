package com.surfs.storage.block.service.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autumn.core.log.LogFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surfs.storage.block.dao.mapper.BlockUserMapper;
import com.surfs.storage.block.dao.mapper.BlockUserTargetMapper;
import com.surfs.storage.block.model.BlockUser;
import com.surfs.storage.block.model.BlockUserTarget;
import com.surfs.storage.block.service.BlockConstant;
import com.surfs.storage.block.service.BlockUserService;
import com.surfs.storage.common.datasource.jdbc.JdbcDao;
import com.surfs.storage.common.util.CmdUtils;
import com.surfs.storage.common.util.Constant;
import com.surfs.storage.common.util.HttpUtils;

@Service("BlockUserService")
public class BlockUserServiceImpl implements BlockUserService {

	@Autowired
	private JdbcDao jdbcDao;

	@Override
	public List<BlockUser> queryAllBlockUser(String poolName) throws Exception {
		// TODO Auto-generated method stub
		String sql = "SELECT userId,userName,passWord,realName,comment,createTime FROM blockUser";

		return jdbcDao.queryForList(poolName, sql, new BlockUserMapper());
	}

	@Override
	public int addBlockUser(String poolName, BlockUser blockUser)
			throws Exception {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO blockUser(userName,passWord,realName,comment) VALUES(?,?,?,?)";
		Object key = jdbcDao.insert(poolName, sql, 
									new Object[] {
										blockUser.getUserName(),
										blockUser.getPassWord(),
										blockUser.getRealName(),
										blockUser.getComment(),
									});
		return Integer.parseInt(key.toString());
	}

	@Override
	public int modifyBlockUser(String poolName, BlockUser blockUser)
			throws Exception {
		// TODO Auto-generated method stub
		String sql = "UPDATE blockUser SET userName=?,passWord=?,realName=?,comment=? WHERE userId=?";
		return jdbcDao.update(poolName, sql, new Object[]{
				blockUser.getUserName(),
				blockUser.getPassWord(),
				blockUser.getRealName(),
				blockUser.getComment(),
				blockUser.getUserId()
		});
	}

	@Override
	public int removeBlockUser(String poolName, int userId) throws Exception {
		// TODO Auto-generated method stub
		String sql = "DELETE FROM blockUser WHERE userId=?";
		return jdbcDao.delete(poolName, sql, userId);
	}

	@Override
	public List<BlockUser> queryListBlockUserTarget(String poolName, String target)
			throws Exception {
		// TODO Auto-generated method stub
		String sql = "SELECT bu.userId,bu.userName,bu.realName,bt.target FROM blockUser bu,blockUserTarget bt WHERE bu.userId=bt.userId AND bt.target=? ";
		List<BlockUser> list = jdbcDao.queryForList(poolName, sql,new BlockUserMapper(),target);
		return list;
	}

	@Override
	public List<BlockUser> queryListBlockUserNotTarget(String poolName, String target)
			throws Exception {
		// TODO Auto-generated method stub
		String sql = "SELECT bu.userId,bu.userName,bu.passWord,bu.realName FROM blockUser bu WHERE bu.userId NOT IN(SELECT bt.userId FROM blockUserTarget bt WHERE bt.target=?)";
		List<BlockUser> list = jdbcDao.queryForList(poolName, sql, new BlockUserMapper(), target);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String addRemoteBlockUserTarget(
			Map<String, Object> map) throws Exception {
		String ip = map.get("currentIp").toString();
		List<Map<String, String>> blockUserTargetList = (List<Map<String, String>>) map.get("blockUserTargetList");
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(blockUserTargetList);
		try {
			String url = HttpUtils.getUrl(ip, Constant.REST_SERVICE_PORT,
					BlockConstant.POOL_SERVICE_PATH,
					BlockConstant.EXPORT_SERVICE_ADDBLOCKUSER_NAME);
			return HttpUtils.invokeHttpForGet(url, json);
		} catch (IOException e) {
			LogFactory.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void addBlockUserTarget(String poolName, 
			List<Map<String, String>> listMap) throws Exception {
		// TODO Auto-generated method stub
		for(Map<String, String> map : listMap){
			jdbcDao.insert(
					poolName, 
					"INSERT INTO blockUserTarget(userId,target)VALUES(?,?)", 
					new Object[]{
							Integer.parseInt(map.get("userId")),
							map.get("target")
					});
			
			String cmd = MessageFormat.format(
					BlockConstant.BLOCK_ADDDEVICE_PATH_OAUTH,
					map.get("target"), map.get("userName"), map.get("passWord"));
			LogFactory.info(cmd);
			// return cmd;
			CmdUtils.executeCmdForString(cmd);
		}
	}

	@Override
	public List<BlockUserTarget> queryAllBlockUserTarget(String poolName)
			throws Exception {
		// TODO Auto-generated method stub
		String sql = "SELECT userTargetId,userId,targetId,permission FROM blockUserTarget";
		return jdbcDao.queryForList(poolName, sql,new BlockUserTargetMapper());
	}

	@Override
	public int removeBlockUserTarget(String poolName,Map<String,String> args) throws Exception {
		// TODO Auto-generated method stub
		String userId = args.get("userId");
		String target = args.get("target");
		String sql = "delete from blockUserTarget where userId=? and target=?";
		return jdbcDao.delete(poolName, sql,userId,target);
	}

	@Override
	public List<BlockUserTarget> queryBlockUser(String poolName, int userId)
			throws Exception {
		// TODO Auto-generated method stub
		String sql = "select * from blockUserTarget where userId = ?";
		return jdbcDao.queryForList(poolName, sql, new BlockUserTargetMapper(), userId);
	}
}
