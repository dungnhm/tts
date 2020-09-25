package com.app.error.exception;

import com.app.pojo.MainObject;

public class UsersException extends SystemException {

    public UsersException(MainObject error) {
        super(error);
    }
}
