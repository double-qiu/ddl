package com.ddl.basic.manager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ddl.basic.dao.UserDao;
import com.ddl.basic.domain.entity.User;
import com.ddl.basic.manager.UserManager;
import com.ddl.basic.manager.batch.AbstractManager;


@Component
public class UserManagerImpl extends AbstractManager implements UserManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserManagerImpl.class);
	
	@Override
	@Transactional
	public Integer batchAddUser(List<User> recordList) throws Exception {
		  int total = 0;
	        total += addDataParamFork(UserDao.class, recordList);
	        if (total == 0) {
	            LOGGER.warn("数据处理异常");
	        }
	        return total;
	}
}
