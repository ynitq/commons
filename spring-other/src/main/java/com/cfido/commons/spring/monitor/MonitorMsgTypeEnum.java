package com.cfido.commons.spring.monitor;

/**
 * <pre>
 * 向监控服务器汇报数据时，信息的类型
 * </pre>
 * 
 * @author 梁韦江 2016年12月19日
 */
public enum MonitorMsgTypeEnum {
	START(0, "启动"),
	ERROR(1, "错误"),
	WARNING(2, "警告"),
	UNKNOW(-100, "未知");
	
	public final int code;
	public final String desc;

	private MonitorMsgTypeEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public static MonitorMsgTypeEnum parse(int value) {
		MonitorMsgTypeEnum res = UNKNOW;
		for (MonitorMsgTypeEnum item : MonitorMsgTypeEnum.values()) {
			if (item.code == value) {
				res = item;
				break;
			}
		}
		return res;
	}

}
