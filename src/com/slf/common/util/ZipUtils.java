package com.slf.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.commons.mail.Email;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtils {

	/**
	 * 压缩文件方法
	 * @param file 压缩文件
	 * @param out 压缩输出流
	 * @param dir 压缩目的目录
	 * @throws IOException
	 */
	public static void zip(File file, ZipOutputStream out,String dir) throws IOException
	{
		if(file.isDirectory())
		{
			File listFile[] = file.listFiles();
			if(listFile.length == 0)
			{
				out.putNextEntry(new ZipEntry(dir + file.getName() + "/"));
				return;
			}else{
				for(File cfile: listFile)
				{
					zip(cfile, out, dir + file.getName() + "/");
				}
			}
		}else if(file.isFile()){
			byte bt[] = new byte[1024];
			ZipEntry entry = new ZipEntry(dir + file.getName());
			entry.setSize(file.length());
			out.putNextEntry(entry);
			FileInputStream in = null;
			try{
				in = new FileInputStream(file);
				int i = 0;
				while((i = in.read(bt)) != -1)
				{
					out.write(bt, 0, i);
				}
			}catch (IOException e) {
				// TODO: handle exception
			}finally{
				if(in != null)
				{
					in.close();
				}
			}
		}
	}
	
	/**
	 * 获取压缩包内文件实际大小
	 * @param file
	 * @return
	 */
	public static long getZipFileSize(File file)
	{
		ZipFile zipFile = null;
		long size = 0;
		try {
			zipFile = new ZipFile(file);
			
			Enumeration<ZipEntry> enums = zipFile.getEntries();
			while(enums.hasMoreElements())
			{
				ZipEntry entry = enums.nextElement();
				if(!entry.isDirectory())
				{
					size += entry.getSize();
					String fileName = entry.getName().toLowerCase();
					if(!fileName.equals("txt") || !fileName.equals("mp3") || !fileName.equals("arm")
							|| !fileName.equals("mid") || !fileName.equals("mpeg") || !fileName.equals("jpg")
							|| !fileName.equals("gif") || !fileName.equals("bmp") || !fileName.equals("jpeg")
							|| !fileName.equals("mp4") || !fileName.equals("3gpp"))
					{
						return 0;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} finally{
			if(zipFile != null)
			{
				try {
					zipFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		return size;
	}
}
