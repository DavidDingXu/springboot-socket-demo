package com.panda.core;

/**
 * 对请求详细的结果进行重新封装，规范接口返回值
 *
 * @param <T>
 *
 * @ClassName：ResponseEntity
 * @author：huangyongfa
 * @date：2017年08月08日
 */
public class ResponseEntity<T> {

	private Integer code;

	private String message;

	private T data;

	/**
	 * 静态构造方法 success时调用
	 *
	 * @param <T>
	 *
	 * @return
	 */
	public static <T> ResponseEntity<T> success() {
		return new ResponseEntity<T>().OK();
	}

	/**
	 * 静态构造方法 success时调用
	 *
	 * @param data
	 * @param <T>
	 *
	 * @return
	 */
	public static <T> ResponseEntity<T> success(T data) {
		return new ResponseEntity<T>().OK(data);
	}

	/**
	 * 静态构造方法 success时调用
	 *
	 * @param data
	 * @param message
	 * @param <T>
	 *
	 * @return
	 */
	public static <T> ResponseEntity<T> success(T data, String message) {
		return new ResponseEntity<T>().OK(data, message);
	}

	/**
	 * 通过错误信息实体异常构造方法
	 *
	 * @param errorInfoEntity 错误信息实体类
	 *
	 * @return 异常信息的返回实例
	 */
	public static <T> ResponseEntity<T> fail(ErrorInfoEntity errorInfoEntity) {
		return new ResponseEntity<T>().ERROR(new ServiceException(errorInfoEntity));
	}

	/**
	 * 异常构造方法
	 *
	 * @param serviceException
	 *
	 * @return
	 */
	public static <T> ResponseEntity<T> fail(ServiceException serviceException) {
		return new ResponseEntity<T>().ERROR(serviceException);
	}

	/**
	 * 异常构造方法
	 *
	 * @param serviceException
	 *
	 * @return
	 */
	public static <T> ResponseEntity<T> fail(T data, ServiceException serviceException) {
		return new ResponseEntity<T>().ERROR(data, serviceException);
	}

	public Integer getCode() {
		return code;
	}

	/**
	 * 私有化，请使用统一构造方法
	 *
	 * @param code
	 */
	@Deprecated
	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * 私有化，请使用统一构造方法
	 *
	 * @param message
	 */
	@Deprecated
	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	/**
	 * 私有化，请使用统一构造方法
	 *
	 * @param data
	 */
	@Deprecated
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * 正常业务返回值
	 *
	 * @return
	 */
	@Deprecated
	public ResponseEntity<T> OK() {
		return OK(null);
	}

	/**
	 * 正常业务返回值
	 *
	 * @param data
	 *
	 * @return
	 */
	@Deprecated
	public ResponseEntity<T> OK(T data) {
		this.setCode(ErrorCode.IPER0001.getValue());
		if (null != data) {
			this.setData(data);
		}
		return this;
	}

	/**
	 * 正常业务返回值,带message
	 *
	 * @param data
	 * @param message
	 *
	 * @return
	 */
	@Deprecated
	public ResponseEntity<T> OK(T data, String message) {
		this.setCode(ErrorCode.IPER0001.getValue());
		if (null != data) {
			this.setData(data);
		}
		if (null != message) {
			this.setMessage(message);
		}
		return this;
	}

	/**
	 * 异常构造方法
	 *
	 * @param serviceException
	 *
	 * @return
	 */
	@Deprecated
	public ResponseEntity<T> ERROR(ServiceException serviceException) {
		this.setMessage(serviceException.getMessage());
		this.setCode(serviceException.getErrorCode());
		return this;
	}

	/**
	 * 异常构造方法
	 *
	 * @param serviceException
	 *
	 * @return
	 */
	@Deprecated
	public ResponseEntity<T> ERROR(T data, ServiceException serviceException) {
		this.setData(data);
		this.setMessage(serviceException.getMessage());
		this.setCode(serviceException.getErrorCode());
		return this;
	}

}
