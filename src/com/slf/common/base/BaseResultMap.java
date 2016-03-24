package com.slf.common.base;

import java.util.HashMap;

public class BaseResultMap extends HashMap{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int retCode;
	private String retMsg;
	
	public BaseResultMap()
	{
		
	}
	
	public BaseResultMap(int retCode,String retMsg)
	{
		this.retCode = retCode;
		this.retMsg = retMsg;
		this.put("status", retCode);
		this.put("result", retMsg);
	}

	public String getRetCode() {
		
		return String.valueOf(this.get("status") == null ? "" : this.get("status").toString());
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
		this.put("status", retCode);
	}

	public String getRetMsg() {
		return String.valueOf(this.get("result") == null ? "" : this.get("result").toString());
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
		this.put("result", retMsg);
	}
	
}