package com.cfido.commons.utils.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import com.cfido.commons.beans.others.IFilter;

/**
 * 日志帮助类
 * 
 * @author liangwj
 * 
 */
public class LogUtil {

	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public final static List<String> PACKAGE_FILTER = new LinkedList<>();
	static {
		PACKAGE_FILTER.add("com.linzi");
		PACKAGE_FILTER.add("com.cfido");
		PACKAGE_FILTER.add("game");
	}

	/**
	 * 默认的只显示 com.linzi开头错误信息的 Filter
	 */
	public static IFilter<StackTraceElement> DEFAULT_FILTER = new IFilter<StackTraceElement>() {

		@Override
		public boolean isMatch(StackTraceElement obj) {
			if (obj != null) {
				String className = obj.getClassName();
				for (String name : PACKAGE_FILTER) {
					if (className.startsWith(name)) {
						return true;
					}
				}
			}
			return false;
		}

	};

	/**
	 * 啥啥都显示的的 Filter
	 */
	public static IFilter<StackTraceElement> FILTER_ALL = new IFilter<StackTraceElement>() {

		@Override
		public boolean isMatch(StackTraceElement obj) {
			return true;
		}
	};

	/**
	 * 打印出错详细过程，默认情况是只显示 和com.linzi相关的trace
	 */
	public static void traceError(Log log, Throwable e) {
		log.error(getTraceString(null, e));
	}

	/**
	 * 打印出错详细过程，默认情况是只显示 和com.linzi相关的trace
	 */
	public static void traceError(Log log, Throwable e, String errorMsg) {
		log.error(getTraceString(errorMsg, e));
	}

	/**
	 * 获得到当前执行处的调用过程作为字符串返回，msg是要放到字符串前面的内容，方便直接用log
	 * 
	 * @param msg
	 * @return
	 */
	public static String getStackTrace(String msg) {
		StringBuffer sb = new StringBuffer();
		sb.append(msg);
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		for (int i = 3; i < trace.length; i++) {
			sb.append("\tat ");
			sb.append(trace[i]).append("\n");
		}
		return sb.toString();
	}

	/**
	 * debug时，将long型按时间的格式输出
	 * 
	 * @param time
	 * @return
	 */
	public static String longDateToStr(long time) {
		return DATE_FORMAT.format(new Date(time));
	}

	public static void traceWarn(Log log, String errorMsg) {
		log.warn(getStackTrace(errorMsg));
	}

	public static void traceWarn(Logger log, String errorMsg) {
		log.warn(getStackTrace(errorMsg));
	}

	/**
	 * 获取调用过程，默认情况是只显示 和com.linzi相关的trace
	 */
	public static String getTraceString(String errorMsg, Throwable e) {
		return getTraceString(errorMsg, e, DEFAULT_FILTER);
	}

	public static String getTraceStringOld(String errorMsg, Throwable e) {
		StringWriter w = new StringWriter();
		PrintWriter out = new PrintWriter(w);
		if (!StringUtils.isEmpty(errorMsg)) {
			out.println(errorMsg);
		}
		e.printStackTrace(out);
		return w.toString();
	}

	public static String getTraceString(String errorMsg, Throwable e, IFilter<StackTraceElement> filter) {
		StringWriter w = new StringWriter();
		PrintWriter out = new PrintWriter(w);

		if (!StringUtils.isEmpty(errorMsg)) {
			// 如果有errorMsg，就输入
			out.println(errorMsg);
		}

		// 构建防止重复输出的set
		Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());

		// 递归打印所有的错误信息
		printThrowable(out, e, filter, dejaVu);

		return w.toString();
	}

	/**
	 * 打印 Throwable
	 * 
	 * @param out
	 * @param e
	 * @param filter
	 * @param set
	 */
	private static void printThrowable(PrintWriter out, Throwable e, IFilter<StackTraceElement> filter, Set<Throwable> set) {
		if (set.contains(e)) {
			return;
		}
		set.add(e);

		out.println(e.toString());
		printStackTraceElements(out, e.getStackTrace(), filter);

		Throwable cause = e.getCause();
		if (cause != null) {
			out.print("Cause by:");
			printThrowable(out, cause, filter, set);
		}
	}

	/**
	 * 输出一行错误
	 * 
	 * @param out
	 * @param traces
	 * @param filter
	 */
	private static void printStackTraceElements(PrintWriter out, StackTraceElement[] traces, IFilter<StackTraceElement> filter) {
		if (traces == null) {
			return;
		}

		boolean skip = false;
		for (StackTraceElement trace : traces) {
			if (filter.isMatch(trace)) {
				out.println("\tat " + trace);
				skip = false;
			} else {
				if (!skip) {
					out.println("\t...");
				}
				skip = true;
			}
		}
	}

	/**
	 * 不会抛错的String.format
	 * 
	 * @param format
	 * @param args
	 * @return
	 */
	public static String format(String format, Object... args) {
		try {
			return String.format(format, args);
		} catch (Exception e) {
			StringBuffer sb = new StringBuffer();
			sb.append(format).append(' ').append(e.getMessage()).append('\t');
			for (int i = 0; i < args.length; i++) {
				sb.append(args[i]).append(" ");
			}
			return sb.toString();
		}
	}

	/**
	 * 打印LOG用
	 * 
	 * @param hsql
	 * @param params
	 */
	public static String printSql(String hsql, Object[] params) {
		return SqlUtils.printSql(hsql, params);
	}

	/**
	 * 打印出错详细过程，默认情况是只显示 和com.linzi相关的trace
	 */
	public static void traceError(Logger log, Throwable e) {
		log.error(getTraceString(null, e));
	}

	/**
	 * 打印出错详细过程，默认情况是只显示 和com.linzi相关的trace
	 */
	public static void traceError(Logger log, Throwable e, String errorMsg) {
		log.error(getTraceString(errorMsg, e));
	}
}
