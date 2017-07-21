package com.cfido.commons.spring.sortedLock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.cfido.commons.spring.sortedLock.INeedSortLockObj;

public class MockNeedLockObj implements INeedSortLockObj {

	private final static AtomicInteger counter = new AtomicInteger();

	private final Lock lock = new ReentrantLock();

	private final String idForSort;

	public MockNeedLockObj() {
		int count = counter.incrementAndGet();

		this.idForSort = "MockNeedLockObj:" + count;
	}

	@Override
	public Lock getLock() {
		return lock;
	}

	@Override
	public String getIdForSort() {
		return this.idForSort;
	}

}
