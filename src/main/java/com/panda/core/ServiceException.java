package com.panda.core;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Integer errorCode;

	/**
	 * 通过错误信息实例构造ServiceException
	 *
	 * @param errorInfoEntity 错误信息Enum实例
	 */
	public ServiceException(ErrorInfoEntity errorInfoEntity) {
		super(errorInfoEntity.getErrorMsg());
		this.errorCode = errorInfoEntity.getErrorCode();
	}

	/**
	 * 通过错误信息实例构造ServiceException,错误信息附带参数
	 *
	 * @param errorInfoEntity 错误信息Enum实例
	 * @param info            需要在错误信息中添加的信息
	 */
	public ServiceException(ErrorInfoEntity errorInfoEntity, Object... info) {
		super(String.format(errorInfoEntity.getErrorMsg(), info));
		this.errorCode = errorInfoEntity.getErrorCode();
	}

	public ServiceException(String message) {
		super(message);
		this.errorCode = ErrorCode.ESYS9999.getValue();
	}

	public ServiceException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * 可自定义Message
	 * Creates a new instance of ServiceException.
	 *
	 * @param message
	 * @param errorCode
	 */
	public ServiceException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode.getValue();
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
		this.errorCode = ErrorCode.ESYS9999.getValue();
	}


	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
}
