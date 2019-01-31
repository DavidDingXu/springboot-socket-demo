package com.panda.utils.socket.client;

import com.alibaba.fastjson.JSONObject;
import com.panda.utils.socket.dto.ClientSendDto;
import com.panda.utils.socket.dto.ServerReceiveDto;
import com.panda.utils.socket.enums.FunctionCodeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author 丁许
 */
@Slf4j
@Data
public class SocketClient {

	private Socket socket;

	private Date lastOnTime;

	public SocketClient(InetAddress ip, int port) {
		try {
			socket = new Socket(ip, port);
			socket.setKeepAlive(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	public void println(String message) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			writer.println(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	/**
	 * This function blocks.
	 *
	 * @return
	 */
	public String readLine() throws Exception {
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return reader.readLine();
	}

	/**
	 * Ready for use.
	 */
	public void close() {
		try {
			// Send a message to tell the server to close the connection.
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			ServerReceiveDto dto = new ServerReceiveDto();
			dto.setFunctionCode(FunctionCodeEnum.CLOSE.getValue());
			writer.println(JSONObject.toJSONString(dto));

			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	public static void main(String[] args) throws UnknownHostException, InterruptedException {
		SocketClient client = new SocketClient(InetAddress.getByName("127.0.0.1"), 60000);
		ClientSendDto dto = new ClientSendDto();
		dto.setFunctionCode(FunctionCodeEnum.LOGIN.getValue());
		dto.setUserId("test1");
		dto.setMessage("登陆信息啦\n");
		//		Thread.sleep(6*1000);
		client.println(JSONObject.toJSONString(dto));
		while (true) {
		}
		//		client.close();
	}
}