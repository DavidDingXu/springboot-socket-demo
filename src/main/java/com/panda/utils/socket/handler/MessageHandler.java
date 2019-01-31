package com.panda.utils.socket.handler;

import com.panda.utils.socket.dto.ServerReceiveDto;
import com.panda.utils.socket.server.Connection;

/**
 * 处理消息的接口
 *
 * @author 丁许
 */
public interface MessageHandler {

	/**
	 * 获得消息的处理函数
	 *
	 * @param connection 封装了客户端的socket
	 * @param dto        接收到的dto
	 */
	void onReceive(Connection connection, ServerReceiveDto dto);
}