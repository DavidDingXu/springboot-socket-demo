package com.panda.controller.socket;

import com.alibaba.fastjson.JSONObject;
import com.panda.core.ResponseEntity;
import com.panda.core.ServiceException;
import com.panda.model.ClientParamVo;
import com.panda.service.SocketClientService;
import com.panda.service.impl.SocketClientServiceImpl;
import com.panda.utils.socket.client.SocketClient;
import com.panda.utils.socket.dto.ClientSendDto;
import com.panda.utils.socket.enums.FunctionCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author 丁许
 * @date 2019-01-25 9:46
 */
@RestController
@RequestMapping("/socket-client")
@Slf4j
public class SocketClientController {

	@Resource(name = "clientTaskPool")
	private ThreadPoolTaskExecutor clientExecutor;

	@Resource
	private SocketClientService socketClientService;

	/**
	 * @param paramVo 用户id
	 *
	 * @return 是否操作成功
	 */
	@PostMapping("/start")
	public ResponseEntity<?> startClient(@RequestBody ClientParamVo paramVo) {
		String userId = paramVo.getUserId();
		socketClientService.startOneClient(userId);
		return ResponseEntity.success();
	}

	/**
	 * 关闭客户端
	 *
	 * @param paramVo userId
	 *
	 * @return 是否操作成功
	 */
	@PostMapping("/close")
	public ResponseEntity<?> closeClient(@RequestBody ClientParamVo paramVo) {
		String userId = paramVo.getUserId();
		socketClientService.closeOneClient(userId);
		return ResponseEntity.success();
	}

	@GetMapping("/get-users")
	public ResponseEntity<Set<String>> getUsers() {
		return ResponseEntity.success(SocketClientServiceImpl.existSocketClientMap.keySet());
	}

	@PostMapping("/send-message")
	public ResponseEntity<?> sendMessage(@RequestBody ClientParamVo paramVo) {
		if (StringUtils.isEmpty(paramVo.getUserId()) || StringUtils.isEmpty(paramVo.getMessage())) {
			throw new ServiceException("参数不全");
		}
		if (!SocketClientServiceImpl.existSocketClientMap.containsKey(paramVo.getUserId())) {
			throw new ServiceException("并没有客户端连接");
		}
		SocketClient client = SocketClientServiceImpl.existSocketClientMap.get(paramVo.getUserId()).getSocketClient();
		ClientSendDto dto = new ClientSendDto();
		dto.setFunctionCode(FunctionCodeEnum.MESSAGE.getValue());
		dto.setUserId(paramVo.getUserId());
		dto.setMessage(paramVo.getMessage());
		client.println(JSONObject.toJSONString(dto));
		return ResponseEntity.success();

	}
}
