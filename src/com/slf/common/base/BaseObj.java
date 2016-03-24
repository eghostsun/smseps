package com.slf.common.base;

import org.apache.struts.action.ActionForm;

import com.slf.sms.common.CommonContants;


public class BaseObj extends ActionForm{
	private String tableUser = CommonContants.ORACLE_TABLEUSER;

	public String getTableUser() {
		return tableUser;
	}

	public void setTableUser(String tableUser) {
		this.tableUser = tableUser;
	}
}
