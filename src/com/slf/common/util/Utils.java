package com.slf.common.util;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;


public class Utils {

	
	public static String bytesToOrcHex(String src,String key)
	{
		String result = "";
		Hex hex = new Hex();
		String sign = new String(hex.encode(src.getBytes()));
		sign += key;
		byte temp[] = null;
		try {
			temp = KeyedDigestMD5.getKeyedDigest(hex.decode(sign.getBytes()),"".getBytes());
			for (int i=0; i<temp.length; i++){
				result+=Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
			}
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
		}
		
		return result.toUpperCase();
	}
	
	/**
	 * 计算模版短信长度
	 * @param content
	 * @return
	 */
	public static int dealTempSmsLen(String s)
	{
		if(null == s)
		{
			return 0;
		}
		int smsLen = 0;
		String str[] = s.split("\\{");
		
		for(int i = 0; i < str.length; i++)
		{
			if(i == 0)
			{
				smsLen += str[i].length();
			}else{
				String s1 = str[i].substring(str[i].indexOf("}") + 1, str[i].length());
				smsLen += s1.length();
			}
			if(i < str.length -1)
			{
				int len = Integer.valueOf(str[i+1].substring(0, str[i+1].indexOf("}")));
				smsLen += len;
			}
		}
		return smsLen;
	}
	
	
	/**
	 * 校验号码群，只能为号码和,号
	 * @param phone
	 * @return true校验通过
	 */
	public static boolean checkHmq(String phone)
	{
		Pattern pattern = Pattern.compile("^[0-9]{11}$");
		Matcher matcher = pattern.matcher(phone);
		return matcher.matches();
	}
	
	/**
	 * 校验4位数字
	 * @param phone
	 * @return true校验通过
	 */
	public static boolean check4Num(String phone)
	{
		Pattern pattern = Pattern.compile("^[0-9]{1,8}$");
		Matcher matcher = pattern.matcher(phone);
		return matcher.matches();
	}
	
	public static void main(String args[])
	{
		String s = "[测试】测试短信";
		System.out.println(hasSuffix(s));
	}
	
	
	/**
	 * 判断是否存在后缀签名
	 * @param input
	 * @return 
	 */
	public static String getSuffix(String input)
	{
		if(input == null || "".equals(input.trim()))
		{
			return null;
		}
		String startFlag = input.substring(0, 1);
		int end = -1;
		if(startFlag.equals("["))
		{
			end = input.indexOf("]");
		}else if(startFlag.equals("【")){
			end = input.indexOf("】");
		}else{
			return null;
		}
		if(end == -1)
		{
			return null;
		}
		return input.substring(1, end);
	
	}
	
	public static int hasSuffix(String input)
	{
//		if(input == null || "".equals(input.trim()))
//		{
//			return 0;
//		}
//		String startFlag = input.substring(0, 1);
//		int end = -1;
//		if(startFlag.equals("["))
//		{
//			end = input.indexOf("]");
//		}else if(startFlag.equals("【")){
//			end = input.indexOf("】");
//		}else{
//			return 0;
//		}
//		if(end == -1)
//		{
//			return 0;
//		}
		return 1;
	}
}