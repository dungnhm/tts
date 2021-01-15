package com.app.dashchat.util;

public class DBProcedurePool {
	public static final String INSERT_USER = "{call PKG_USER.insert_user(?,?,?,?,?,?,?,?,?,?)}";
	public static final String GET_USER_BY_EMAIL = "{call PKG_USER.get_user_by_email(?,?,?,?)}";
	public static final String GET_ALL_USER = "{call PKG_USER.get_all_user(?,?,?,?)}";
	
}
