package com.slf.sms.bo;

import com.slf.common.base.BaseObj;

public class Order extends BaseObj {

	private String userId = "";
	private String orderId = "";
	private int msgType = 0;
	private String dateOrder = "";
	private  String msgs;
	private String sign;
	private String phone;
	private String date;
	private String number = "";
	
	/**
	 * 彩信使用
	 */
	private String subject;
	private String msg;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**
	 * 彩信使用
	 */
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number == null ? "" : number.trim();
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	private int status;
	private String result;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public String getDateOrder() {
		return dateOrder;
	}
	public void setDateOrder(String dateOrder) {
		this.dateOrder = dateOrder;
	}
	public String getMsgs() {
		return msgs;
	}
	public void setMsgs(String msgs) {
		this.msgs = msgs;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}
