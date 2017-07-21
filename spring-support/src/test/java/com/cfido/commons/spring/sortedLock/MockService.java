package com.cfido.commons.spring.sortedLock;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cfido.commons.spring.sortedLock.ANeedSortLock;
import com.cfido.commons.spring.sortedLock.INeedSortLockObj;

@Service
public class MockService {

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(MockService.class);

	@ANeedSortLock
	public void testMethod(MockNeedLockObj obj, List<INeedSortLockObj> list, MockNeedLockObj[] ary) throws Exception {
		log.debug("此方法需要加锁");
	}

	public void test2() {
		log.debug("此方法不需要加锁");
	}

}
