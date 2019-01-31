package com.panda.utils.socket.server;

import com.alibaba.fastjson.JSONObject;
import com.panda.utils.socket.constants.SocketConstant;
import com.panda.utils.socket.dto.ServerSendDto;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
class ListeningThread extends Thread {

	private SocketServer socketServer;

	private ServerSocket serverSocket;

	private boolean isRunning;

	public ListeningThread(SocketServer socketServer) {
		this.socketServer = socketServer;
		this.serverSocket = socketServer.getServerSocket();
		isRunning = true;
		log.info("socket服务端开始监听");
	}

	@Override
	public void run() {
		while (isRunning) {
			if (serverSocket.isClosed()) {
				isRunning = false;
				break;
			}
			try {
				Socket socket;
				socket = serverSocket.accept();
				if (socketServer.getExistConnectionThreadList().size() > SocketConstant.MAX_SOCKET_THREAD_NUM) {
					//超过线程数量
					PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
					ServerSendDto dto = new ServerSendDto();
					dto.setStatusCode(999);
					dto.setErrorMessage("已超过连接最大数限制，请稍后再试");
					writer.println(JSONObject.toJSONString(dto));
					socket.close();
				}
				//设置超时时间为5s（有心跳机制了不需要设置）
				//				socket.setSoTimeout(5 * 1000);
				ConnectionThread connectionThread = new ConnectionThread(socket, socketServer);
				socketServer.getExistConnectionThreadList().add(connectionThread);
				//todo:这边最好用线程池
				connectionThread.start();
			} catch (IOException e) {
				log.error("ListeningThread.run failed,exception:{}", e.getMessage());
			}
		}
	}

	/**
	 * 关闭所有的socket客户端连接的线程
	 */
	public void stopRunning() {
		for (ConnectionThread currentThread : socketServer.getExistConnectionThreadList()) {
			currentThread.stopRunning();
		}
		isRunning = false;
	}
} 