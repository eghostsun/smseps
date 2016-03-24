package com.slf.sms.action.helper;


import java.util.List;
import com.slf.common.util.KeyedDigestMD5;
import com.slf.sms.bo.LsDxhc;
import com.slf.sms.bo.LsMtsms;
import com.slf.sms.bo.LsQydx;
import com.slf.sms.bo.Order;
import com.slf.sms.bo.XxQy;

public class ActionHelper {

	/**
	 * 扣除保证金
	 * @param order
	 * @param xxQy
	 * @return
	 */
	public static String makeSmsSendSign(Order order, XxQy xxQy)
	{
		StringBuffer sign = new StringBuffer(order.getUserId());
		sign.append(order.getOrderId());
		sign.append(order.getMsgType());
		if(order.getMsgType() == 1)
		{
			sign.append(order.getDateOrder());
		}
		sign.append(order.getNumber());
		sign.append(xxQy.getJym());
		return KeyedDigestMD5.getKeyedDigest(sign.toString(), "");
	}

	/**
	 * 查询号码状态
	 * @param list
	 * @return
	 */
	public static String queryPhoneStatus(List<LsMtsms> list)
	{
		
		StringBuffer msgs = new StringBuffer();
		msgs.append("[");
		if(list != null)
		{
			for(int i = 0; i < list.size(); i++)
			{
				LsMtsms lsMtsms = list.get(i);
				if(i > 0)
				{
					msgs.append(",");
				}
				msgs.append("{");
				msgs.append("\"phone\":");
				msgs.append("\"" + lsMtsms.getFshm() + "\"");
				msgs.append("\"status\":");
				msgs.append("\"" + lsMtsms.getZt() + "\"");
				msgs.append("}");
			}
		}
		msgs.append("]");
		return msgs.toString();
	}
}
