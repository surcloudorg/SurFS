package com.surfs.storage.user.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.surfs.storage.common.datasource.jdbc.JdbcDao;
import com.surfs.storage.user.dao.mapper.MountMapper;
import com.surfs.storage.user.model.Mount;
import com.surfs.storage.user.service.MountService;

@Service("mountService")
public class MountServiceImpl implements MountService {

	@Autowired
	private JdbcDao jdbcDao;

	@Override
	public int addMount(String poolName, Mount mount) throws Exception {
		// MB---->bit
		long quota = mount.getQuota() * 1048576 * 1024;
		Object key = jdbcDao.insert(poolName,
				"insert into Mount(path,quota) values(?,?)", new Object[] {
						mount.getPath(), quota });
		return Integer.parseInt(key.toString());
	}

	@Override
	public int modifyMount(String poolName, Mount mount) throws Exception {
		// MB---->bit
		long quota = mount.getQuota() * 1048576 * 1024;
		return jdbcDao.update(poolName,
				"update Mount set path=?,quota=? where mountId=?",
				new Object[] { mount.getPath(), quota, mount.getMountId() });
	}

	@Override
	public int removeMount(String poolName, int mountId) throws Exception {
		return jdbcDao.delete(poolName, "delete from Mount where mountId=?", mountId);
	}

	@Override
	public List<Mount> queryAllMount(String poolName) throws Exception {
		List<Mount> list = jdbcDao.queryForList(poolName,
				"select mountId,path,quota,createTime from Mount", new MountMapper());
		return list;
	}

	@Override
	public void addUsersMount(String poolName, List<Map<String, String>> listMap)
			throws Exception {
		for (Map<String, String> map : listMap) {
			jdbcDao.insert(
					poolName,
					"insert into UsersMount(usersId,mountId,permission) values(?,?,?)",
					new Object[] { Integer.parseInt(map.get("usersId")),
							Integer.parseInt(map.get("mountId")),
							map.get("permission") });
		}
	}

	@Override
	public void editUsersMount(String poolName,
			List<Map<String, String>> listMap) throws Exception {
		String mountId = listMap.get(0).get("mountId");
		jdbcDao.delete(poolName, "delete from UsersMount where mountId=?", Integer.parseInt(mountId));
		
		for (Map<String, String> map : listMap) {
			String usersId = map.get("usersId");
			if (usersId != null)
				jdbcDao.insert(poolName, "insert into UsersMount(usersId,mountId,permission) values(?,?,?)",
						new Object[] { Integer.parseInt(usersId), Integer.parseInt(map.get("mountId")), map.get("permission") });
		}
		
	}

}
