package com.panda.service;

/**
 * @author 丁许
 * @date 2019-01-25 9:49
 */
public interface SocketClientService {

	/**
	 * 开始一个socket客户端
	 *
	 * @param userId 用户id
	 */
	void startOneClient(String userId);

	void closeOneClient(String userId);
}
