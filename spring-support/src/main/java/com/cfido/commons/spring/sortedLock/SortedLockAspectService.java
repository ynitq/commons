package com.cfido.commons.spring.sortedLock;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.cfido.commons.utils.utils.LogUtil;

/**
 * 用于实现顺序加锁的AOP拦截器，这个是目前比较完美的死锁解决方案
 * 
 * <pre>
 * 这个类即拥有声明切面，也用于实现通知（Advice）
 * 
 * 使用方法：
 * 	必须先生成之类，子类其实什么都不干，之所以要做子类，是为了配合spring通过注解扫描包的包名，
 * 	在子类中加入Aspect的注解，声明这个类是AOP切点
 * 	spring xml配置文件中需要有 [aop:aspectj-autoproxy proxy-target-class="true"]
 * 	具体例子可参考测试用例
 * 
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
@Component
@Aspect
public class SortedLockAspectService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SortedLockAspectService.class);

	private String lastMethodName;

	private Class<?> lastClass;

	private int lastNeedLockObjCount;

	private final AtomicInteger counter = new AtomicInteger();

	/**
	 * 其实我们只拦截环绕
	 * 
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("@annotation(com.cfido.commons.spring.sortedLock.ANeedSortLock)")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		// 执行原来的方法
		this.lastMethodName = joinPoint.getSignature().getName();// 获得最后一次拦截的方法名
		this.lastClass = joinPoint.getTarget().getClass();
		int count = this.counter.incrementAndGet();

		if (log.isDebugEnabled()) {
			log.debug("累计执行 {}次，当前拦截方法 {}", count, joinPoint.toShortString());
		}

		// 先把所有要加锁的对象找出来，放到一个list中
		List<INeedSortLockObj> targetList = new LinkedList<INeedSortLockObj>();
		Object[] args = joinPoint.getArgs();
		for (int i = 0; i < args.length; i++) {
			addNeedSortedObj(args[i], targetList);
		}
		// 对list中需要加锁的对象排重，并且用idForSort排序
		List<INeedSortLockObj> sortedList = this.getSortedList(targetList);
		this.lastNeedLockObjCount = sortedList.size();

		// 执行前，全部加锁
		for (INeedSortLockObj iNeedSortLockObj : sortedList) {
			iNeedSortLockObj.getLock().lock();
		}
		if (log.isDebugEnabled()) {
			if (!sortedList.isEmpty()) {
				log.debug("执行前，{} 个对象加锁", sortedList.size());
			}
		}

		try {
			// 执行目标代码
			Object obj = joinPoint.proceed();

			if (log.isDebugEnabled()) {
				log.debug("返回的内容是 {}", obj);
			}

			return obj;
		} catch (Throwable e) {
			// 如果有异常，必须抛回出去
			throw e;
		} finally {
			// 执行完成后，全部解锁
			for (INeedSortLockObj iNeedSortLockObj : sortedList) {
				iNeedSortLockObj.getLock().unlock();
			}

			if (log.isDebugEnabled()) {
				if (!sortedList.isEmpty()) {
					log.debug("执行后， {} 个对象解锁", sortedList.size());
				}
			}
		}
	}

	/**
	 * 最后拦截的方法名
	 * 
	 * @return
	 */
	public String getLastMethodName() {
		return lastMethodName;
	}

	/**
	 * 最后拦截的类
	 * 
	 * @return
	 */
	public Class<?> getLastClass() {
		return lastClass;
	}

	/**
	 * 最后一次拦截的时候，参数中找到的要加锁的对象的数量
	 * 
	 * @return
	 */
	public int getLastNeedLockObjCount() {
		return lastNeedLockObjCount;
	}

	/**
	 * 合计拦截的次数
	 * 
	 * @return
	 */
	public int getCounter() {
		return counter.get();
	}

	/**
	 * 重置计数器，合计拦截次数清0
	 */
	public void reset() {
		this.counter.set(0);
		this.lastClass = null;
		this.lastMethodName = null;
	}

	private void addNeedSortedObj(Object arg, List<INeedSortLockObj> targetList) {
		// ((ParameterizedType)type).getActualTypeArguments()[0]

		if (targetList == null) {
			throw new IllegalArgumentException();
		}

		if (arg == null) {
			if (log.isDebugEnabled()) {
				log.debug("参数值为空");
			}
			return;
		}

		if (arg.getClass().isArray()) {
			// 如果是数组类的，判断数组的组成类型
			Object[] ary = (Object[]) arg;
			if (ary.length > 0) {
				Object first = ary[0];
				if (first instanceof INeedSortLockObj) {
					for (int i = 0; i < ary.length; i++) {
						targetList.add((INeedSortLockObj) ary[i]);
					}
					if (log.isDebugEnabled()) {
						log.debug(LogUtil.format("参数是INeedSortLockObj[]，找到%d个", ary.length));
					}
				}
			}

		} else if (arg instanceof java.util.List) {
			// 如果是List类的，看看第一个内容是否是 INeedSortLockObj
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) arg;
			if (!list.isEmpty()) {
				Object object = list.get(0);
				if (object instanceof INeedSortLockObj) {
					for (Object obj : list) {
						targetList.add((INeedSortLockObj) obj);
					}
					if (log.isDebugEnabled()) {
						log.debug(LogUtil.format("参数是List<INeedSortLockObj>，找到%d个", list.size()));
					}
				}
			}

		} else if (arg instanceof INeedSortLockObj) {
			targetList.add((INeedSortLockObj) arg);
			if (log.isDebugEnabled()) {
				log.debug("参数就是INeedSortLockObj");
			}
		}
	}

	/**
	 * 排重，并排序
	 * 
	 * @param targetList
	 * @return
	 */
	private List<INeedSortLockObj> getSortedList(List<INeedSortLockObj> targetList) {
		List<INeedSortLockObj> res = new LinkedList<INeedSortLockObj>();
		if (!targetList.isEmpty()) {
			Set<String> set = new HashSet<String>();// 排重用的set,只管key
			for (INeedSortLockObj obj : targetList) {
				String key = obj.getIdForSort();
				if (key != null) {
					// idForSort不能为空，否则不加锁
					if (!set.contains(key)) {
						// 排重
						res.add(obj);
						set.add(key);
					}
				}
			}

			Collections.sort(res, new Comparator<INeedSortLockObj>() {

				@Override
				public int compare(INeedSortLockObj o1, INeedSortLockObj o2) {
					return o1.getIdForSort().compareTo(o2.getIdForSort());
				}
			});
		}

		return res;
	}

	@PostConstruct
	protected void init() {
		log.info("启动排序加锁的AOP服务");
	}

}
