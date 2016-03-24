package com.slf.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class ReadProperties {
	
	private static Properties prop = null;
	
	/**
	 * fileName需要带后缀名
	 * 资源文件必须在src直接目录下
	 * @param fileName
	 * @return
	 */
	public static Properties getProp(String fileName)
	{
		if(null == prop)
		{
			prop = new Properties();
			
			try {
				File pf = new File(fileName);
				
				if(pf.exists()){
					prop.load((new FileInputStream(pf)));
				} else {
					prop.load(ReadProperties.class.getResourceAsStream("/"+fileName));
				}
				
			} catch (IOException e) {
				// TODO 自动生成 catch 块
				e.printStackTrace();
			}
		}
		return prop;
	}
}
