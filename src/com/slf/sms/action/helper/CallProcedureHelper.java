package com.slf.sms.action.helper;

import java.util.Map;
import com.slf.common.util.CryptUtils;
import com.slf.common.util.DateUtils;
import com.slf.common.util.Utils;
import com.slf.sms.bo.CallPMap;
import com.slf.sms.bo.Order;
import com.slf.sms.bo.XxQy;

/**
 * 存储过程MAP生成类
 * @author 孙立凡
 *
 */
public class CallProcedureHelper {

	/*
	 * 插入一条新生支付存款请求
	 */
	public static CallPMap makebzj(Order order,Map<String, String> msg,XxQy xxQy)
	{
		CallPMap map = new CallPMap();
		map.put("DLID", xxQy.getDlid());
		map.put("DXNR", msg.get("content"));
		map.put("HMSL", msg.get("phone").split("\\,").length);
		int smslen = 0;
		if("1".equals(msg.get("isParam"))) 
		{//模板短信
			smslen = Utils.dealTempSmsLen(msg.get("content"));
		}else{
			smslen = msg.get("content").length();
		}
		if(smslen > 70 && xxQy.getNrzs() > 67) //采用网关长短信算法
		{
			smslen += (xxQy.getNrzs() - 67) * (smslen%67 == 0 ?  smslen/67 : smslen/67 + 1);
		}
		map.put("DXZS", smslen);
		map.put("JRBZ", "h");
		map.put("DLFLSH", order.getOrderId());
		map.put("MBBZ", msg.get("isParam"));
		map.put("DSFSSJ", order.getDateOrder() == null || "".equals(order.getDateOrder())? DateUtils.strDate("yyyyMMddHHmmss") : order.getDateOrder());
		return map;
	}
	
	/*
	 * 插入一条新生支付存款请求
	 */
	public static CallPMap makebzj2(Order order,Map<String, String> msg,XxQy xxQy)
	{
		String smsMsg = CryptUtils.decrypt(msg.get("content"), xxQy.getJym());
		CallPMap map = new CallPMap();
		map.put("DLID", xxQy.getDlid());
		map.put("DXNR", smsMsg);
		map.put("HMSL", msg.get("phone").split("\\,").length);
		int smslen = 0;
		if("1".equals(msg.get("isParam"))) 
		{//模板短信
			smslen = Utils.dealTempSmsLen(smsMsg);
		}else{
			smslen = smsMsg.length();
		}
		if(smslen > 70 && xxQy.getNrzs() > 67) //采用网关长短信算法
		{
			smslen += (xxQy.getNrzs() - 67) * (smslen%67 == 0 ?  smslen/67 : smslen/67 + 1);
		}
		map.put("DXZS", smslen);
		map.put("JRBZ", "h");
		map.put("DLFLSH", order.getOrderId());
		map.put("MBBZ", msg.get("isParam"));
		map.put("DSFSSJ", order.getDateOrder() == null || "".equals(order.getDateOrder())? DateUtils.strDate("yyyyMMddHHmmss") : order.getDateOrder());
		return map;
	}
}
