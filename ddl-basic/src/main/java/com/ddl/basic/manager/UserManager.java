package com.ddl.basic.manager;

import java.util.List;

import com.ddl.basic.domain.entity.User;


public interface UserManager {
	
	Integer batchAddUser( List<User> recordList)  throws Exception;
}
