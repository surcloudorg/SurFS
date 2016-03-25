package com.surfs.storage.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.surfs.storage.common.datasource.jdbc.JdbcDao;
import com.surfs.storage.user.dao.mapper.UsersMapper;
import com.surfs.storage.user.model.Users;
import com.surfs.storage.user.service.UsersService;

@Service("usersService")
public class UsersServiceImpl implements UsersService {

	@Autowired
	private JdbcDao jdbcDao;

	@Override
	public int addUsers(String poolName, Users users) throws Exception {
		Object key = jdbcDao
				.insert(poolName,
						"insert into Users(usersName,pwd,realName,comment) values(?,?,?,?)",
						new Object[] { users.getUsersName(), users.getPwd(),
								users.getRealName(), users.getComment() });
		return Integer.parseInt(key.toString());

	}

	@Override
	public int modifyUsers(String poolName, Users users) throws Exception {
		return jdbcDao
				.update(poolName,
						"update Users set usersName=?,pwd=?,realName=?,comment=? where usersId=?",
						new Object[] { users.getUsersName(), users.getPwd(),
								users.getRealName(), users.getComment(),
								users.getUsersId() });
	}

	@Override
	public int removeUsers(String poolName, int usersId) throws Exception {
		return jdbcDao.delete(poolName, "delete from Users where usersId=?",
				usersId);
	}

	@Override
	public List<Users> queryListUsersMount(String poolName, int mountId)
			throws Exception {
		List<Users> list = jdbcDao
				.queryForList(
						poolName,
						"select u.usersId,u.usersName,u.realName,um.permission from Users u, UsersMount um where u.usersId = um.usersId and um.mountId=?",
						new UsersMapper(), mountId);
		return list;
	}

	@Override
	public List<Users> queryAllUsers(String poolName) throws Exception {
		return jdbcDao
				.queryForList(
						poolName,
						"select usersId,usersName,pwd,realName,comment,createTime from Users",
						new UsersMapper());
	}

	@Override
	public int removeUsersRelation(String poolName, int usersId)
			throws Exception {
		return jdbcDao.delete(poolName,
				"delete from UsersMount where usersId=?", usersId);
	}

	@Override
	public void removeUsersAndRelation(String poolName, int usersId)
			throws Exception {
		removeUsersRelation(poolName, usersId);
		removeUsers(poolName, usersId);
	}

	@Override
	public List<Users> queryListUsersNotMount(String poolName, int mountId)
			throws Exception {
		List<Users> list = jdbcDao
				.queryForList(
						poolName,
						"SELECT u.usersId,u.usersName,u.realName FROM Users u WHERE u.usersId NOT IN (SELECT um.usersId FROM UsersMount um WHERE um.mountId=?)",
						new UsersMapper(), mountId);
		return list;
	}

}
