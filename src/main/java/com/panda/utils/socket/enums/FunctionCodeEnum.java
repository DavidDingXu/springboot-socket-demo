package com.panda.utils.socket.enums;

/**
 * 功能码 0 心跳 1 登陆 2 登出 3 发送消息
 *
 * @author 丁许
 * @date 2019-01-24 15:53
 */
public enum FunctionCodeEnum {
	/**
	 * 心跳
	 */
	HEART(0, "心跳"),
	/**
	 * 用户鉴权
	 */
	LOGIN(1, "登陆"),

	/**
	 * 客户端关闭
	 */
	CLOSE(2, "客户端关闭"),

	/**
	 * 发送消息
	 */
	MESSAGE(3, "发送消息");

	private Integer value;

	private String desc;

	FunctionCodeEnum(Integer value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
