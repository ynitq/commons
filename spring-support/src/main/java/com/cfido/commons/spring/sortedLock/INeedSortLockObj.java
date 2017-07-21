package com.cfido.commons.spring.sortedLock;

import java.util.concurrent.locks.Lock;

/**
 * 
 * 实现了这个接口的类，表示可用AOP方式排序加锁，可完美避免死锁
 * 
 * @author liangwj
 * 
 */
public interface INeedSortLockObj {

	/**
	 * 获得用于加锁的锁
	 * 
	 * @return
	 */
	Lock getLock();

	/**
	 * 获得用于排序的字符串，如果为空，将不参加排序
	 * 
	 * @return
	 */
	String getIdForSort();
}
