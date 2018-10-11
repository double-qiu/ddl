package com.ddl.basic.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ddl.basic.dao.UserDao;
import com.ddl.basic.domain.entity.User;
import com.ddl.basic.manager.UserManager;
import com.ddl.basic.service.UserService;
import com.github.pagehelper.PageHelper;

@Service(value = "userService")
public class UserServiceImpl  implements UserService {
	
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserManager userManager;

    @Override
    public int addUser(User user) {

        return userDao.insertSelective(user);
    }

    @Override
    public Integer batchAddUser( List<User> recordList) throws Exception {
        return userManager.batchAddUser(recordList);
    }
    /*
    * 这个方法中用到了我们开头配置依赖的分页插件pagehelper
    * 很简单，只需要在service层传入参数，然后将参数传递给一个插件的一个静态方法即可；
    * pageNum 开始页数
    * pageSize 每页显示的数据条数
    * */
    @Override
    public List<User> findAllUser(int pageNum, int pageSize) {
        //将参数传给这个方法就可以实现物理分页了，非常简单。
        PageHelper.startPage(pageNum, pageSize);
        return userDao.selectAllUser();
    }
}
