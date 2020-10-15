package com.app.error.exception;

import com.app.pojo.MainObject;

public class UsersException extends SystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UsersException(MainObject error) {
		super(error);
	}
}
