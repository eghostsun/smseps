package com.slf.sms.common;

public enum ErrorCode {

	SUBMIT_SUCCESS("提交成功",1), 
	SUBMIT_ERROR("提交失败",-1),
	NO_MONEY("资金余额不足",-3),
	SIGN_ERROR("校验码失败",-4),
	SYS_ERROR("系统错误",-5),
	SYS_DEFEND("系统维护中",-8),
	DEAL_SUCCESS("处理结束", 0),
	DEAL_RUNNING("处理中", 3),
	UNFOUND("未查询到记录", -2),
	REPEAT_REQ("订单号重复",-6),
	QUERY_OFTEN("查询过频繁",-7),
	KEY_ERROR("关键字错误",-9),
	ZIPFILE_TOBIG("文件数据大于100k",-10),
	ZIPFILE_NULL("文件不能为空", -11),
	PHONE_NUM("号码数量错误",-12),
	MSGS_ERROR("msgs结构错误",-13);
	
	private  String msg;  
	private   int  code; 
	private  ErrorCode(String msg,  int  code) {  
	      this .msg = msg;  
	      this .code = code;  
	}  
	
	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

}
