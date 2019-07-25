package com.export.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class ExportTool2
{
	public static Properties props;

	public static void copyFile(String source, String baseFromPath, String baseToPath)
	{
		String fromPath = source;
		if (source.indexOf(baseFromPath) < 0) {
			fromPath = baseFromPath + File.separator + source;
		}
		if ((source.indexOf("/src/") > -1) && (source.indexOf("WEB-INF") < 0)) {
			fromPath = baseFromPath + File.separator + source
					.replaceAll("/src/", new StringBuilder("/")
							.append("WebContent").append("/WEB-INF/classes/").toString());
		}
		if (fromPath.indexOf("/WEB-INF/classes/") > -1) {
			fromPath = fromPath.replaceAll("java$", "class");
		}

		String toPath = fromPath.replace(baseFromPath, baseToPath);
		File souceFile = new File(fromPath);
		if ((souceFile.isDirectory()) && (!fromPath.endsWith("CVS")) && (!fromPath.endsWith("CVS/")) && 
				(!fromPath.endsWith(".svn")) && (!fromPath.endsWith(".svn/"))) {
			File tof = new File(toPath);
			if (!tof.exists()) tof.mkdirs();

			String[] fps = souceFile.list();
			for (String fp : fps) {
				copyFile(fromPath + File.separator + fp, baseFromPath, baseToPath);
			}
		} else {
			if (source.indexOf("/WEB-INF/classes/") > -1) {
				source = source.replaceAll("java$", "class");
			}
			String wjmc = souceFile.getName().substring(0, souceFile.getName().lastIndexOf("."));
			String wjgs = fromPath.substring(fromPath.lastIndexOf(".") + 1, fromPath.length());
			File classFile = new File(fromPath.substring(0, fromPath.lastIndexOf("/") + 1));
			String[] classf = classFile.list();
			for (String fp : classf) {
				if (fp.startsWith(wjmc) && fp.endsWith(wjgs)) {
					toPath = toPath.substring(0, toPath.lastIndexOf("/") + 1) + fp;
					File sourceFileNew = null;
					File outf = new File(toPath);
					FileInputStream fins = null;
					FileOutputStream fous = null;
					try {
						if (souceFile.isHidden()) return;
						outf.getParentFile().mkdirs();
						
						sourceFileNew = new File(souceFile.getParent() + File.separator + fp);
						fins = new FileInputStream(sourceFileNew);
						fous = new FileOutputStream(outf);
		
						byte[] buffer = new byte[4096];
						int len;
						while ((len = fins.read(buffer)) > 0)
						{
							fous.write(buffer, 0, len);
						}
						System.out.println("抽取文件:" + sourceFileNew.getPath());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						try {
							if (fins != null) fins.close(); 
						} catch (IOException localIOException3) {}
						try { 
							if (fous != null) fous.close();
						} catch (IOException localIOException4) {}
					} catch (IOException e) {
						e.printStackTrace();
						try {
							if (fins != null) fins.close(); 
						} catch (IOException localIOException5) {}
						try {
							if (fous != null) fous.close();
						} catch (IOException localIOException6) {}
					} finally {
						try {
							if (fins != null) fins.close(); 
						} catch (IOException localIOException7) {}
						try {
							if (fous != null) fous.close();  
						} catch (IOException localIOException8) {}
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		Properties props = new Properties();
		try {
			FileInputStream fins = new FileInputStream(new File("WebRoot/WEB-INF/config/conf.properties"));
			props.load(fins);

			String writePath = props.getProperty("project.outPath", "/patches");
			String readPath = props.getProperty("project.location");
			String encoding = props.getProperty("project.encoding", "UTF-8");

			File file = new File("WebRoot/WEB-INF/config/path.txt");
			if ((file.isFile()) && (file.exists())) {
				File outDir = new File(writePath);
				if (outDir.exists()) {	//删除已有文件
					ExportTool.delFolder(writePath);
				}
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String path0 = null;
				while ((path0 = bufferedReader.readLine()) != null) {
					if (!"".equals(path0.trim())) copyFile(path0.trim(), readPath, writePath);
				}
				read.close();
				System.out.println("完成");
			} else {
				System.out.println("修改列表文件path.txt不存在!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
