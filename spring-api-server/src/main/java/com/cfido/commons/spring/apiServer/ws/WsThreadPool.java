package com.cfido.commons.spring.apiServer.ws;

import org.springframework.stereotype.Service;

import com.cfido.commons.utils.threadPool.BaseThreadPool;

/**
 * <pre>
 * 线程池
 * </pre>
 * 
 * @author 江凯文
 * 
 */
@Service
public class WsThreadPool extends BaseThreadPool {

	@Override
	protected String getName() {
		return "websock异步线程池";
	}

	@Override
	protected int getPoolSize() {
		// 线程池容量就随便设置一个3吧
		return 3;
	}

	@Override
	protected int getUniqueIdSetInitSize() {
		// 任务排重缓冲区的容量，用于防止重复名字的任务，但超过1000个任务时，后面的就没法排重了
		return 1000;
	}

}
