package com.panda.config;

import com.panda.core.ResponseEntity;
import com.panda.core.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName：SystemExceptionHandler
 * @Description：TODO
 * @author：huangyongfa
 * @date：2017年08月08日
 */
@ControllerAdvice
public class SystemExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 解决Form请求无法转换日期问题
	 *
	 * @param binder  绑定器
	 * @param request 请求体
	 */
	@InitBinder
	private void initBinder(WebDataBinder binder, WebRequest request) {
		// 转换日期
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// CustomDateEditor为自定义日期编辑器
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * 未捕获的业务异常处理
	 *
	 * @param exception
	 *
	 * @return
	 *
	 * @throws
	 * @see
	 */
	@ExceptionHandler(ServiceException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody
	ResponseEntity<String> handleServcerException(ServiceException exception) {
		logger.error("system service exception handler,exception code:{},msg:{}", exception.getErrorCode(),
				exception.getMessage());
		return new ResponseEntity<String>().ERROR(exception);
	}


	@ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<String> noHandlerFoundException(org.springframework.web.servlet.NoHandlerFoundException e) {
		logger.error(e.getMessage(), e);
		return new ResponseEntity<String>().ERROR(new ServiceException("没有找到该页面"));
	}


	/**
	 * 处理@requestParam不存在的异常
	 *
	 * @param exception 异常
	 *
	 * @return 返回responseEntity
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<String> handleMissingServletRequestParameterException(
			MissingServletRequestParameterException exception) {
		logger.error("system runtime exception handler:", exception);
		return new ResponseEntity<String>().ERROR(new ServiceException(exception.getMessage(), exception));
	}

	/**
	 * 处理请求类型不一致的异常
	 *
	 * @param exception 异常
	 *
	 * @return 返回responseEntity
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(
			HttpRequestMethodNotSupportedException exception) {
		logger.error("system runtime exception handler:", exception);
		return new ResponseEntity<String>().ERROR(new ServiceException(exception.getMessage(), exception));
	}
}
