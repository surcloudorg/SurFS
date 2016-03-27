/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.user.service;

import java.util.List;

import com.surfs.storage.user.model.Users;

public interface UsersService {

	public int addUsers(String poolName, Users users) throws Exception;

	public int modifyUsers(String poolName, Users users) throws Exception;

	public int removeUsers(String poolName, int usersId) throws Exception;

	public int removeUsersRelation(String poolName, int usersId)
			throws Exception;

	public void removeUsersAndRelation(String poolName, int usersId)
			throws Exception;

	public List<Users> queryListUsersMount(String poolName, int mountId)
			throws Exception;
	
	public List<Users> queryListUsersNotMount(String poolName, int mountId)
			throws Exception;

	public List<Users> queryAllUsers(String poolName) throws Exception;

}
