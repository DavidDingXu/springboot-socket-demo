package com.panda.config;

import com.alibaba.fastjson.JSONObject;
import com.panda.utils.socket.server.SocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 丁许
 * @date 2019-01-24 22:24
 */
@Configuration
@Slf4j
public class SocketServerConfig {

	@Bean
	public SocketServer socketServer() {
		SocketServer socketServer = new SocketServer(60000);
		socketServer.setLoginHandler(userId -> {
			log.info("处理socket用户身份验证,userId:{}", userId);
			//用户名中包含了dingxu则允许登陆
			return userId.contains("dingxu");

		});
		socketServer.setMessageHandler((connection, receiveDto) -> log
				.info("处理socket消息,userId:{},receiveDto:{}", connection.getUserId(),
						JSONObject.toJSONString(receiveDto)));
		socketServer.start();
		return socketServer;
	}
}
