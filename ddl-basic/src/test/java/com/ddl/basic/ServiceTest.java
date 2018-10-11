package com.ddl.basic;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ddl.basic.domain.entity.User;
import com.ddl.basic.domain.query.MobileQuery;
import com.ddl.basic.service.MobileService;
import com.ddl.basic.service.UserService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
public class ServiceTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceTest.class);
  
    @Autowired
	private MobileService mobileService;
	@Autowired
	private Properties properties;
	@Autowired
	private UserService userService;
    
    // 在测试开始前初始化工作    
    @Before
    public void setup() {    
    }    
    
    @Test
    public void getMobileTest() throws Exception {
    	String iphone = "17370038143";
    	MobileQuery query = new MobileQuery(properties.getMobileUrl(),iphone,properties.getMobileKey());
        try {
        	LOGGER.info("Mobile：" + mobileService.getMobile(query));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @Test
    public void insertBatchTest()  throws Exception {
    	Long start = System.currentTimeMillis();
    	List<User> recordList = new ArrayList<User>(10000);
    	for (int i = 10001; i < 20001; i++) {
    		User user = new User();
    		user.setUserId(i);
    		user.setUserName("testBatch"+i);
    		user.setPassword("pwdBatch"+i);
    		user.setPhone("13831339199");
    		recordList.add(user);
		}
    	userService.batchAddUser(recordList);
    	Long end = System.currentTimeMillis();
    	LOGGER.info("batch方式10000条数据共耗时:" + (end - start ) + "ms");
    }
}
