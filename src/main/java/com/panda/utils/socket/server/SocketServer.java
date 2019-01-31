package com.panda.utils.socket.server;

import com.panda.utils.socket.constants.SocketConstant;
import com.panda.utils.socket.handler.LoginHandler;
import com.panda.utils.socket.handler.MessageHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 丁许
 */
@Slf4j
@Data
public class SocketServer {

	private ServerSocket serverSocket;

	/**
	 * 服务监听主线程
	 */
	private ListeningThread listeningThread;

	/**
	 * 消息处理器
	 */
	private MessageHandler messageHandler;

	/**
	 * 登陆处理器
	 */
	private LoginHandler loginHandler;

	/**
	 * 用户扫已有的socket处理线程
	 * 1. 没有的线程不引用
	 * 2. 关注是否有心跳
	 * 3. 关注是否超过登陆时间
	 */
	private ScheduledExecutorService scheduleSocketMonitorExecutor = Executors
			.newSingleThreadScheduledExecutor(r -> new Thread(r, "socket_monitor_" + r.hashCode()));

	/**
	 * 存储只要有socket处理的线程
	 */
	private List<ConnectionThread> existConnectionThreadList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * 中间list，用于遍历的时候删除
	 */
	private List<ConnectionThread> noConnectionThreadList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * 存储当前由用户信息活跃的的socket线程
	 */
	private ConcurrentMap<String, Connection> existSocketMap = new ConcurrentHashMap<>();

	public SocketServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			log.error("本地socket服务启动失败.exception:{}", e);
		}
	}

	/**
	 * 开一个线程来开启本地socket服务，开启一个monitor线程
	 */
	public void start() {
		listeningThread = new ListeningThread(this);
		listeningThread.start();
		//每隔1s扫一次ThreadList
		scheduleSocketMonitorExecutor.scheduleWithFixedDelay(() -> {
			Date now = new Date();
			//删除list中没有用的thread引用
			existConnectionThreadList.forEach(connectionThread -> {
				if (!connectionThread.isRunning()) {
					noConnectionThreadList.add(connectionThread);
				} else {
					//还在运行的线程需要判断心跳是否ok以及是否身份验证了
					Date lastOnTime = connectionThread.getConnection().getLastOnTime();
					long heartDuration = now.getTime() - lastOnTime.getTime();
					if (heartDuration > SocketConstant.HEART_RATE) {
						//心跳超时,关闭当前线程
						log.error("心跳超时");
						connectionThread.stopRunning();
					}
					if (!connectionThread.getConnection().isLogin()) {
						//还没有用户登陆成功
						Date createTime = connectionThread.getConnection().getCreateTime();
						long loginDuration = now.getTime() - createTime.getTime();
						if (loginDuration > SocketConstant.LOGIN_DELAY) {
							//身份验证超时
							log.error("身份验证超时");
							connectionThread.stopRunning();
						}
					}
				}
			});
			noConnectionThreadList.forEach(connectionThread -> {
				existConnectionThreadList.remove(connectionThread);
				if (connectionThread.getConnection().isLogin()) {
					//说明用户已经身份验证成功了，需要删除map
					this.existSocketMap.remove(connectionThread.getConnection().getUserId());
				}
			});
			noConnectionThreadList.clear();
		}, 0, 1, TimeUnit.SECONDS);
	}

	/**
	 * 关闭本地socket服务
	 */
	public void close() {
		try {
			//先关闭monitor线程，防止遍历list的时候
			scheduleSocketMonitorExecutor.shutdownNow();
			if (serverSocket != null && !serverSocket.isClosed()) {
				listeningThread.stopRunning();
				listeningThread.suspend();
				listeningThread.stop();

				serverSocket.close();
			}
		} catch (IOException e) {
			log.error("SocketServer.close failed.exception:{}", e);
		}
	}

}