package com.slf.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
public class ResultUtils {
	private static final String DEFAULT_CHARASET = "utf-8";
	public static String makeResult(Map<String, Object> retMap, String type)
	{
		type = type == null || "".equals(type) ? "xml" : type.toLowerCase();
		if("json".equals(type))
		{
			return makeJSONResult(retMap);
		}else{
			return makeXMLResult(retMap);
		}
	}
	/**
	 * Map对象生成XML字符串进行通信
	 * @param map
	 * @return
	 */
	private static String makeXMLResult(Map<String, Object> retMap)
	{
		StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		xml.append("<RESULT>");
		Iterator<String> keys = retMap.keySet().iterator();
		while(keys.hasNext())
		{
			String key = keys.next();
			Object obj = retMap.get(key);
			if(obj != null)
			{
				if(obj instanceof List)
				{
					List<Map<String, Object>> list = (List<Map<String,Object>>)obj;
					if(list.size() > 0)
					{
						xml.append("<" + key.toUpperCase() + ">");
							Map<String, Object> child = null;
							for(int i = 0; i < list.size(); i++)
							{
								child = list.get(i);
								if(child.size() > 0)
								{
									Iterator<String> childKeys = child.keySet().iterator();
									xml.append("<OBJECT i=\"" + i + "\">");
									while(childKeys.hasNext())
									{
										String childKey = childKeys.next();
										if(child.get(childKey) != null)
										{
											xml.append("<" + childKey.toUpperCase() + ">");
											try {
												xml.append(URLEncoder.encode(String.valueOf(child.get(childKey) == null ? "" : child.get(childKey)),DEFAULT_CHARASET));
											} catch (UnsupportedEncodingException e) {
												// TODO Auto-generated catch block
											}
											xml.append("</" + childKey.toUpperCase() + ">");
										}else{
											xml.append("<" + childKey.toUpperCase() + " />");
										}
									}
									xml.append("</OBJECT>");
								}else{
									xml.append("<OBJECT />");
								}
							}
						xml.append("</" + key.toUpperCase() + ">");
					}else{
						xml.append("<" + key.toUpperCase() + " />");
					}
				}else{
					xml.append("<" + key.toUpperCase() + ">");
					try {
						xml.append(URLEncoder.encode(String.valueOf(obj == null ? "" : obj),DEFAULT_CHARASET));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
					}
					xml.append("</" + key.toUpperCase() + ">");
				}
			}else{
				xml.append("<" + key.toUpperCase() + " />");
			}
		}
		xml.append("</RESULT>");
		return xml.toString();
	}
	
	/**
	 * Map对象生成JSON字符串进行通信
	 * @param map
	 * @return
	 */
	private static String makeJSONResult(Map<String, Object> map)
	{
		Iterator<String> keys = map.keySet().iterator();
		StringBuffer json = new StringBuffer("{");
		int i = 0;
		while(keys.hasNext())
		{
			if(i != 0)
			{
				json.append(",");
			}
			String key = keys.next();
			Object val = map.get(key);
			if(val instanceof List)
			{
				json.append("\"" + key.toUpperCase() + "\":[");
				
				List<Map<String, Object>> list = (List<Map<String,Object>>)val;
				int j = 0;
				
				for(Map<String, Object> obj : list)
				{
					if(j != 0)
					{
						json.append(",");
					}
					json.append("{");
					Iterator<String> Childkeys = obj.keySet().iterator();
					int b = 0;
					while(Childkeys.hasNext())
					{
						if(b != 0)
						{
							json.append(",");
						}
						String childKey = Childkeys.next();
						Object childVal = obj.get(childKey);
						try {
							json.append("\"" + childKey.toUpperCase() +"\":" + "\"" + URLEncoder.encode(String.valueOf(childVal == null ? "" : childVal), DEFAULT_CHARASET) + "\"");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
						}
						b++;
					}
					json.append("}");
					j++;
				}
				json.append("]");
			}else{
				try {
					json.append("\"" + key.toUpperCase() + "\":" + "\"" + URLEncoder.encode(String.valueOf(val == null ? "" : val), DEFAULT_CHARASET) + "\"");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
				}
			}
			i++;
		}
		json.append("}");
		return json.toString();
	}
	
	public static void main(String args[])
	{
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("RETCODE", 1);
		retMap.put("RETCODE", 1);
		
		Map<String, Object> childMap = new HashMap<String, Object>();
		childMap.put("A", 2);
		childMap.put("B", 2);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list.add(childMap);
		list.add(childMap);
		retMap.put("list", list);
		System.out.println(ResultUtils.makeJSONResult(retMap));
	}
}
