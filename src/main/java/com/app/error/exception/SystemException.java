package com.app.error.exception;

import com.app.pojo.MainObject;

public class SystemException extends RuntimeException {

    private MainObject systemError;

    public MainObject getSystemError() {
        return systemError;
    }

    public SystemException(MainObject error) {
        super(error.getMessage());
        this.systemError = error;
    }
}
