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
import com.app.dashchat.util.DBProcedurePool;
import com.app.dashchat.util.DBProcedureUtil;
import com.app.dashchat.util.ParamUtil;

import io.netty.handler.codec.http.HttpResponseStatus;
import oracle.jdbc.OracleTypes;

public class MessageService {
	private static DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public static final String GET_CHAT_HISTORY = "{call PKG_MESSAGE.get_chat_common(?,?,?,?,?)}";
	public static final String INSERT_MESSAGE = "{call PKG_MESSAGE.insert_message(?,?,?,?,?,?,?,?,?,?)}";

	public static List<Map> getChatHistory(String sender, String receiver) throws SQLException {

		Map inputParams = new LinkedHashMap<Integer, String>();
		inputParams.put(1, sender);
		inputParams.put(2, receiver);

		Map<Integer, Integer> outputParamsTypes = new LinkedHashMap<>();
		outputParamsTypes.put(3, OracleTypes.NUMBER);
		outputParamsTypes.put(4, OracleTypes.VARCHAR);
		outputParamsTypes.put(5, OracleTypes.CURSOR);

		Map<Integer, String> outputParamsNames = new LinkedHashMap<>();
		outputParamsNames.put(3, AppParams.RESULT_CODE);
		outputParamsNames.put(4, AppParams.RESULT_MSG);
		outputParamsNames.put(5, AppParams.RESULT_DATA);

		Map searchResultMap = DBProcedureUtil.execute(dataSource, GET_CHAT_HISTORY, inputParams,
				outputParamsTypes, outputParamsNames);

		int resultCode = ParamUtil.getInt(searchResultMap, AppParams.RESULT_CODE);

		if (resultCode != HttpResponseStatus.OK.code()) {
			throw new OracleException(ParamUtil.getString(searchResultMap, AppParams.RESULT_MSG));
		}

		Map resultMap = new HashMap<>();
		List<Map> resultDataList = ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA);

		LOGGER.info("=> All chat result: " + ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA));

		return resultDataList;
	}
	
	public static Map searchChat(String email) throws SQLException {

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

		Map searchResultMap = DBProcedureUtil.execute(dataSource, DBProcedurePool.GET_USER_BY_EMAIL, inputParams,
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

		LOGGER.info("=> search message result: " + ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA));

		return resultMap;
	}
	
	public static Map insertMessage(String sender, String receiver, String type,
			String content, String media, String status, String state) throws SQLException {

		Map inputParams = new LinkedHashMap<Integer, String>();
		inputParams.put(1, sender);
		inputParams.put(2, receiver);
		inputParams.put(3, type);
		inputParams.put(4, content);
		inputParams.put(5, media);
		inputParams.put(6, status);
		inputParams.put(7, state);

		Map<Integer, Integer> outputParamsTypes = new LinkedHashMap<>();
		outputParamsTypes.put(8, OracleTypes.NUMBER);
		outputParamsTypes.put(9, OracleTypes.VARCHAR);
		outputParamsTypes.put(10, OracleTypes.CURSOR);

		Map<Integer, String> outputParamsNames = new LinkedHashMap<>();
		outputParamsNames.put(8, AppParams.RESULT_CODE);
		outputParamsNames.put(9, AppParams.RESULT_MSG);
		outputParamsNames.put(10, AppParams.RESULT_DATA);

		Map searchResultMap = DBProcedureUtil.execute(dataSource, INSERT_MESSAGE, inputParams,
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

		LOGGER.info("=> insert message result: " + ParamUtil.getListData(searchResultMap, AppParams.RESULT_DATA));

		return resultMap;
	}

	private static Map format(Map queryData) throws SQLException {

		Map resultMap = new LinkedHashMap<>();

		resultMap.put(AppParams.ID, ParamUtil.getString(queryData, AppParams.S_ID));
		resultMap.put(AppParams.SENDER, ParamUtil.getString(queryData, AppParams.S_SENDER));
		resultMap.put(AppParams.RECEIVER, ParamUtil.getString(queryData, AppParams.S_RECEIVER));
		resultMap.put(AppParams.TYPE, ParamUtil.getString(queryData, AppParams.S_TYPE));
		resultMap.put(AppParams.CONTENT, ParamUtil.getString(queryData, AppParams.S_CONTENT));
		resultMap.put(AppParams.MEDIA, ParamUtil.getString(queryData, AppParams.S_MEDIA));
		resultMap.put(AppParams.STATUS, ParamUtil.getString(queryData, AppParams.S_STATUS));
		resultMap.put(AppParams.CREATE_AT, ParamUtil.getString(queryData, AppParams.D_CREATED_AT));
		resultMap.put(AppParams.UPDATE_AT, ParamUtil.getString(queryData, AppParams.D_UPDATED_AT));
		resultMap.put(AppParams.S_STATE, ParamUtil.getString(queryData, AppParams.S_STATE));
		return resultMap;
	}

	private static final Logger LOGGER = Logger.getLogger(MessageService.class.getName());
}
