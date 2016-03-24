package com.slf.sms.common;


import com.slf.common.util.ReadProperties;



public interface CommonContants {

	static final String PROPERTIES_FILE = "engine.properties";
	public static final String LOCK_IP = ReadProperties.getProp(PROPERTIES_FILE).getProperty("lock.ip");
	public static final String ORACLE_TABLEUSER = ReadProperties.getProp(PROPERTIES_FILE).getProperty("ORACLE.TABLEUSER");
	public static final int MAX_NUM = Integer.parseInt(ReadProperties.getProp(PROPERTIES_FILE).getProperty("max.num"));
	public static final String ZIP_DIR = ReadProperties.getProp(PROPERTIES_FILE).getProperty("zip.dir");
	public static final String FTP_IP = ReadProperties.getProp(PROPERTIES_FILE).getProperty("ftp.ip");
	public static final int FTP_PORT = Integer.parseInt(ReadProperties.getProp(PROPERTIES_FILE).getProperty("ftp.port"));
	public static final String FTP_NAME = ReadProperties.getProp(PROPERTIES_FILE).getProperty("ftp.name");
	public static final String FTP_PWD = ReadProperties.getProp(PROPERTIES_FILE).getProperty("ftp.pwd");
	public static final String DXHC_NAME = ReadProperties.getProp(PROPERTIES_FILE).getProperty("dxhc.name");
	
	public static final String HTTPSQS_AUTH = ReadProperties.getProp(PROPERTIES_FILE).getProperty("httpsqs.auth");
	public static final String HTTPSQS_URL = ReadProperties.getProp(PROPERTIES_FILE).getProperty("httpsqs.url");
	public static final int STATUS_NUM = Integer.parseInt(ReadProperties.getProp(PROPERTIES_FILE).getProperty("status.num"));
	public static final long ORDER_TIMEOUT = Long.parseLong(ReadProperties.getProp(PROPERTIES_FILE).getProperty("order.timeout"));
}
