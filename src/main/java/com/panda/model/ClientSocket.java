package com.panda.model;

import com.panda.utils.socket.client.SocketClient;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author 丁许
 * @date 2019-01-25 10:17
 */
@Data
@AllArgsConstructor
public class ClientSocket {

	private SocketClient socketClient;

	private ScheduledExecutorService clientHeartExecutor;
}
