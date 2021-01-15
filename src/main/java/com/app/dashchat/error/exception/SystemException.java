package com.app.dashchat.error.exception;

import com.app.dashchat.pojo.MainObject;

public class SystemException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainObject systemError;

	public MainObject getSystemError() {
		return systemError;
	}

	public SystemException(MainObject error) {
		super(error.getMessage());
		this.systemError = error;
	}
}
