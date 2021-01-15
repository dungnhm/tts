package com.app.dashchat.services;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.app.dashchat.error.exception.OracleException;
import com.app.dashchat.util.AppParams;
import com.app.dashchat.util.DBProcedureUtil;
import com.app.dashchat.util.ParamUtil;

import io.netty.handler.codec.http.HttpResponseStatus;
import oracle.jdbc.OracleTypes;

public class UserService {
	private static DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public static final String INSERT_USER = "{call PKG_USER.insert_user(?,?,?,?,?,?,?,?,?,?)}";
	public static final String GET_USER_BY_EMAIL = "{call PKG_USER.get_user_by_email(?,?,?,?)}";
	public static final String GET_USER_BY_ID = "{call PKG_USER.get_user_by_id(?,?,?,?)}";
	public static final String GET_ALL_USER = "{call PKG_USER.get_all_user(?,?,?,?)}";
	public static final String GET_CONTACT_BY_EMAIL = "{call PKG_USER.get_contact_by_email(?,?,?,?)}";
	public static final String UPDATE_USER = "{call PKG_USER.update_user(?,?,?,?,?,?,?,?,?,?)}";

	public static List<Map> getAllUsers(String state) throws SQLException {

		Map inputParams = new LinkedHashMap<Integer, String>();
		state = "%"+state+"%";
		inputParams.put(1, state);

		Map<Integer, Integer> outputParamsTypes = new LinkedHashMap<>();
		outputParamsTypes.put(2, OracleTypes.NUMBER);
		outputParamsTypes.put(3, OracleTypes.VARCHAR);
		outputParamsTypes.put(4, OracleTypes.CURSOR);

		Map<Integer, String> outputParamsNames = new LinkedHashMap<>();
		outputParamsNames.put(2, AppParams.RESULT_CODE);
		outputParamsNames.put(3, AppParams.RESULT_MSG);
		outputParamsNames.put(4, AppParams.RESULT_DATA);

		Map searchResultMap = DBProcedureUtil.execute(dataSource, GET_ALL_USER, inputParams,
				outputParamsTypes, outputParamsNames);

		int resultCode = ParamUtil.getInt(searchResultMap, AppParams.RESULT_CODE);

		if (resultCode != HttpResponseStatus.OK.code()) {
			throw new OracleException(ParamUtil.getString(searchResultMap, AppParams.RESULT_MSG));
		}

		Map resultMap = new HashMap<>();
		List<Map> resultDataList = ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA);

//		if (!resultDataList.isEmpty()) {
//			resultMap = format(resultDataList.get(0));
//		}

		LOGGER.info("=> All Users result: " + ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA));

		return resultDataList;
	}
	
	public static Map getUserByEmail(String email) throws SQLException {

		Map inputParams = new LinkedHashMap<Integer, String>();
		inputParams.put(1, email);

		Map<Integer, Integer> outputParamsTypes = new LinkedHashMap<>();
		outputParamsTypes.put(2, OracleTypes.NUMBER);
		outputParamsTypes.put(3, OracleTypes.VARCHAR);
		outputParamsTypes.put(4, OracleTypes.CURSOR);

		Map<Integer, String> outputParamsNames = new LinkedHashMap<>();
		outputParamsNames.put(2, AppParams.RESULT_CODE);
		outputParamsNames.put(3, AppParams.RESULT_MSG);
		outputParamsNames.put(4, AppParams.RESULT_DATA);

		Map searchResultMap = DBProcedureUtil.execute(dataSource, GET_USER_BY_EMAIL, inputParams,
				outputParamsTypes, outputParamsNames);

		int resultCode = ParamUtil.getInt(searchResultMap, AppParams.RESULT_CODE);

		if (resultCode != HttpResponseStatus.OK.code()) {
			throw new OracleException(ParamUtil.getString(searchResultMap, AppParams.RESULT_MSG));
		}

		Map resultMap = new HashMap<>();
		List<Map> resultDataList = ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA);

		if (!resultDataList.isEmpty()) {
			resultMap = format(resultDataList.get(0));
		}

		LOGGER.info("=> Get user result2: " + resultMap);
		return resultMap;
	}
	
	public static Map getUserById(String id) throws SQLException {

		Map inputParams = new LinkedHashMap<Integer, String>();
		inputParams.put(1, id);

		Map<Integer, Integer> outputParamsTypes = new LinkedHashMap<>();
		outputParamsTypes.put(2, OracleTypes.NUMBER);
		outputParamsTypes.put(3, OracleTypes.VARCHAR);
		outputParamsTypes.put(4, OracleTypes.CURSOR);

		Map<Integer, String> outputParamsNames = new LinkedHashMap<>();
		outputParamsNames.put(2, AppParams.RESULT_CODE);
		outputParamsNames.put(3, AppParams.RESULT_MSG);
		outputParamsNames.put(4, AppParams.RESULT_DATA);

		Map searchResultMap = DBProcedureUtil.execute(dataSource, GET_USER_BY_ID, inputParams,
				outputParamsTypes, outputParamsNames);

		int resultCode = ParamUtil.getInt(searchResultMap, AppParams.RESULT_CODE);

		if (resultCode != HttpResponseStatus.OK.code()) {
			throw new OracleException(ParamUtil.getString(searchResultMap, AppParams.RESULT_MSG));
		}

		Map resultMap = new HashMap<>();
		List<Map> resultDataList = ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA);

		if (!resultDataList.isEmpty()) {
			resultMap = format(resultDataList.get(0));
		}

		LOGGER.info("=> Get user result2: " + resultMap);
		return resultMap;
	}
	
	public static Map updateUser(String email, String password,String firstName,String lastName,String address,String phone,String contact) throws SQLException {

		Map inputParams = new LinkedHashMap<Integer, String>();
		inputParams.put(1, email);
		inputParams.put(2, password);
		inputParams.put(3, firstName);
		inputParams.put(4, lastName);
		inputParams.put(5, address);
		inputParams.put(6, phone);
		inputParams.put(7, contact);

		Map<Integer, Integer> outputParamsTypes = new LinkedHashMap<>();
		outputParamsTypes.put(8, OracleTypes.NUMBER);
		outputParamsTypes.put(9, OracleTypes.VARCHAR);
		outputParamsTypes.put(10, OracleTypes.CURSOR);

		Map<Integer, String> outputParamsNames = new LinkedHashMap<>();
		outputParamsNames.put(8, AppParams.RESULT_CODE);
		outputParamsNames.put(9, AppParams.RESULT_MSG);
		outputParamsNames.put(10, AppParams.RESULT_DATA);

		Map searchResultMap = DBProcedureUtil.execute(dataSource, UPDATE_USER, inputParams,
				outputParamsTypes, outputParamsNames);

		int resultCode = ParamUtil.getInt(searchResultMap, AppParams.RESULT_CODE);

		if (resultCode != HttpResponseStatus.OK.code()) {
			throw new OracleException(ParamUtil.getString(searchResultMap, AppParams.RESULT_MSG));
		}

		Map resultMap = new HashMap<>();
		List<Map> resultDataList = ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA);

		if (!resultDataList.isEmpty()) {
			resultMap = format(resultDataList.get(0));
		}

		LOGGER.info("=> Get user by email result: " + ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA));

		return resultMap;
	}
	
	public static Map insertUser(String username, String password, String email,
			String firstName, String lastName, String address, String phone) throws SQLException {

		Map inputParams = new LinkedHashMap<Integer, String>();
		inputParams.put(1, username);
		inputParams.put(2, password);
		inputParams.put(3, email);
		inputParams.put(4, firstName);
		inputParams.put(5, lastName);
		inputParams.put(6, address);
		inputParams.put(7, phone);

		Map<Integer, Integer> outputParamsTypes = new LinkedHashMap<>();
		outputParamsTypes.put(8, OracleTypes.NUMBER);
		outputParamsTypes.put(9, OracleTypes.VARCHAR);
		outputParamsTypes.put(10, OracleTypes.CURSOR);

		Map<Integer, String> outputParamsNames = new LinkedHashMap<>();
		outputParamsNames.put(8, AppParams.RESULT_CODE);
		outputParamsNames.put(9, AppParams.RESULT_MSG);
		outputParamsNames.put(10, AppParams.RESULT_DATA);

		Map searchResultMap = DBProcedureUtil.execute(dataSource, INSERT_USER, inputParams,
				outputParamsTypes, outputParamsNames);

		int resultCode = ParamUtil.getInt(searchResultMap, AppParams.RESULT_CODE);

		if (resultCode != HttpResponseStatus.CREATED.code()) {
			throw new OracleException(ParamUtil.getString(searchResultMap, AppParams.RESULT_MSG));
		}

		Map resultMap = new HashMap<>();
		List<Map> resultDataList = ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA);

		if (!resultDataList.isEmpty()) {
			resultMap = format(resultDataList.get(0));
		}

		LOGGER.info("=> insert user result: " + ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA));

		return resultMap;
	}
	
	public static Map getContactUsers(String email) throws SQLException {

		Map inputParams = new LinkedHashMap<Integer, String>();
		inputParams.put(1, email);

		Map<Integer, Integer> outputParamsTypes = new LinkedHashMap<>();
		outputParamsTypes.put(2, OracleTypes.NUMBER);
		outputParamsTypes.put(3, OracleTypes.VARCHAR);
		outputParamsTypes.put(4, OracleTypes.CURSOR);

		Map<Integer, String> outputParamsNames = new LinkedHashMap<>();
		outputParamsNames.put(2, AppParams.RESULT_CODE);
		outputParamsNames.put(3, AppParams.RESULT_MSG);
		outputParamsNames.put(4, AppParams.RESULT_DATA);

		Map searchResultMap = DBProcedureUtil.execute(dataSource, GET_CONTACT_BY_EMAIL, inputParams,
				outputParamsTypes, outputParamsNames);

		int resultCode = ParamUtil.getInt(searchResultMap, AppParams.RESULT_CODE);

		if (resultCode != HttpResponseStatus.OK.code()) {
			throw new OracleException(ParamUtil.getString(searchResultMap, AppParams.RESULT_MSG));
		}

		
		List<Map> resultDataList = ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA);
		LOGGER.info("=> contact resultDataList result: " + ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA));
		Map resultMap = new HashMap<>();
		resultMap = resultDataList.get(0);
		LOGGER.info("=> contact list result: " + resultMap);
		return resultMap;
	}

	private static Map format(Map queryData) throws SQLException {

		Map resultMap = new LinkedHashMap<>();
		resultMap.put(AppParams.ID, ParamUtil.getString(queryData, AppParams.S_ID));
		resultMap.put(AppParams.USERNAME, ParamUtil.getString(queryData, AppParams.S_USERNAME));
		resultMap.put(AppParams.PASSWORD, ParamUtil.getString(queryData, AppParams.S_PASSWORD));
		resultMap.put(AppParams.EMAIL, ParamUtil.getString(queryData, AppParams.S_EMAIL));
		resultMap.put(AppParams.FIRST_NAME, ParamUtil.getString(queryData, AppParams.S_FIRST_NAME));
		resultMap.put(AppParams.LAST_NAME, ParamUtil.getString(queryData, AppParams.S_LAST_NAME));
		resultMap.put(AppParams.ADDRESS, ParamUtil.getString(queryData, AppParams.S_ADDRESS));
		resultMap.put(AppParams.STATE, ParamUtil.getString(queryData, AppParams.S_STATE));
		resultMap.put(AppParams.PHONE, ParamUtil.getString(queryData, AppParams.S_PHONE));
		resultMap.put(AppParams.CREATE_AT, ParamUtil.getString(queryData, AppParams.D_CREATED_AT));
		resultMap.put(AppParams.UPDATE_AT, ParamUtil.getString(queryData, AppParams.D_UPDATED_AT));
		resultMap.put(AppParams.LAST_LOGGED_IN, ParamUtil.getString(queryData, AppParams.D_LAST_LOGGED_IN));
		resultMap.put(AppParams.CONTACT, ParamUtil.getString(queryData, AppParams.S_CONTACT));
		return resultMap;
	}

	private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
}
