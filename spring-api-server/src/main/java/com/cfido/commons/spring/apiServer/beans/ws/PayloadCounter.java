package com.cfido.commons.spring.apiServer.beans.ws;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <pre>
 * 流量计数器
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public class PayloadCounter {

	private final AtomicLong counter = new AtomicLong(); // 次数
	private final AtomicLong payload = new AtomicLong(); // 总带宽

	/** 传输的次数 */
	public long getCount() {
		return counter.get();
	}

	/** 使用的带宽 */
	public long getPayload() {
		return payload.get();
	}

	/** 增加使用量 */
	public void addPayload(long payload) {
		this.counter.incrementAndGet(); // 增加次数
		this.payload.addAndGet(payload); // 增加宽度总使用量
	}

	/** 平均带宽 */
	public long getAvg() {
		long count = this.getCount();
		if (count > 0) {
			return this.getPayload() / count;
		} else {
			return 0;
		}
	}

	/** 重置 */
	public void reset() {
		this.counter.set(0);
		this.payload.set(0);
	}
}
