package com.cfido.commons.spring.redis;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;

/**
 * <pre>
 * 用于反映ASyncCacheExecuter执行情况的MBean
 * </pre>
 * 
 * @author 梁韦江 2017年4月27日
 */
@ManagedResource(description = "异步Cache执行器，命中率统计")
@ADomainOrder(CommonMBeanDomainNaming.ORDER)
public class ASyncCacheExecuterCounterMBean extends CommonMBeanDomainNaming {
	private final AtomicLong counterForGet = new AtomicLong();
	private final AtomicLong counterForPutIfAbsent = new AtomicLong();
	private final AtomicLong counterForPut = new AtomicLong();
	private final AtomicLong memoryHit = new AtomicLong();
	private final AtomicLong memoryMiss = new AtomicLong();
	private final AtomicLong redisHit = new AtomicLong();
	private final AtomicLong redisMiss = new AtomicLong();
	private final AtomicLong timeForGet = new AtomicLong();
	private final AtomicLong timeForPut = new AtomicLong();

	@ManagedAttribute(description = "get执行次数")
	public long getCountForGet() {
		return this.counterForGet.get();
	}

	@ManagedAttribute(description = "put执行次数")
	public long getCountForPut() {
		return this.counterForPut.get();
	}

	@ManagedAttribute(description = "平均每次get花费时间(毫秒)")
	public long getAvgTimeForGet() {
		long times = this.counterForGet.get();
		if (times == 0) {
			return 0;
		}

		long total = this.timeForGet.get();
		return total / times;
	}

	@ManagedAttribute(description = "平均每次put花费时间(毫秒)")
	public long getAvgTimeForPut() {
		long times = this.counterForPut.get();
		if (times == 0) {
			return 0;
		}

		long total = this.timeForPut.get();
		return total / times;
	}

	@ManagedAttribute(description = "内存Cache 命中次数")
	public long getMemoryHit() {
		return memoryHit.get();
	}

	@ManagedAttribute(description = "内存Cache miss次数")
	public long getMemoryMiss() {
		return this.memoryMiss.get();
	}

	@ManagedAttribute(description = "Redis Cache miss次数")
	public long getRedisMiss() {
		return this.redisMiss.get();
	}

	@ManagedAttribute(description = "Redis Cache 命中次数")
	public long getRedisHit() {
		return this.redisHit.get();
	}

	public void incrementPutIfAbsent() {
		this.counterForPutIfAbsent.incrementAndGet();

	}

	public void incrementMemoryMiss() {
		this.memoryMiss.incrementAndGet();
	}

	public void incrementMemoryHit() {
		this.memoryHit.incrementAndGet();

	}

	public void incrementRedisMiss() {
		this.redisMiss.incrementAndGet();

	}

	public void incrementRedisHit() {
		this.redisHit.incrementAndGet();
	}

	public void countGet(long time) {
		this.counterForGet.incrementAndGet();
		this.timeForGet.addAndGet(time);
	}

	public void countPut(long time) {
		this.timeForPut.addAndGet(time);
		this.counterForPut.incrementAndGet();
	}

	@ManagedOperation(description = "重置所有的计数器")
	public void resetAllCounter() {

		this.counterForGet.set(0);
		this.counterForPut.set(0);
		this.counterForPutIfAbsent.set(0);
		this.memoryHit.set(0);
		this.memoryMiss.set(0);
		this.redisHit.set(0);
		this.redisMiss.set(0);
		this.timeForGet.set(0);
		this.timeForPut.set(0);
	}
}
