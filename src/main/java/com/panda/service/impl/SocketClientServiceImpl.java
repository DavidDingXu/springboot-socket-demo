package com.panda.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.core.ServiceException;
import com.panda.model.ClientSocket;
import com.panda.service.SocketClientService;
import com.panda.utils.socket.client.SocketClient;
import com.panda.utils.socket.constants.SocketConstant;
import com.panda.utils.socket.dto.ClientSendDto;
import com.panda.utils.socket.dto.ServerSendDto;
import com.panda.utils.socket.enums.FunctionCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author 丁许
 * @date 2019-01-25 9:50
 */
@Service
@Slf4j
public class SocketClientServiceImpl implements SocketClientService {

	/**
	 * 全局缓存，用于存储已存在的socket客户端连接
	 */
	public static ConcurrentMap<String, ClientSocket> existSocketClientMap = new ConcurrentHashMap<>();


	@Resource(name = "clientTaskPool")
	private ThreadPoolTaskExecutor clientExecutor;

	@Resource(name = "clientMessageTaskPool")
	private ThreadPoolTaskExecutor messageExecutor;

	@Override
	public void startOneClient(String userId) {
		if (existSocketClientMap.containsKey(userId)) {
			throw new ServiceException("该用户已登陆");
		}
		//异步创建socket
		clientExecutor.execute(() -> {
			//新建一个socket连接
			SocketClient client;
			try {
				client = new SocketClient(InetAddress.getByName("127.0.0.1"), 60000);
			} catch (UnknownHostException e) {
				throw new ServiceException("socket新建失败");
			}
			client.setLastOnTime(new Date());

			ScheduledExecutorService clientHeartExecutor = Executors
					.newSingleThreadScheduledExecutor(r -> new Thread(r, "socket_client_heart_" + r.hashCode()));
			ClientSocket clientSocket = new ClientSocket(client,clientHeartExecutor);
			//登陆
			ClientSendDto dto = new ClientSendDto();
			dto.setFunctionCode(FunctionCodeEnum.LOGIN.getValue());
			dto.setUserId(userId);
			client.println(JSONObject.toJSONString(dto));
			messageExecutor.submit(() -> {
				try {
					String message;
					while ((message = client.readLine()) != null) {
						log.info("客户端:{}，获得消息：{}", userId, message);
						ServerSendDto serverSendDto;
						try {
							serverSendDto = JSONObject.parseObject(message, ServerSendDto.class);
						} catch (Exception e) {
							ClientSendDto sendDto = new ClientSendDto();
							sendDto.setFunctionCode(FunctionCodeEnum.MESSAGE.getValue());
							sendDto.setMessage("data error");
							client.println(JSONObject.toJSONString(sendDto));
							break;
						}
						Integer functionCode = serverSendDto.getFunctionCode();
						if (functionCode.equals(FunctionCodeEnum.HEART.getValue())) {
							//心跳类型
							client.setLastOnTime(new Date());
						}
					}
				} catch (Exception e) {
					log.error("客户端异常,userId:{},exception：{}", userId, e.getMessage());
					client.close();
					existSocketClientMap.remove(userId);
				}
			});
			clientHeartExecutor.scheduleWithFixedDelay(() -> {
				try {

					Date lastOnTime = client.getLastOnTime();
					long heartDuration = (new Date()).getTime() - lastOnTime.getTime();
					if (heartDuration > SocketConstant.HEART_RATE) {
						//心跳超时,关闭当前线程
						log.error("心跳超时");
						throw new Exception("服务端已断开socket");
					}
					ClientSendDto heartDto = new ClientSendDto();
					heartDto.setFunctionCode(FunctionCodeEnum.HEART.getValue());
					client.println(JSONObject.toJSONString(heartDto));
				} catch (Exception e) {
					log.error("客户端异常,userId:{},exception：{}", userId, e.getMessage());
					client.close();
					existSocketClientMap.remove(userId);
					clientHeartExecutor.shutdown();
				}

			}, 0, 5, TimeUnit.SECONDS);
			existSocketClientMap.put(userId, clientSocket);
		});
	}

	@Override
	public void closeOneClient(String userId) {
		if (!existSocketClientMap.containsKey(userId)) {
			throw new ServiceException("该用户未登陆，不能关闭");
		}
		ClientSocket clientSocket = existSocketClientMap.get(userId);
		clientSocket.getClientHeartExecutor().shutdown();
		clientSocket.getSocketClient().close();
		existSocketClientMap.remove(userId);
	}
}
