package com.app.dashchat.error;

public class SystemError {

    public static final SystemError MAX_CLIENT = new SystemError("MAX_CLIENT_LOGIN", "You can login on 5 device.", "", "http://developer.cmteam.mobi/errors/400.html");

    
    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public String getInformationLink() {
        return informationLink;
    }

    public SystemError(String name, String message, String details, String informationLink) {
        this.name = name;
        this.message = message;
        this.details = details;
        this.informationLink = informationLink;
    }

    public SystemError(int code, String message){
        this.code = code;
        this.message = message;
    }
    public SystemError(int code, String reason, String name, String message, String details, String informationLink) {
        this.code = code;
        this.reason = reason;
        this.name = name;
        this.message = message;
        this.details = details;
        this.informationLink = informationLink;
    }

    private int code;
    private String reason;
    private String name;
    private String message;
    private String details;
    private String informationLink;
}
