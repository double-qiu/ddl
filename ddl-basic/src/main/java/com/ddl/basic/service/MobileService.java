package com.ddl.basic.service;

import com.ddl.basic.domain.query.MobileQuery;
import com.ddl.basic.domain.vo.MobileVO;

public interface MobileService {

	MobileVO getMobile(MobileQuery mobileQuery) throws Exception;
}
