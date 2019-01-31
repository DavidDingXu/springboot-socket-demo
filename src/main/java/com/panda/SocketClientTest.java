package com.panda;

import com.alibaba.fastjson.JSONObject;
import com.panda.utils.socket.client.SocketClient;
import com.panda.utils.socket.dto.ClientSendDto;
import com.panda.utils.socket.enums.FunctionCodeEnum;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author 丁许
 * @date 2019-01-25 14:34
 */
@Slf4j
public class SocketClientTest {

	public static void main(String[] args) {
		ExecutorService clientService = Executors.newCachedThreadPool();
		String userId = "dingxu";
		for (int i = 0; i < 1000; i++) {
			int index = i;
			clientService.execute(() -> {
				try {
					SocketClient client;
					client = new SocketClient(InetAddress.getByName("127.0.0.1"), 60000);
					//登陆
					ClientSendDto dto = new ClientSendDto();
					dto.setFunctionCode(FunctionCodeEnum.LOGIN.getValue());
					dto.setUserId(userId + index);
					client.println(JSONObject.toJSONString(dto));
					ScheduledExecutorService clientHeartExecutor = Executors.newSingleThreadScheduledExecutor(
							r -> new Thread(r, "socket_client+heart_" + r.hashCode()));
					clientHeartExecutor.scheduleWithFixedDelay(() -> {
						try {
							ClientSendDto heartDto = new ClientSendDto();
							heartDto.setFunctionCode(FunctionCodeEnum.HEART.getValue());
							client.println(JSONObject.toJSONString(heartDto));
						} catch (Exception e) {
							log.error("客户端异常,userId:{},exception：{}", userId, e.getMessage());
							client.close();
						}
					}, 0, 5, TimeUnit.SECONDS);
					while (true){

					}
				} catch (Exception e) {
					log.error(e.getMessage());
				}

			});
		}
	}

}
