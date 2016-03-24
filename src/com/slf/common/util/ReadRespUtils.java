package com.slf.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;




public class ReadRespUtils {
	private static final String DEFAULT_CHARASET = "utf-8";
	/**
	 * 读取xml结果
	 * {"RETCODE":"1","LIST":[{"A":"2","B":"2"},{"A":"2","B":"2"}]}
	 * @param xml
	 * @return
	 * @throws DocumentException 
	 */
	
	/**
	 * 读取json结果
	 * <?xml version="1.0" encoding="utf-8"?>
	 * <RESULT>
	 * <RETCODE>1</RETCODE>
	 * <LIST><OBJECT i="0"><A>2</A><B>2</B></OBJECT>
	 * <OBJECT i="1"><A>2</A><B>2</B></OBJECT></LIST>
	 * </RESULT>
	 * @param json
	 * @return
	 */
	public List<Object> readJsonResult(String json)
	{
		List<Object> resultList = new ArrayList<Object>();
		JSONArray parent = JSONArray.fromObject(json);
		for(int i = 0; i < parent.size(); i++)
		{
			JSONObject child = parent.getJSONObject(i);
			Iterator<String> keys = child.keys();
			Map<String, String> map = new HashMap<String, String>();
			while(keys.hasNext())
			{
				String key = keys.next();
				try {
					map.put(key, URLDecoder.decode(child.getString(key) == null ? "" : child.getString(key),DEFAULT_CHARASET));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
				}
			}
			resultList.add(map);
		}
		return resultList;
	}
	
	public Map<String,String> readJsonObject(String json)
	{
		JSONObject parent = JSONObject.fromObject(json);
		Iterator<String> keys = parent.keys();
		Map<String, String> map = new HashMap<String, String>();
		while(keys.hasNext())
		{
			String key = keys.next();
			try {
				map.put(key, URLDecoder.decode(parent.getString(key) == null ? "" : parent.getString(key),DEFAULT_CHARASET));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
			}
		}
		return map;
	}
	
	public static void main(String args[])
	{
		String json = "[{\"phone\":\"15556670001\",\"content\":\"62467D4B9162D8B3909151847D6B2E61252BF61F590D3B59AC013101DBD786AA\",\"isParam\":\"0\"}]";
//		JSONArray parent = JSONArray.fromObject(json);
//		for(int i = 0; i < parent.size(); i++)
//		{
//			JSONObject child = parent.getJSONObject(i);
//			System.out.println(child.getString("phone"));
//		}
		
//		System.out.println(readJsonResult(json));
//		xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><RESULT><RETCODE>1</RETCODE><LIST><OBJECT i=\"0\"><A>2</A><B>2</B></OBJECT><OBJECT i=\"1\"><A>2</A><B>2</B></OBJECT></LIST></RESULT>";
		System.out.println(new ReadRespUtils().readJsonResult(json).toString());
	}
	
}
