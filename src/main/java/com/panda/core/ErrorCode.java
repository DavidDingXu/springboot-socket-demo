package com.panda.core;

/**
 * 系统错误码，规范系统异常的构造
 *
 * @ClassName：ErrorCode
 * @Description：TODO
 * @author：huangyongfa
 * @date：2017年08月08日
 */
public enum ErrorCode {

	/**
	 * 404找不到出错
	 */
	ERROR404(404),

	/**
	 * 会话超时，请重新登陆
	 */
	ESYS0001(50014),

	/**
	 * 用户鉴权失败
	 */
	ESYS9998(50008),

	/**
	 * 系统内部异常
	 */
	ESYS9999(40001),

	/**
	 * 用户重复登录
	 */
	EUOP0001(50012),

	/**
	 * 参数错误
	 */
	ESYS10000(40002),

	/**
	 * 操作成功!
	 */
	IPER0001(20000);

	private Integer value;

	private ErrorCode(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
