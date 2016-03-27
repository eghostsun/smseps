package com.slf.sms.services.helper;

import java.util.List;
import java.util.Map;

import com.slf.sms.bo.XxDxgjz;

public class ServiceHelper {

	public static String compareGjz(List<XxDxgjz> list, String content)
	{
		if(list == null)
		{
			return null;
		}
		for(int i = 0; i < list.size(); i++)
		{
			String key = list.get(i).getGjz();
			if(content.contains(key))
			{
				return key;
			}
		}
		return null;
	}
}
