package com.panda.utils.socket.handler;

/**
 * @author 丁许
 * @date 2019-01-23 22:28
 */
public interface LoginHandler {

	/**
	 * client登陆的处理函数
	 *
	 * @param userId 用户id
	 *
	 * @return 是否验证通过
	 */
	boolean canLogin(String userId);
}
