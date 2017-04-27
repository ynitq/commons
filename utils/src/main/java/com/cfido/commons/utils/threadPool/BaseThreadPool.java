package com.cfido.commons.utils.threadPool;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PreDestroy;

import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.cfido.commons.utils.utils.LogUtil;

/**
 * 一个线程池
 * 
 * @author liangwj
 * 
 */
public abstract class BaseThreadPool {

	class MyTask implements Runnable {
		private final IMyTask task;

		public MyTask(IMyTask task) {
			this.task = task;
		}

		@Override
		public void run() {
			long now = System.currentTimeMillis();
			try {
				this.task.run();
				this.task.afterRun();
			} catch (RuntimeException e) {
				LogUtil.traceError(log, e);
			}
			// 记录任务总数和执行总时间
			long cost = System.currentTimeMillis() - now;

			afterMainTask(task, cost);
		}
	}

	// 要使用基类的log
	private final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(this.getClass());

	/**
	 * 在排队中的任务数量
	 */
	private final AtomicInteger counter = new AtomicInteger();

	/**
	 * 任务总数
	 */
	private final AtomicInteger totalCount = new AtomicInteger();
	/**
	 * 总花费时间
	 */
	private final AtomicLong totalCost = new AtomicLong();

	private long maxCost = 0;

	private long minCost = Long.MAX_VALUE;

	/** 主线程池，处理游戏的各类事件、聊天、发送系统信息等 */
	private final ExecutorService executorService;

	/** 维护任务set的锁 */
	private final Lock taskSetLock = new ReentrantLock();

	/** 任务集合，用于防治重复提交任务 */
	private final Set<String> taskUniqueIdSet;

	public BaseThreadPool() {
		log.info(String.format("初始化线程池:%s, 线程数量:%d", this.getName(), this.getPoolSize()));

		this.executorService = Executors.newFixedThreadPool(this.getPoolSize());
		this.taskUniqueIdSet = new HashSet<>(this.getUniqueIdSetInitSize());
	}

	/**
	 * 增加新的任务
	 * 
	 * @param task
	 *            要增加的任务
	 * @return 如果任务没有重复，可以增加成功就返回真
	 */
	public boolean addNewTask(IMyTask task) {
		String key = task.getUniqueId();
		if (key != null) {
			// 如果该任务有唯一性校验
			this.taskSetLock.lock();
			try {
				if (this.taskUniqueIdSet.contains(key)) {
					if (log.isDebugEnabled()) {
						log.debug(String.format("在线程池 %s 增加任务 %s, 但该任务已经存在", this.getName(), task.toString()));
					}
					// 如果已经存在在任务，就直接返回
					return false;
				} else {
					// 如果没有该任务，就添加到set中
					this.taskUniqueIdSet.add(key);
				}
			} finally {
				this.taskSetLock.unlock();
			}
		}

		counter.incrementAndGet();// 加入任务时，计数器+1
		this.executorService.execute(new MyTask(task));

		if (log.isDebugEnabled()) {
			log.debug(String.format("在线程池 %s 增加任务 %s，待执行任务数量 %d", this.getName(), task.toString(), this.counter.get()));
		}
		return true;
	}

	@ManagedAttribute(description = "任务执行的平均时间(毫秒)")
	public long getAvgTaskCost() {
		long count = this.getFinishedTaskCount();
		if (count == 0) {
			return 0;
		} else {
			return this.totalCost.get() / count;
		}
	}

	@ManagedAttribute(description = "已执行完的任务的数量")
	public int getFinishedTaskCount() {
		return this.totalCount.get();
	}

	@ManagedAttribute(description = "任务执行的最长时间(毫秒)")
	public long getMaxCost() {
		return maxCost;
	}

	@ManagedAttribute(description = "任务执行的最短时间(毫秒)")
	public long getMinCost() {
		if (this.getFinishedTaskCount() == 0) {
			return 0;
		}
		return minCost;
	}

	/**
	 * 获得主线程池正在执行的任务的数量
	 * 
	 * @return
	 */
	@ManagedAttribute(description = "当前未执行完的任务的数量")
	public int getUnFinishTaskCount() {
		return counter.get();
	}

	@PreDestroy
	public void shutdown() {
		int count = this.counter.get();
		log.info("开始停止 " + this.getName() + (count > 0 ? (" 剩余任务:" + count) : ""));
		this.executorService.shutdown();
		try {
			// 5分钟如果不能停止就强行停止
			if (!this.executorService.awaitTermination(10, TimeUnit.MINUTES)) {
				this.executorService.shutdownNow();
				if (!this.executorService.awaitTermination(1, TimeUnit.MINUTES)) {
					log.error(this.getName() + " shutdown 失败");
				}
			}
		} catch (InterruptedException e) {
			LogUtil.traceError(log, e);
			this.executorService.shutdownNow();
		}
	}

	/**
	 * 任务执行完成后，将任务的id从set中移除
	 * 
	 * @param task
	 * @param cost
	 */
	private void afterMainTask(IMyTask task, long cost) {
		String key = task.getUniqueId();
		if (key != null) {
			this.taskSetLock.lock();
			try {
				this.taskUniqueIdSet.remove(key);
			} finally {
				this.taskSetLock.unlock();
			}
		}

		counter.decrementAndGet();// 任务执行完成时，计数器-1

		// 记录时间开销
		totalCost.addAndGet(cost);
		totalCount.incrementAndGet();

		if (this.maxCost < cost) {
			this.maxCost = cost;
		}
		if (this.minCost > cost) {
			this.minCost = cost;
		}
	}

	/**
	 * 线程池的名字，用于显示在日志
	 * 
	 * @return
	 */
	protected abstract String getName();

	/**
	 * 线程池的大小
	 * 
	 * @return
	 */
	protected abstract int getPoolSize();

	/**
	 * 唯一任务ID排重HashSet大小的初始值
	 * 
	 * @return
	 */
	protected abstract int getUniqueIdSetInitSize();

}
