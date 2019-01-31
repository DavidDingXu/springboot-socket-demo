package com.panda.utils.socket.server;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import static com.panda.utils.socket.constants.SocketConstant.RETRY_COUNT;

/**
 * 封装socket添加println方法
 *
 * @author 丁许
 */
@Slf4j
@Data
public class Connection {

	/**
	 * 当前的socket连接实例
	 */
	private Socket socket;

	/**
	 * 当前连接线程
	 */
	private ConnectionThread connectionThread;

	/**
	 * 当前连接是否登陆
	 */
	private boolean isLogin;

	/**
	 * 存储当前的user信息
	 */
	private String userId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 最后一次更新时间，用于判断心跳
	 */
	private Date lastOnTime;

	public Connection(Socket socket, ConnectionThread connectionThread) {
		this.socket = socket;
		this.connectionThread = connectionThread;
	}

	public void println(String message) {
		int count = 0;
		PrintWriter writer;
		do {
			try {
				writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
				writer.println(message);
				break;
			} catch (IOException e) {
				count++;
				if (count >= RETRY_COUNT) {
					//重试多次失败，说明client端socket异常
					this.connectionThread.stopRunning();
				}
			}
			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e1) {
				log.error("Connection.println.IOException interrupt,userId:{}", userId);
			}
		} while (count < 3);
	}
}