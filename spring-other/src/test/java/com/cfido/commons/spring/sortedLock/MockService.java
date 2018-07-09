package com.cfido.commons.spring.sortedLock;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cfido.commons.spring.sortedLock.ANeedSortLock;
import com.cfido.commons.spring.sortedLock.INeedSortLockObj;

@Service
public class MockService {

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(MockService.class);

	@ANeedSortLock
	public String testMethod(MockNeedLockObj obj, List<INeedSortLockObj> list, MockNeedLockObj[] ary) throws Exception {
		log.debug("此方法需要加锁");
		return "testMethod";
	}

	public void test2() {
		log.debug("此方法不需要加锁");
	}

}
