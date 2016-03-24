package com.slf.common.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.slf.common.util.CryptUtils;
import com.slf.sms.common.CommonContants;


public class HttpSqsServiceImpl implements IHttpSqsService {

	private static final Logger log = Logger.getLogger(HttpSqsServiceImpl.class);
	private static final String DEFAULT_CHARSET = "utf-8";
	private static String mapToStr(Map<String, String> obj)
	{
		StringBuffer str = new StringBuffer();
		Iterator<String> keys = obj.keySet().iterator();
		while(keys.hasNext())
		{
			String key = keys.next();
			str.append(key);
			str.append("=");
			str.append(obj.get(key));
			str.append("|");
		}
		return str.toString();
	}
	
	public boolean resetSqs(String name)
	{
		BufferedReader instream = null;
		StringBuffer strUrl =new StringBuffer(CommonContants.HTTPSQS_URL);
		strUrl.append("?name=");
		strUrl.append(name);
		strUrl.append("&opt=reset");
		strUrl.append("&auth=");
		String auth = CommonContants.HTTPSQS_AUTH.startsWith("{3DES}") ? CryptUtils.decrypt(CommonContants.HTTPSQS_AUTH.replace("{3DES}", ""),CryptUtils.PASSWORD_CRYPT_KEY) : CommonContants.HTTPSQS_AUTH;
		strUrl.append(auth);
		URL url = null;
        try {
            url = new URL(strUrl.toString());
            instream = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = null;
            StringBuffer result = new StringBuffer("");
            while((s = instream.readLine()) != null)
            {
                result.append(s);
            }
            if(result.toString().indexOf("HTTPSQS_RESET_OK") != -1)
            {
            	 return true;
            }
        } catch (MalformedURLException e) {
        		log.log(Priority.ERROR, "the httpsqs error->get");
        } catch (IOException e) {
			// TODO Auto-generated catch block
        	log.log(Priority.ERROR, "the httpsqs error->get");
		}finally{
        	if(instream != null)
        	{
        		try {
					instream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
        	}
        }
        return false;
	}
	
	public boolean putIntoSqs(String name,Map<String, String> obj)
	{
		BufferedReader instream = null;
		StringBuffer strUrl =new StringBuffer(CommonContants.HTTPSQS_URL);
		strUrl.append("?name=");
		strUrl.append(name);
		strUrl.append("&opt=put");
		strUrl.append("&data=");
		try {
			strUrl.append(URLEncoder.encode(mapToStr(obj), DEFAULT_CHARSET));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
		}
		String auth = CommonContants.HTTPSQS_AUTH.startsWith("{3DES}") ? CryptUtils.decrypt(CommonContants.HTTPSQS_AUTH.replace("{3DES}", ""),CryptUtils.PASSWORD_CRYPT_KEY) : CommonContants.HTTPSQS_AUTH;
		strUrl.append("&auth=");
		strUrl.append(auth);
		URL url = null;
        try {
//        		log.log(Priority.INFO, strUrl.toString());
            url = new URL(strUrl.toString());
            instream = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = null;
            StringBuffer result = new StringBuffer("");
            while((s = instream.readLine()) != null)
            {
                result.append(s);
            }
            if(result.toString().indexOf("HTTPSQS_PUT_OK") != -1)
            {
            	return true;
            }
        } catch (MalformedURLException e) {
    		log.log(Priority.ERROR, "the httpsqs error->put");
        } catch (IOException e) {
			// TODO Auto-generated catch block
        	log.log(Priority.ERROR, "the httpsqs error->put");
		}finally{
        	if(instream != null)
        	{
        		try {
					instream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
        	}
        }
		return false;
	}
	
	
	private static Map<String, String> strToMap(String str)
	{
		Map<String, String> obj = new HashMap<String, String>();
		String ss[] = str.split("\\|");
		for(int i = 0; i < ss.length; i++)
		{
			String s = ss[i];
			if(s.length() > 0)
			{
				String sobj[] = s.split("\\=");
				if(sobj.length == 2)
				{
					obj.put(sobj[0], sobj[1]);
				}else{
					obj.put(sobj[0], "");
				}
			}
		}
		return obj;
	}
	public Map<String, String> getFromSqs(String name)
	{
		BufferedReader instream = null;
		StringBuffer strUrl =new StringBuffer(CommonContants.HTTPSQS_URL);
		strUrl.append("?name=");
		strUrl.append(name);
		strUrl.append("&opt=get");
		strUrl.append("&auth=");
		String auth = CommonContants.HTTPSQS_AUTH.startsWith("{3DES}") ? CryptUtils.decrypt(CommonContants.HTTPSQS_AUTH.replace("{3DES}", ""),CryptUtils.PASSWORD_CRYPT_KEY) : CommonContants.HTTPSQS_AUTH;
		strUrl.append(auth);
		URL url = null;
        try {
            url = new URL(strUrl.toString());
            instream = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = null;
            StringBuffer result = new StringBuffer("");
            while((s = instream.readLine()) != null)
            {
                result.append(s);
            }
            if(result.toString().indexOf("HTTPSQS_GET_END") == -1)
            {
            	 return strToMap(URLDecoder.decode(result.toString(),DEFAULT_CHARSET));
            }
        } catch (MalformedURLException e) {
        		log.log(Priority.ERROR, "the httpsqs error->get");
        } catch (IOException e) {
			// TODO Auto-generated catch block
        	log.log(Priority.ERROR, "the httpsqs error->get");
		}finally{
        	if(instream != null)
        	{
        		try {
					instream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
        	}
        }
        return null;
	}
	
	public static void main(String args[])
	{
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("fshm", "13858001723");
		obj.put("fsnr", "已暂停系统业务【2013-04-15 2001:54:00】");
		obj.put("jkdxid", "0");
		IHttpSqsService httpSqsService = new HttpSqsServiceImpl();
//		httpSqsService.putIntoSqs(CommonContants.HTTPSQS_SMS,obj);
//		System.out.println(httpSqsService.getFromSqs(CommonContants.HTTPSQS_SMS).toString());
//		StringBuffer strUrl =new StringBuffer(CommonContants.HTTPSQS_URL);
//		strUrl.append("?name=");
//		strUrl.append(CommonContants.HTTPSQS_SMS);
//		strUrl.append("&opt=put");
//		strUrl.append("&data=");
//		try {
//			strUrl.append(URLEncoder.encode(mapToStr(obj), CommonContants.DEFAULT_CHARSET));
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//		}
//		String auth = CommonContants.HTTPSQS_AUTH.startsWith("{3DES}") ? CryptUtils.decrypt(CommonContants.HTTPSQS_AUTH.replace("{3DES}", "")) : CommonContants.HTTPSQS_AUTH;
//		strUrl.append("&auth=");
//		strUrl.append(auth);
//		
//		System.out.println(strUrl.toString());
	}
}
