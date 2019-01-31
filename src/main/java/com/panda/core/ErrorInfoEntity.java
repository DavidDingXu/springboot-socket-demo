package com.panda.core;

/**
 * Created With User-Center
 *
 * @author ChenHao
 * @date 2018/7/5
 * Target
 */
public interface ErrorInfoEntity {

	/**
	 * 获取错误信息
	 *
	 * @return 错误信息
	 */
	String getErrorMsg();

	/**
	 * 获取错误码
	 *
	 * @return 错误码
	 */
	Integer getErrorCode();
}
