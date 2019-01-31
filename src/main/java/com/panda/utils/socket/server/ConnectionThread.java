package com.panda.utils.socket.server;

import com.alibaba.fastjson.JSONObject;
import com.panda.utils.socket.dto.ServerReceiveDto;
import com.panda.utils.socket.dto.ServerSendDto;
import com.panda.utils.socket.enums.FunctionCodeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

/**
 * 每一个client连接开一个线程
 *
 * @author 丁许
 */
@Slf4j
@Data
public class ConnectionThread extends Thread {

	/**
	 * 客户端的socket
	 */
	private Socket socket;

	/**
	 * 服务socket
	 */
	private SocketServer socketServer;

	/**
	 * 封装的客户端连接socket
	 */
	private Connection connection;

	/**
	 * 判断当前连接是否运行
	 */
	private boolean isRunning;

	public ConnectionThread(Socket socket, SocketServer socketServer) {
		this.socket = socket;
		this.socketServer = socketServer;
		connection = new Connection(socket, this);
		Date now = new Date();
		connection.setCreateTime(now);
		connection.setLastOnTime(now);
		isRunning = true;
	}

	@Override
	public void run() {
		while (isRunning) {
			// Check whether the socket is closed.
			if (socket.isClosed()) {
				isRunning = false;
				break;
			}
			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String message;
				while ((message = reader.readLine()) != null) {
					log.info("服务端收到消息：" + message);
					ServerReceiveDto receiveDto;
					try {
						receiveDto = JSONObject.parseObject(message, ServerReceiveDto.class);
					} catch (Exception e) {
						ServerSendDto dto = new ServerSendDto();
						dto.setStatusCode(999);
						dto.setErrorMessage("data error");
						connection.println(JSONObject.toJSONString(dto));
						break;
					}
					Integer functionCode = receiveDto.getFunctionCode();
					if (functionCode.equals(FunctionCodeEnum.HEART.getValue())) {
						//心跳类型
						connection.setLastOnTime(new Date());
						ServerSendDto dto = new ServerSendDto();
						dto.setFunctionCode(FunctionCodeEnum.HEART.getValue());
						connection.println(JSONObject.toJSONString(dto));
					} else if (functionCode.equals(FunctionCodeEnum.LOGIN.getValue())) {
						//登陆，身份验证
						String userId = receiveDto.getUserId();
						if (socketServer.getLoginHandler().canLogin(userId)) {
							connection.setLogin(true);
							connection.setUserId(userId);
							if (socketServer.getExistSocketMap().containsKey(userId)) {
								//存在已登录的用户，发送登出指令并主动关闭该socket
								Connection existConnection = socketServer.getExistSocketMap().get(userId);
								ServerSendDto dto = new ServerSendDto();
								dto.setStatusCode(999);
								dto.setFunctionCode(FunctionCodeEnum.MESSAGE.getValue());
								dto.setErrorMessage("force logout");
								existConnection.println(JSONObject.toJSONString(dto));
								existConnection.getConnectionThread().stopRunning();
								log.error("用户被客户端重入踢出，userId:{}", userId);
							}
							//添加到已登录map中
							socketServer.getExistSocketMap().put(userId, connection);
						} else {
							//用户鉴权失败
							ServerSendDto dto = new ServerSendDto();
							dto.setStatusCode(999);
							dto.setFunctionCode(FunctionCodeEnum.MESSAGE.getValue());
							dto.setErrorMessage("user valid failed");
							connection.println(JSONObject.toJSONString(dto));
							log.error("用户鉴权失败,userId:{}", userId);
						}
					} else if (functionCode.equals(FunctionCodeEnum.MESSAGE.getValue())) {
						//发送一些其他消息等
						socketServer.getMessageHandler().onReceive(connection, receiveDto);
					} else if (functionCode.equals(FunctionCodeEnum.CLOSE.getValue())) {
						//主动关闭客户端socket
						log.info("客户端主动登出socket");
						this.stopRunning();
					}

				}
			} catch (IOException e) {
				log.error("ConnectionThread.run failed. IOException:{}", e.getMessage());
				this.stopRunning();
			}
		}
	}

	public void stopRunning() {
		if (this.connection.isLogin()) {
			log.info("停止一个socket连接,ip:{},userId:{}", this.socket.getInetAddress().toString(),
					this.connection.getUserId());
		} else {
			log.info("停止一个还未身份验证的socket连接,ip:{}", this.socket.getInetAddress().toString());
		}
		isRunning = false;
		try {
			socket.close();
		} catch (IOException e) {
			log.error("ConnectionThread.stopRunning failed.exception:{}", e);
		}
	}
}