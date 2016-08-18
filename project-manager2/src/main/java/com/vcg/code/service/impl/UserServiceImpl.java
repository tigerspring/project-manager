package com.vcg.code.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vcg.code.dao.UserDao;
import com.vcg.code.model.User;
import com.vcg.code.model.query.UserExample;
import com.vcg.code.service.UserService;
import com.vcg.common.base.BaseServiceImpl;
import com.vcg.common.utils.MD5;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserExample, Integer> implements UserService {

	@Autowired
	private UserDao userDao;

	@Override
	public void setDao() {
		this.baseDao = userDao;
	}

	@Override
	public User login(User user) {
		UserExample userExample = new UserExample();
		String password = MD5.md5(user.getPassword());
		userExample
			.createCriteria()
			.andUsernameEqualTo(user.getUsername())
			.andPasswordEqualTo(password);
		List<User> users = userDao.selectByExample(userExample);
		return users.size() > 0 ? users.get(0) : null;
	}

}