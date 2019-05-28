package com.export.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class ExportTool
{
	public static Properties props;
	
	/**
	 * 
	 * copyFile:(这里用一句话描述这个方法的作用). 
	 * 
	 * @author use 
	 * @param source		源路径
	 * @param baseFromPath	源文件夹路径
	 * @param baseToPath	目标文件夹路径
	 */
	public static void copyFile(String source, String baseFromPath, String baseToPath)
	{
		String fromPath = source;
		if (source.indexOf(baseFromPath) < 0) {
			fromPath = baseFromPath + File.separator + source;
		}
		if ((source.indexOf("/src/main/java/") > -1) && (source.indexOf("target") < 0)) {
			fromPath = baseFromPath + File.separator + source
					.replaceAll("/src/main/java/", new StringBuilder("/")
					.append("/target/classes/").toString());
		}
		if (fromPath.indexOf("/target/classes/") > -1) {
			fromPath = fromPath.replaceAll("java$", "class");
		}

		String toPath = fromPath.replace(baseFromPath, baseToPath);

		File souceFile = new File(fromPath);
		if ((souceFile.isDirectory()) && (!fromPath.endsWith("CVS")) && (!fromPath.endsWith("CVS/")) && 
				(!fromPath.endsWith(".svn")) && (!fromPath.endsWith(".svn/"))) {
			File tof = new File(toPath);
			if (!tof.exists()) tof.mkdirs();

			String[] fps = souceFile.list();
			for (String fp : fps)
				copyFile(fromPath + File.separator + fp, baseFromPath, baseToPath);
		} else {
			if (source.indexOf("/target/classes/") > -1) {
				source = source.replaceAll("java$", "class");
			}
			String path = fromPath.substring(0, fromPath.lastIndexOf("/") + 1);
			String name = fromPath.substring(fromPath.lastIndexOf("/") + 1, fromPath.lastIndexOf("."));
			System.out.println("name------" + name);
			if (toPath.indexOf("target") >= 0) {
				toPath = toPath.replace("target", "WebContent/WEB-INF");
			}
			if (toPath.indexOf("main") >= 0) {
				toPath = toPath.replace("src/main/webapp", "WebContent");
			}
	
			File classFile = new File(fromPath.substring(0, fromPath.lastIndexOf("/") + 1));
			String[] classf = classFile.list();
			for (String fp : classf) {
				if (fp.startsWith(name)) {
					toPath = toPath.substring(0, toPath.lastIndexOf("/") + 1) + fp;
	
					File outf = new File(toPath);
					FileInputStream fins = null;
					FileOutputStream fous = null;
					File souceFileNew = new File(path + fp);
		          	try
		          	{
		          		if (souceFile.isHidden()) return;
		          		outf.getParentFile().mkdirs();
	
		          		fins = new FileInputStream(souceFileNew);
		          		fous = new FileOutputStream(outf);
	
		          		byte[] buffer = new byte[4096];
		          		int len;
		          		while ((len = fins.read(buffer)) > 0)
		          		{
		          			//int len;
		          			fous.write(buffer, 0, len);
		          		}
		          		System.out.println("抽取文件:" + souceFileNew.getPath());
		          	} catch (FileNotFoundException e) {
		          		e.printStackTrace();
		          		try{
		          			if (fins != null) fins.close(); 
		          		} catch (IOException localIOException3) {}
		          		try { 
		          			if (fous != null) fous.close();
		          		} catch (IOException localIOException4){}
		          		try {
		          			if (fins != null) fins.close(); 
		          		} catch (IOException localIOException5) {}
		          		try { 
		          			if (fous != null) fous.close();
		          		} catch (IOException localIOException6) {}
		          	} catch (IOException e) {
		          		e.printStackTrace();
		          		try {
		          			if (fins != null) fins.close(); 
		          		} catch (IOException localIOException7) {}
			            try { 
			            	if (fous != null) fous.close();
			            } catch (IOException localIOException8) {}
			            try {
			            	if (fins != null) fins.close(); 
			            } catch (IOException localIOException9) {}
			            try { 
			            	if (fous != null) fous.close();
			            } catch (IOException localIOException10) {}
		          	} finally {
		          		try {
		          			if (fins != null) fins.close(); 
		          		} catch (IOException localIOException11) {}
		          		try { 
		          			if (fous != null) fous.close();  
		          		} catch (IOException localIOException12) {}
		          	}
				}
			}
		}
	}

	public static void main(String[] args) {
		Properties props = new Properties();
		try
		{	
			//servlet文件读取配置文件
			//使用ServletContext
			//非servlet文件读取配置文件
//			ClassLoader loader = ExportTool.class.getClassLoader();
//			用类装载器读取src下配置文件conf.properties
//			InputStream fins = loader.getResourceAsStream("conf.properties");
//			用类装载器读取包com.export.tool下配置文件conf.properties
//			InputStream fins = loader.getResourceAsStream("com/export/tool/conf.properties");
//			int index = 0;
//			byte[] b = new byte[1024];
//			while ((index = fins.read(b, 0, b.length)) > 0) {
//				System.out.println(new String(b, 0, index, "UTF-8"));
//			}
//			props.load(fins);
			
			props.load(new FileInputStream(new File("WebRoot/WEB-INF/config/conf.properties")));

			String writePath = props.getProperty("project.outPath");
			String readPath = props.getProperty("project.location");
			String encoding = props.getProperty("project.encoding", "UTF-8");

			File file = new File("WebRoot/WEB-INF/config/path.txt");
			if ((file.isFile()) && (file.exists())) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String path0 = null;
				while ((path0 = bufferedReader.readLine()) != null) {		//逐个文件复制
					if (!"".equals(path0.trim())) {
						copyFile(path0, readPath, writePath);
					}
				}
				read.close();
				System.out.println("完成");
			} else {
				System.out.println("修改列表文件path.txt不存在!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {}
	}
}