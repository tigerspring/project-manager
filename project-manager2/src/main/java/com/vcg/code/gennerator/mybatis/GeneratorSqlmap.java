package com.vcg.code.gennerator.mybatis;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.vcg.common.base.BaseDao;
import com.vcg.common.base.BaseExample;
import com.vcg.common.base.BaseModel;
import com.vcg.common.base.BaseService;
import com.vcg.common.base.BaseServiceImpl;
import com.vcg.common.utils.FileUtil;

public class GeneratorSqlmap {

	/*spring.datasource1.url=jdbc:mysql://123.56.248.214/gettychina?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull
		spring.datasource1.username=kangminggang
		spring.datasource1.password=FFg04wJO*/
	/*private static String url = "jdbc:mysql://123.56.248.214:3306/gettyphoto";
	private static String username = "kangminggang";
	private static String password = "FFg04wJO";*/
	
	/*private static String url = "jdbc:mysql://101.200.220.163:3306/ph";
	private static String username ="kangminggang";
	private static String password = "FFg04wJO";*/
	
	private static String url = "jdbc:mysql://219.239.94.125:3306/provider";
	private static String username ="cfpswap";
	private static String password = "cfp_Swap_2012";
		
	
	/*private static String url = "jdbc:mysql://rm-2ze94l5qkx3920xglo.mysql.rds.aliyuncs.com:3306/edit";
	private static String username = "photo";
	private static String password = "photo@1234";*/

	/*private static String url = "jdbc:mysql://rm-2ze5t95m5802gd20po.mysql.rds.aliyuncs.com/boss";
	private static String username = "boss";
	private static String password = "boss123abcH";*/
	
	private static String driverClass = "com.mysql.jdbc.Driver";
	

	private static String baseDao = BaseDao.class.getName();
	private static String baseModel = BaseModel.class.getName();
	private static String baseService = BaseService.class.getName();
	private static String baseServiceImpl = BaseServiceImpl.class.getName();
	private static String baseExample = BaseExample.class.getName();

	public static void main(String[] args) throws Exception {
		GeneratorSqlmap generatorSqlmap = new GeneratorSqlmap();
		boolean isGenerator=true;
		List lst=null;
		lst=Arrays.asList("origin_brand");
		//lst=new ArrayList();
		FileInputStream in = generatorSqlmap.genneratorBoot(url, username, password, driverClass, "cfp", "com.vcg.originProvider",
				lst,isGenerator);
		
		delAllFile("e:/output/");
		
		IOUtils.copy(in, new FileOutputStream(new File("e:/output/outstorage.zip")));
		System.err.println("生成ok....");
		
		unZipFiles("e:/output/outstorage.zip","e:/output/");
		
	}
	
    /** 
     * 解压到指定目录 
     * @param zipPath 
     * @param descDir 
     * @author isea533 
     */  
    public static void unZipFiles(String zipPath,String descDir)throws IOException{  
        unZipFiles(new File(zipPath), descDir);  
    }  
    /** 
     * 解压文件到指定目录 
     * @param zipFile 
     * @param descDir 
     * @author isea533 
     */  
    @SuppressWarnings("rawtypes")  
    public static void unZipFiles(File zipFile,String descDir)throws IOException{  
        File pathFile = new File(descDir);  
        if(!pathFile.exists()){  
            pathFile.mkdirs();  
        }  
        ZipFile zip = new ZipFile(zipFile);  
        for(Enumeration entries = zip.entries();entries.hasMoreElements();){  
            ZipEntry entry = (ZipEntry)entries.nextElement();  
            String zipEntryName = entry.getName();  
            InputStream in = zip.getInputStream(entry);  
            String outPath = (descDir+zipEntryName).replaceAll("\\*", "/");;  
            //判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
            if(!file.exists()){  
                file.mkdirs();  
            }  
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压  
            if(new File(outPath).isDirectory()){  
                continue;  
            }  
            //输出文件路径信息  
            System.out.println(outPath);  
              
            OutputStream out = new FileOutputStream(outPath);  
            byte[] buf1 = new byte[1024];  
            int len;  
            while((len=in.read(buf1))>0){  
                out.write(buf1,0,len);  
            }  
            in.close();  
            out.close();  
            }  
        System.out.println("******************解压完毕********************");  
    }  
	public static boolean delAllFile(String path) {
	       boolean flag = false;
	       File file = new File(path);
	       if (!file.exists()) {
	         return flag;
	       }
	       if (!file.isDirectory()) {
	         return flag;
	       }
	       String[] tempList = file.list();
	       File temp = null;
	       for (int i = 0; i < tempList.length; i++) {
	          if (path.endsWith(File.separator)) {
	             temp = new File(path + tempList[i]);
	          } else {
	              temp = new File(path + File.separator + tempList[i]);
	          }
	          if (temp.isFile()) {
	             temp.delete();
	          }
	          if (temp.isDirectory()) {
	             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
	             delFolder(path + "/" + tempList[i]);//再删除空文件夹
	             flag = true;
	          }
	       }
	       return flag;
	     }
	  public static void delFolder(String folderPath) {
		     try {
		        delAllFile(folderPath); //删除完里面所有内容
		        String filePath = folderPath;
		        filePath = filePath.toString();
		        java.io.File myFilePath = new java.io.File(filePath);
		        myFilePath.delete(); //删除空文件夹
		     } catch (Exception e) {
		       e.printStackTrace(); 
		     }
		}
	
	public FileInputStream genneratorCloud(String url, String username, String password, String driverClass,
			String project, String packagePath, List<String> tbls,boolean isGenerator) throws Exception {
		String targetPath = "/"
				+ Thread.currentThread().getContextClassLoader().getResource("out").toString().substring(6) + "/"
				+ new Date().getTime();
		File targetPathDir = new File(targetPath);
		if (!targetPathDir.exists()) {
			targetPathDir.mkdirs();
		}
		Class.forName(driverClass);
		Connection connection = DriverManager.getConnection(url, username, password);
		if (tbls == null || tbls.size() == 0) {
			tbls = TblUtil.getTbls(connection);
		}
		String parentProject = targetPath + "/" + project + "-parent/";
		String baseProject = parentProject + project + "-interface/";
		String serviceImplProject = parentProject + project + "-service/";

		String serviceImplPath = serviceImplProject + "/src/main/java/";
		String basePath = baseProject + "/src/main/java/";
		
		String resources = serviceImplPath.replace("/java/", "/resources/");
		String configStr = VelocityUtil.writerGeneratorConfig(url, username, password, driverClass, packagePath,
				serviceImplPath, tbls);
		
		for (String table : tbls) {
			getService(connection, serviceImplPath + "/" + packagePath.replace(".", "/") + "/", packagePath, table);
			getServiceImpl(connection, serviceImplPath + "/" + packagePath.replace(".", "/") + "/", packagePath, table);
			getWeb(connection, serviceImplPath + "/" + packagePath.replace(".", "/") + "/", packagePath, table);
			//get client
			getFeignClient(basePath + "/" + packagePath.replace(".", "/") + "/", packagePath, project);
			
		}
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp
				.parseConfiguration(new InputStreamReader(new ByteArrayInputStream(configStr.getBytes())));
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGeneratorWrap myBatisGenerator = new MyBatisGeneratorWrap(config, callback, warnings);
		myBatisGenerator.setBaseDao(baseDao);
		myBatisGenerator.setConn(connection);
		myBatisGenerator.setBaseModel(baseModel);
		myBatisGenerator.setBaseExample(baseExample);
		myBatisGenerator.setGenerator(isGenerator);
		myBatisGenerator.generate(null, null, null);

		configStr = configStr.replace(serviceImplPath, ".\\src\\main\\java");
		
		FileUtils.moveDirectoryToDirectory(new File(serviceImplPath + "/" + packagePath.replace(".", "/") + "/model"),
				new File(basePath + "/" + packagePath.replace(".", "/")), true);
		
		//resources
		String srcResourceDir = "/" + Thread.currentThread().getContextClassLoader()
				.getResource("template/cloud/resources").toString().substring(6);
		File config2 = new File(resources+"config");
		if(!config2.exists()){
			config2.mkdir();
		}
		FileUtils.copyFile(new File(srcResourceDir+"/config/configuration.xml"), new File(resources+"/config/configuration.xml"));
		
		writeApplicationProperties("template/cloud/resources/application.yml", resources + "/application.yml", url,
				username, password,project);
		writeApplicationProperties("template/cloud/resources/bootstrap.yml", resources + "/bootstrap.yml", url,
				username, password,project);
		
		//bean config
		String srcbeanconfig = "/" + Thread.currentThread().getContextClassLoader().getResource("template/cloud/java/")
				.toString().substring(6);
		 File desbeanconfig = new File(serviceImplPath + "/com/vcg/");
		 if(!desbeanconfig.exists()){
			 desbeanconfig.mkdirs();
		 }
		 FileUtils.copyDirectory(new File(srcbeanconfig), desbeanconfig);
		 
		 //覆盖mybatis scanner
		 Map<String,String> params=new HashMap<String,String>();
		 params.put("basePackage", packagePath+".dao");
		 writeByTemplate("template/cloud/java/config/MyBatisMapperScannerConfig.java", 
				 serviceImplPath + "/com/vcg/config/MyBatisMapperScannerConfig.java",params);
		 
		 //DeserializationContext.java
		 String deseriSrc = "/" + Thread.currentThread().getContextClassLoader().getResource("template/cloud/third/DeserializationContext.java")
					.toString().substring(6);
		 File Deserializ = new File(serviceImplPath + "/com/fasterxml/jackson/databind/");
		 if(!Deserializ.exists()){
			 Deserializ.mkdirs();
		 }
		 FileUtils.copyFile(new File(deseriSrc), new File(serviceImplPath + "/com/fasterxml/jackson/databind/DeserializationContext.java") );
		 
		 
		String pomDir = "template/cloud/";
		
		writeParentPom(pomDir + "/parent.xml", parentProject, project);
		writeServiceInterfacePom(pomDir + "/base.xml", baseProject, project);
		writeServiceImplPom(pomDir + "/service.xml", serviceImplProject, project);
	
		
		String mainRun = "/" + Thread.currentThread().getContextClassLoader().getResource("template/cloud/Main.vm")
				.toString().substring(6);
		FileUtils.copyFile(new File(mainRun), new File(serviceImplPath + "/com/vcg/Main.java"));
		String binDir = "/" + Thread.currentThread().getContextClassLoader().getResource("template/cloud/bin").toString()
				.substring(6);
		FileUtils.copyDirectory(new File(binDir), new File(serviceImplProject));
		
		
		String installDir = "/"
				+ Thread.currentThread().getContextClassLoader().getResource("template/cloud/install").toString().substring(6);
		FileUtils.copyDirectory(new File(installDir), new File(parentProject));
	
	
	
		final List<File> oldXmlFile = new ArrayList<File>();
		FileUtils.copyDirectory(new File(serviceImplPath + "/" + packagePath.replace(".", "/") + "/dao"),
				new File(resources + "/" + packagePath.replace(".", "/") + "/dao"), new FileFilter() {

					public boolean accept(File pathname) {

						int lastIndexOf = pathname.getName().lastIndexOf(".xml");
						if (lastIndexOf != -1) {
							oldXmlFile.add(pathname);
						}
						return lastIndexOf != -1;
					}
				});
		for (File file : oldXmlFile) {
			file.delete();
		}
		String zipFile = "/" + Thread.currentThread().getContextClassLoader().getResource("out").toString().substring(6)
				+ "/" + new Date().getTime();
		FileUtil.zip(targetPath, zipFile);
		FileUtils.deleteQuietly(new File(targetPath));
		return new FileInputStream(zipFile);

	}
	
	
	public FileInputStream genneratorBoot(String url, String username, String password, String driverClass,
			String project, String packagePath, List<String> tbls,boolean isGenerator) throws Exception {
		String targetPath = "/"
				+ Thread.currentThread().getContextClassLoader().getResource("out").toString().substring(6) + "/"
				+ new Date().getTime();
		File targetPathDir = new File(targetPath);
		if (!targetPathDir.exists()) {
			targetPathDir.mkdirs();
		}
		Class.forName(driverClass);
		Connection connection = DriverManager.getConnection(url, username, password);
		if (tbls == null || tbls.size() == 0) {
			tbls = TblUtil.getTbls(connection);
		}
		String parentProject = targetPath + "/" + project + "/";

		String serviceImplPath = parentProject + "/src/main/java/";
		String servicePath = parentProject + "/src/main/java/";
		String resources = serviceImplPath.replace("/java/", "/resources/");
		String configStr = VelocityUtil.writerGeneratorConfig(url, username, password, driverClass, packagePath,
				serviceImplPath, tbls);

		for (String table : tbls) {
			getService(connection, servicePath + "/" + packagePath.replace(".", "/") + "/", packagePath, table);
			getServiceImpl(connection, serviceImplPath + "/" + packagePath.replace(".", "/") + "/", packagePath, table);
			getWeb(connection, serviceImplPath + "/" + packagePath.replace(".", "/") + "/", packagePath, table);
			
		}
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp
				.parseConfiguration(new InputStreamReader(new ByteArrayInputStream(configStr.getBytes())));
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGeneratorWrap myBatisGenerator = new MyBatisGeneratorWrap(config, callback, warnings);
		myBatisGenerator.setBaseDao(baseDao);
		myBatisGenerator.setConn(connection);
		myBatisGenerator.setBaseModel(baseModel);
		myBatisGenerator.setBaseExample(baseExample);
		myBatisGenerator.setGenerator(isGenerator);
		myBatisGenerator.generate(null, null, null);

		configStr = configStr.replace(serviceImplPath, ".\\src\\main\\java");
		
		//resources
		String srcResourceDir = "/" + Thread.currentThread().getContextClassLoader()
				.getResource("template/boot/resources").toString().substring(6);
		File config2 = new File(resources+"config");
		if(!config2.exists()){
			config2.mkdir();
		}
		FileUtils.copyFile(new File(srcResourceDir+"/config/configuration.xml"), new File(resources+"/config/configuration.xml"));
		
		writeApplicationProperties("template/boot/resources/application.properties", resources + "/application.properties", url,
				username, password,null);
		writeApplicationProperties("template/boot/resources/application-test.properties", resources + "/application-test.properties", url,
				username, password,null);
		
		//bean config
		String srcbeanconfig = "/" + Thread.currentThread().getContextClassLoader().getResource("template/boot/java/")
				.toString().substring(6);
		 File desbeanconfig = new File(serviceImplPath + "/com/vcg/");
		 if(!desbeanconfig.exists()){
			 desbeanconfig.mkdirs();
		 }
		 FileUtils.copyDirectory(new File(srcbeanconfig), desbeanconfig);
		 
		 //覆盖mybatis scanner
		 Map<String,String> params=new HashMap<String,String>();
		 params.put("basePackage", packagePath+".dao");
		 writeByTemplate("template/boot/java/config/MyBatisMapperScannerConfig.java", 
				 servicePath + "/com/vcg/config/MyBatisMapperScannerConfig.java",params);
		 
		 //DeserializationContext.java
		 String deseriSrc = "/" + Thread.currentThread().getContextClassLoader().getResource("template/boot/third/DeserializationContext.java")
					.toString().substring(6);
		 File Deserializ = new File(serviceImplPath + "/com/fasterxml/jackson/databind/");
		 if(!Deserializ.exists()){
			 Deserializ.mkdirs();
		 }
		 FileUtils.copyFile(new File(deseriSrc), new File(serviceImplPath + "/com/fasterxml/jackson/databind/DeserializationContext.java") );
		 
		 
		String pomDir = "template/boot/";
		
		writeParentPom(pomDir + "/pom.xml", parentProject, project);
		
		String mainRun = "/" + Thread.currentThread().getContextClassLoader().getResource("template/boot/Main.vm")
				.toString().substring(6);
		FileUtils.copyFile(new File(mainRun), new File(serviceImplPath + "/com/vcg/Main.java"));
		String binDir = "/" + Thread.currentThread().getContextClassLoader().getResource("template/boot/bin").toString()
				.substring(6);
		FileUtils.copyDirectory(new File(binDir), new File(parentProject));
		
		
	
	
		final List<File> oldXmlFile = new ArrayList<File>();
		FileUtils.copyDirectory(new File(serviceImplPath + "/" + packagePath.replace(".", "/") + "/dao"),
				new File(resources + "/" + packagePath.replace(".", "/") + "/dao"), new FileFilter() {

					public boolean accept(File pathname) {

						int lastIndexOf = pathname.getName().lastIndexOf(".xml");
						if (lastIndexOf != -1) {
							oldXmlFile.add(pathname);
						}
						return lastIndexOf != -1;
					}
				});
		for (File file : oldXmlFile) {
			file.delete();
		}
		String zipFile = "/" + Thread.currentThread().getContextClassLoader().getResource("out").toString().substring(6)
				+ "/" + new Date().getTime();
		FileUtil.zip(targetPath, zipFile);
		FileUtils.deleteQuietly(new File(targetPath));
		return new FileInputStream(zipFile);

	}
	
	
	
	
	
	
	

	public FileInputStream generatorWeb(String url, String username, String password, String driverClass,
			String project, String packagePath, List<String> tbls, String containerType) throws Exception {
		String targetPath = "/"
				+ Thread.currentThread().getContextClassLoader().getResource("out").toString().substring(6) + "/"
				+ new Date().getTime();
		File targetPathDir = new File(targetPath);
		if (!targetPathDir.exists()) {
			targetPathDir.mkdirs();
		}
		Class.forName(driverClass);
		Connection connection = DriverManager.getConnection(url, username, password);
		if (tbls == null || tbls.size() == 0) {
			tbls = TblUtil.getTbls(connection);
		}
		String parentProject = targetPath + "/" + project + "/";

		String serviceImplPath = parentProject + "/src/main/java/";
		String servicePath = parentProject + "/src/main/java/";
		String resources = serviceImplPath.replace("/java/", "/resources/");
		String configStr = VelocityUtil.writerGeneratorConfig(url, username, password, driverClass, packagePath,
				serviceImplPath, tbls);

		for (String table : tbls) {
			getService(connection, servicePath + "/" + packagePath.replace(".", "/") + "/", packagePath, table);
			getServiceImpl(connection, serviceImplPath + "/" + packagePath.replace(".", "/") + "/", packagePath, table);
		}
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp
				.parseConfiguration(new InputStreamReader(new ByteArrayInputStream(configStr.getBytes())));
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGeneratorWrap myBatisGenerator = new MyBatisGeneratorWrap(config, callback, warnings);
		myBatisGenerator.setBaseDao(baseDao);
		myBatisGenerator.setConn(connection);
		myBatisGenerator.setBaseModel(baseModel);
		myBatisGenerator.setBaseExample(baseExample);
		myBatisGenerator.generate(null, null, null);

		configStr = configStr.replace(serviceImplPath, ".\\src\\main\\java");

		String srcResourceDir = "/" + Thread.currentThread().getContextClassLoader()
				.getResource("template/web/conf/resources").toString().substring(6);
		FileUtils.copyDirectory(new File(srcResourceDir), new File(resources));

		String pomDir = "template/web/";
		if (containerType.equals("2")) {
			writeParentPom(pomDir + "/externalContainer.xml", parentProject, project);
		} else {
			writeParentPom(pomDir + "/emContainer.xml", parentProject, project);
			String mainRun = "/" + Thread.currentThread().getContextClassLoader().getResource("template/web/Main.vm")
					.toString().substring(6);
			FileUtils.copyFile(new File(mainRun), new File(serviceImplPath + "/com/vcg/Main.java"));
			String binDir = "/" + Thread.currentThread().getContextClassLoader().getResource("template/bin").toString()
					.substring(6);
			FileUtils.copyDirectory(new File(binDir), new File(parentProject));

		}
		writerMyBatisConf("template/web/conf/resources/config/mybatis.xml", resources + "/config/mybatis.xml",
				packagePath);
		writeranotation("template/web/conf/resources/config/anotation.xml", resources + "/config/anotation.xml",
				packagePath);
		writeJdbc("template/web/conf/resources/properties/db.properties", resources + "/properties/db.properties", url,
				username, password, driverClass);
		writeDubboConf("template/web/conf/resources/config/dubbo-consumer.xml",
				resources + "/config/dubbo-consumer.xml", project, packagePath);

		String srcDir = "/" + Thread.currentThread().getContextClassLoader().getResource("template/web/webapp")
				.toString().substring(6);

		FileUtils.copyDirectory(new File(srcDir), new File(parentProject + "/src/main/webapp/"));

		final List<File> oldXmlFile = new ArrayList<File>();
		FileUtils.copyDirectory(new File(serviceImplPath + "/" + packagePath.replace(".", "/") + "/dao"),
				new File(resources + "/" + packagePath.replace(".", "/") + "/dao"), new FileFilter() {

					public boolean accept(File pathname) {

						int lastIndexOf = pathname.getName().lastIndexOf(".xml");
						if (lastIndexOf != -1) {
							oldXmlFile.add(pathname);
						}
						return lastIndexOf != -1;
					}
				});
		for (File file : oldXmlFile) {
			file.delete();
		}
		String zipFile = "/" + Thread.currentThread().getContextClassLoader().getResource("out").toString().substring(6)
				+ "/" + new Date().getTime();
		FileUtil.zip(targetPath, zipFile);
		FileUtils.deleteQuietly(new File(targetPath));
		return new FileInputStream(zipFile);

	}

	public FileInputStream generator(String url, String username, String password, String driverClass, String project,
			String packagePath, List<String> tbls) throws Exception {
		String targetPath = "/"
				+ Thread.currentThread().getContextClassLoader().getResource("out").toString().substring(6) + "/"
				+ new Date().getTime();
		File targetPathDir = new File(targetPath);
		if (!targetPathDir.exists()) {
			targetPathDir.mkdirs();
		}
		Class.forName(driverClass);
		Connection connection = DriverManager.getConnection(url, username, password);
		if (tbls == null || tbls.size() == 0) {
			tbls = TblUtil.getTbls(connection);
		}
		String parentProject = targetPath + "/" + project + "-parent/";
		String serviceProject = parentProject + project + "-service-interface/";
		String serviceImplProject = parentProject + project + "-service/";

		String serviceImplPath = serviceImplProject + "/src/main/java/";
		String servicePath = serviceProject + "/src/main/java/";
		String resources = serviceImplPath.replace("/java/", "/resources/");
		String configStr = VelocityUtil.writerGeneratorConfig(url, username, password, driverClass, packagePath,
				serviceImplPath, tbls);

		for (String table : tbls) {
			getService(connection, servicePath + "/" + packagePath.replace(".", "/") + "/", packagePath, table);
			getServiceImpl(connection, serviceImplPath + "/" + packagePath.replace(".", "/") + "/", packagePath, table);
		}
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp
				.parseConfiguration(new InputStreamReader(new ByteArrayInputStream(configStr.getBytes())));
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGeneratorWrap myBatisGenerator = new MyBatisGeneratorWrap(config, callback, warnings);
		myBatisGenerator.setBaseDao(baseDao);
		myBatisGenerator.setBaseModel(baseModel);
		myBatisGenerator.setBaseExample(baseExample);
		myBatisGenerator.generate(null, null, null);

		configStr = configStr.replace(serviceImplPath, ".\\src\\main\\java");

		// createDir(targetPath, project);
		String srcResourceDir = "/" + Thread.currentThread().getContextClassLoader()
				.getResource("template/conf/resources").toString().substring(6);
		FileUtils.copyDirectory(new File(srcResourceDir), new File(resources));
		FileUtils.moveDirectoryToDirectory(new File(serviceImplPath + "/" + packagePath.replace(".", "/") + "/model"),
				new File(servicePath + "/" + packagePath.replace(".", "/")), true);
		String pomDir = "template/pom";
		writeParentPom(pomDir + "/parentPom.vm", parentProject, project);
		writeServiceInterfacePom(pomDir + "/interfacePom.vm", serviceProject, project);
		writeServiceImplPom(pomDir + "/servicePom.vm", serviceImplProject, project);
		writerMyBatisConf("template/conf/resources/config/mybatis.xml", resources + "/config/mybatis.xml", packagePath);
		writeranotation("template/conf/resources/config/anotation.xml", resources + "/config/anotation.xml",
				packagePath);
		writeJdbc("template/conf/resources/properties/db.properties", resources + "/properties/db.properties", url,
				username, password, driverClass);
		writeDubboConf("template/conf/resources/config/dubbo-service.xml", resources + "/config/dubbo-service.xml",
				project, packagePath);

		String mainRun = "/" + Thread.currentThread().getContextClassLoader().getResource("template/Main.vm").toString()
				.substring(6);
		FileUtils.copyFile(new File(mainRun), new File(serviceImplPath + "/com/vcg/Main.java"));
		
		String binDir = "/"
				+ Thread.currentThread().getContextClassLoader().getResource("template/bin").toString().substring(6);
		FileUtils.copyDirectory(new File(binDir), new File(serviceImplProject));
		
		String installDir = "/"
				+ Thread.currentThread().getContextClassLoader().getResource("template/install").toString().substring(6);
		FileUtils.copyDirectory(new File(installDir), new File(parentProject));
		
		final List<File> oldXmlFile = new ArrayList<File>();
		FileUtils.copyDirectory(new File(serviceImplPath + "/" + packagePath.replace(".", "/") + "/dao"),
				new File(resources + "/" + packagePath.replace(".", "/") + "/dao"), new FileFilter() {

					public boolean accept(File pathname) {

						int lastIndexOf = pathname.getName().lastIndexOf(".xml");
						if (lastIndexOf != -1) {
							oldXmlFile.add(pathname);
						}
						return lastIndexOf != -1;
					}
				});
		for (File file : oldXmlFile) {
			file.delete();
		}
		String zipFile = "/" + Thread.currentThread().getContextClassLoader().getResource("out").toString().substring(6)
				+ "/" + new Date().getTime();
		FileUtil.zip(targetPath, zipFile);
		FileUtils.deleteQuietly(new File(targetPath));
		return new FileInputStream(zipFile);

	}
	
	public static void writeByTemplate(String templatePath, String dest, Map<String,String> params) throws IOException {
		Template tempate = VelocityUtil.getTempate(templatePath);
		VelocityContext ctx = new VelocityContext();
		for(Map.Entry<String, String> entry:params.entrySet()){
			ctx.put(entry.getKey(),entry.getValue());
		}
		
		FileWriter writer = new FileWriter(dest);
		tempate.merge(ctx, writer);
		writer.close();
	}

	public static void writeApplicationProperties(String templatePath, String dest, String url, String username, String password,
			String project) throws IOException {
		Template tempate = VelocityUtil.getTempate(templatePath);
		VelocityContext ctx = new VelocityContext();
		if(StringUtils.isNotBlank(url)){
			ctx.put("url", url);
		}
		if(StringUtils.isNotBlank(username)){
			ctx.put("username", username);
		}
		if(StringUtils.isNotBlank(password)){
			ctx.put("password", password);
		}
		if(StringUtils.isNotBlank(project)){
			ctx.put("project", project);
		}
		
		//ctx.put("driverClass", driverClass);
		FileWriter writer = new FileWriter(dest);
		tempate.merge(ctx, writer);
		writer.close();
	}
	
	
	
	public static void writeDubboConf(String templatePath, String dest, String project, String packagePath)
			throws IOException {
		Template tempate = VelocityUtil.getTempate(templatePath);
		VelocityContext ctx = new VelocityContext();
		ctx.put("project", project);
		ctx.put("packagePath", packagePath);
		FileWriter writer = new FileWriter(dest);
		tempate.merge(ctx, writer);
		writer.close();
	}

	public static void writeJdbc(String templatePath, String dest, String url, String username, String password,
			String driverClass) throws IOException {
		Template tempate = VelocityUtil.getTempate(templatePath);
		VelocityContext ctx = new VelocityContext();
		ctx.put("url", url);
		ctx.put("username", username);
		ctx.put("password", password);
		ctx.put("driverClass", driverClass);
		FileWriter writer = new FileWriter(dest);
		tempate.merge(ctx, writer);
		writer.close();
	}

	public static void writerMyBatisConf(String templatePath, String dest, String packagePath) throws IOException {
		Template tempate = VelocityUtil.getTempate(templatePath);
		VelocityContext ctx = new VelocityContext();
		ctx.put("packagePath", packagePath);
		ctx.put("packagePathDir", packagePath.replace(".", "/"));
		FileWriter writer = new FileWriter(dest);
		tempate.merge(ctx, writer);
		writer.close();
	}

	public static void writeranotation(String templatePath, String dest, String packagePath) throws IOException {
		Template tempate = VelocityUtil.getTempate(templatePath);
		VelocityContext ctx = new VelocityContext();
		ctx.put("packagePath", packagePath);
		ctx.put("packagePathDir", packagePath.replace(".", "/"));
		FileWriter writer = new FileWriter(dest);
		tempate.merge(ctx, writer);
		writer.close();
	}

	public static void writeServiceInterfacePom(String templatePath, String dest, String project) throws IOException {
		Template tempate = VelocityUtil.getTempate(templatePath);
		VelocityContext ctx = new VelocityContext();
		ctx.put("project", project);
		FileWriter writer = new FileWriter(dest + "/pom.xml");
		tempate.merge(ctx, writer);
		writer.close();
	}

	public static void writeServiceImplPom(String templatePath, String dest, String project) throws IOException {
		Template tempate = VelocityUtil.getTempate(templatePath);
		VelocityContext ctx = new VelocityContext();
		ctx.put("project", project);
		FileWriter writer = new FileWriter(dest + "/pom.xml");
		tempate.merge(ctx, writer);
		writer.close();
	}

	public static void writeParentPom(String templatePath, String dest, String project) throws IOException {
		Template tempate = VelocityUtil.getTempate(templatePath);
		VelocityContext ctx = new VelocityContext();
		ctx.put("project", project);
		FileWriter writer = new FileWriter(dest + "/pom.xml");
		tempate.merge(ctx, writer);
		writer.close();
	}

	public static void getService(Connection conn, String targetPath, String packagePath, String tableName)
			throws Exception {

		File file = new File(targetPath + "/service");
		if (!file.exists()) {
			file.mkdirs();
		}
		String primaryKey = TblUtil.getPrimaryKey(conn, tableName);
		String pkClass = "Object";
		if (StringUtils.isNotBlank(primaryKey)) {
			String type = TblUtil.getColunmType(conn, tableName, primaryKey);
			if (StringUtils.isNotBlank(type)) {
				pkClass = type.split("[.]")[type.split("[.]").length - 1];
				pkClass = resolveColunmType(pkClass);
			}
		}

		String modelName = VelocityUtil.tableNameConvertModelName(tableName);

		BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + "/service/" + modelName + "Service.java"));

		bw.write("package " + packagePath + ".service ;");
		bw.newLine();
		bw.newLine();

		bw.write("import " + baseService + ";");
		bw.newLine();

		bw.write("import " + packagePath + ".model." + modelName + ";");
		bw.newLine();

		bw.write("import " + packagePath + ".model." + "query." + modelName + "Example;");
		bw.newLine();
		bw.newLine();

		bw.write("public interface " + modelName + "Service extends "
				+ baseService.split("[.]")[baseService.split("[.]").length - 1] + "<" + modelName + "," + modelName
				+ "Example," + pkClass + ">{");
		bw.newLine();
		bw.newLine();

		bw.write("}");

		bw.close();

	}
	
	public static void getFeignClient(String target, String packagePath,String project)
			throws Exception {

		File file = new File(target + "/feignClient");
		if (!file.exists()) {
			file.mkdirs();
		}
		
		String dest=file.getAbsolutePath() + "/" + firstUppder(project)+"Client.java";
		Template tempate = VelocityUtil.getTempate("template/cloud/feignClient/client.java");
		VelocityContext ctx = new VelocityContext();
		ctx.put("packagePath", packagePath);
		ctx.put("project", firstUppder(project));
		FileWriter writer = new FileWriter(dest);
		tempate.merge(ctx, writer);
		writer.close();

	}
	
	public static void getWeb(Connection conn, String target, String packagePath, String tableName)
			throws Exception {

		String modelName = VelocityUtil.tableNameConvertModelName(tableName);
		File file = new File(target + "/web");
		if (!file.exists()) {
			file.mkdirs();
		}
		String primaryKey = TblUtil.getPrimaryKey(conn, tableName);
		String pkClass = "Object";
		if (StringUtils.isNotBlank(primaryKey)) {
			String type = TblUtil.getColunmType(conn, tableName, primaryKey);
			if (StringUtils.isNotBlank(type)) {
				pkClass = type.split("[.]")[type.split("[.]").length - 1];
				pkClass = resolveColunmType(pkClass);
			}
		}
		String dest=file.getAbsolutePath() + "/" + modelName + "Controller.java";
		Template tempate = VelocityUtil.getTempate("template/boot/web/Controller.java");
		VelocityContext ctx = new VelocityContext();
		ctx.put("packagePath", packagePath);
		ctx.put("modelName", modelName);
		ctx.put("lmodelName", firstLow(modelName));
		ctx.put("pkclass", pkClass);
		FileWriter writer = new FileWriter(dest);
		tempate.merge(ctx, writer);
		writer.close();

	}

	public static void getServiceImpl(Connection conn, String target, String packagePath, String tableName)
			throws Exception {

		String modelName = VelocityUtil.tableNameConvertModelName(tableName);
		File file = new File(target + "/service/impl/");
		if (!file.exists()) {
			file.mkdirs();
		}
		String primaryKey = TblUtil.getPrimaryKey(conn, tableName);
		String pkClass = "Object";
		if (StringUtils.isNotBlank(primaryKey)) {
			String type = TblUtil.getColunmType(conn, tableName, primaryKey);
			if (StringUtils.isNotBlank(type)) {
				pkClass = type.split("[.]")[type.split("[.]").length - 1];
				pkClass = resolveColunmType(pkClass);
			}
		}

		BufferedWriter bw = new BufferedWriter(
				new FileWriter(file.getAbsolutePath() + "/" + modelName + "ServiceImpl.java"));

		bw.write("package " + packagePath + ".service.impl" + ";");
		bw.newLine();
		bw.newLine();

		bw.write("import org.springframework.beans.factory.annotation.Autowired;");
		bw.newLine();
		bw.write("import org.springframework.stereotype.Service;");
		bw.newLine();
		bw.newLine();

		bw.write("import " + baseServiceImpl + ";");
		bw.newLine();

		bw.write("import " + packagePath + ".model." + modelName + ";");
		bw.newLine();

		bw.write("import " + packagePath + ".model." + "query." + modelName + "Example;");
		bw.newLine();

		bw.write("import " + packagePath + ".dao." + modelName + "Dao;");
		bw.newLine();

		bw.write("import " + packagePath + ".service." + modelName + "Service;");
		bw.newLine();

		bw.newLine();

		bw.write("@Service");
		bw.newLine();

		bw.write("public class " + modelName + "ServiceImpl extends "
				+ baseServiceImpl.split("[.]")[baseService.split("[.]").length - 1] + "<" + modelName + "," + modelName
				+ "Example," + pkClass + "> implements " + modelName + "Service {");
		bw.newLine();
		bw.newLine();

		bw.write("\t@Autowired");
		bw.newLine();

		bw.write("\tprivate " + modelName + "Dao " + firstLow(modelName) + "Dao;");
		bw.newLine();
		bw.newLine();

		bw.write("\t@Override");
		bw.newLine();

		bw.write("\tpublic void setDao() {");
		bw.newLine();

		bw.write("		this.baseDao = " + firstLow(modelName) + "Dao;");
		bw.newLine();

		bw.write("\t}");
		bw.newLine();
		bw.newLine();

		bw.write("}");
		bw.close();

	}

	public static String resolveColunmType(String colunm) {
		if ("nvarchar".equalsIgnoreCase(colunm) || "varchar".equalsIgnoreCase(colunm)
				|| "char".equalsIgnoreCase(colunm)) {
			return "String";
		}

		if ("int".equalsIgnoreCase(colunm)) {
			return "Integer";
		}

		if ("bigInt".equalsIgnoreCase(colunm)) {
			return "Long";
		}
		return "Object";
	}

	public static String firstLow(String str) {

		if (str == null) {
			return null;
		}
		if (str.equals("")) {
			return "";
		}

		if (str.length() == 1) {
			return str.toUpperCase();
		}

		String first = str.substring(0, 1).toLowerCase();
		String last = str.substring(1);

		return first + last;
	}

	public static String firstUppder(String str) {

		if (str == null) {
			return null;
		}
		if (str.equals("")) {
			return "";
		}

		if (str.length() == 1) {
			return str.toUpperCase();
		}

		String first = str.substring(0, 1).toUpperCase();
		String last = str.substring(1);

		return first + last;
	}
}
