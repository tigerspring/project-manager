package com.vcg.code.gennerator.mybatis;

import static org.mybatis.generator.internal.util.ClassloaderUtility.getCustomClassloader;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.XmlFileMergerJaxp;

/**
 * O
 * 
 * @author wuyu
 * @Date 2016-3-03
 *
 */
public class MyBatisGeneratorWrap extends MyBatisGenerator {

	static Logger logger = Logger.getLogger(MyBatisGenerator.class);

	private Connection conn;

	private String baseModel;

	private String baseExample;

	
	private boolean isGenerator=false;
	
	 
	
	public MyBatisGeneratorWrap(Configuration configuration, ShellCallback shellCallback, List<String> warnings)
			throws InvalidConfigurationException {
		super(configuration, shellCallback, warnings);
	}

	String baseDaoFullName;

	public void setBaseDao(String baseDaoFullName) {
		this.baseDaoFullName = baseDaoFullName;
	}

	
	
	@SuppressWarnings("unchecked")
	@Override
	public void generate(ProgressCallback callback, Set<String> contextIds, Set<String> fullyQualifiedTableNames)
			throws SQLException, IOException, InterruptedException {
		if (callback == null) {
			callback = new NullProgressCallback();
		}

		List<GeneratedJavaFile> generatedJavaFiles = null;
		List<GeneratedXmlFile> generatedXmlFiles = null;
		Configuration configuration = null;
		Set<String> projects = null;
		ShellCallback shellCallback = null;
		// 反射拿到私有变量

		try {

			Field generatedJavaFilesField = MyBatisGenerator.class.getDeclaredField("generatedJavaFiles");
			Field generatedXmlFilesField = MyBatisGenerator.class.getDeclaredField("generatedXmlFiles");
			Field configurationField = MyBatisGenerator.class.getDeclaredField("configuration");
			Field projectsField = MyBatisGenerator.class.getDeclaredField("projects");
			Field shellCallbackField = MyBatisGenerator.class.getDeclaredField("shellCallback");

			generatedJavaFilesField.setAccessible(true);
			generatedXmlFilesField.setAccessible(true);
			configurationField.setAccessible(true);
			projectsField.setAccessible(true);
			shellCallbackField.setAccessible(true);
			generatedJavaFiles = (List<GeneratedJavaFile>) generatedJavaFilesField.get(this);
			generatedXmlFiles = (List<GeneratedXmlFile>) generatedXmlFilesField.get(this);
			configuration = (Configuration) configurationField.get(this);
			projects = (Set<String>) projectsField.get(this);
			shellCallback = (ShellCallback) shellCallbackField.get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		generatedJavaFiles.clear();
		generatedXmlFiles.clear();

		// calculate the contexts to run
		List<Context> contextsToRun;
		if (contextIds == null || contextIds.size() == 0) {
			contextsToRun = configuration.getContexts();
		} else {
			contextsToRun = new ArrayList<Context>();
			for (Context context : configuration.getContexts()) {
				if (contextIds.contains(context.getId())) {
					contextsToRun.add(context);
				}
			}
		}

		// setup custom classloader if required
		if (configuration.getClassPathEntries().size() > 0) {
			ClassLoader classLoader = getCustomClassloader(configuration.getClassPathEntries());
			ObjectFactory.addExternalClassLoader(classLoader);
		}

		// now run the introspections...
		int totalSteps = 0;
		for (Context context : contextsToRun) {
			totalSteps += context.getIntrospectionSteps();
		}
		callback.introspectionStarted(totalSteps);

		for (Context context : contextsToRun) {
			context.introspectTables(callback, new ArrayList<String>(), null);
		}

		// now run the generates
		totalSteps = 0;
		for (Context context : contextsToRun) {
			totalSteps += context.getGenerationSteps();
		}
		callback.generationStarted(totalSteps);

		for (Context context : contextsToRun) {
			context.generateFiles(callback, generatedJavaFiles, generatedXmlFiles, new ArrayList<String>());
		}
		List<String> warnings = new ArrayList<String>();
		// now save the files
		callback.saveStarted(generatedXmlFiles.size() + generatedJavaFiles.size());
		List<String> columns = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		
		 
		for (GeneratedXmlFile gxf : generatedXmlFiles) {
			String fileName = gxf.getFileName().replace("Mapper", "Dao");
			logger.info("生成 xml:" + fileName);
			try {
				Field FileNameFiled = GeneratedXmlFile.class.getDeclaredField("fileName");
				Field documentField = GeneratedXmlFile.class.getDeclaredField("document");
				FileNameFiled.setAccessible(true);
				documentField.setAccessible(true);
				FileNameFiled.set(gxf, fileName);
				Document document = (Document) documentField.get(gxf);
				XmlElement rootElement = document.getRootElement();
				List<Attribute> rootAttribute = new ArrayList<Attribute>();
				String daoFulleName = "";
				for (Attribute attribute : rootElement.getAttributes()) {
					daoFulleName = attribute.getValue().replace("Mapper", "Dao");
					Attribute a = new Attribute(attribute.getName(), daoFulleName);
					rootAttribute.add(a);
				}
				List<Element> rootElements = rootElement.getElements();
				XmlElement baseCloumnElement = new XmlElement("sql");
				baseCloumnElement.addAttribute(new Attribute("id", "Base_Column_List_Dy"));
				rootElements.add(baseCloumnElement);
				String exampleFullName = daoFulleName.replace("Dao", "Example").replace(".dao.", ".model.query.");
				String ab="";
				for (Element element : rootElements) {
					XmlElement e = (XmlElement) element;
					List<Attribute> attributes = e.getAttributes();
					for (int i = attributes.size() - 1; i >= 0; i--) {
						Attribute attribute = attributes.get(i);
						if (attribute.getValue()
								.equals(daoFulleName.replace("Dao", "Example").replace(".dao.", ".model."))) {
							Attribute a = new Attribute(attributes.get(i).getName(), exampleFullName);
							attributes.remove(attributes.get(i));
							attributes.add(a);
						}

						if (attribute.getValue().equals("Base_Column_List")) {
							XmlElement ifNullElement = new XmlElement("if");

							XmlElement includeBaseColunms=new XmlElement("include");
							includeBaseColunms.addAttribute(new Attribute("refid", "Base_Column_List"));
							ifNullElement.addElement(includeBaseColunms);
							Attribute testNullAttr = new Attribute("test", "fields==null");
							ifNullElement.addAttribute(testNullAttr);

							XmlElement ifNotElement = new XmlElement("if");
							Attribute testNotNull = new Attribute("test", "fields!=null");
							ifNotElement.addAttribute(testNotNull);

							XmlElement forEachElement = new XmlElement("foreach");
							Attribute collectionAttr = new Attribute("collection", "fields");
							Attribute itemAttr = new Attribute("item", "field");
							Attribute separatorAttr = new Attribute("separator", ",");
							forEachElement.addAttribute(collectionAttr);
							forEachElement.addAttribute(itemAttr);
							forEachElement.addAttribute(separatorAttr);
							forEachElement.addElement(new TextElement("${field}"));
							ifNotElement.addElement(forEachElement);

							baseCloumnElement.addElement(ifNullElement);
							baseCloumnElement.addElement(ifNotElement);
						}

						if (attribute.getValue().equals("selectByExample")) {
							List<Element> elements = e.getElements();
							for (Element element2 : elements) {
								if (element2 instanceof XmlElement) {
									XmlElement e2 = (XmlElement) element2;
									List<Attribute> attributes2 = e2.getAttributes();
									for (int j = attributes2.size() - 1; j >= 0; j--) {
										if (attributes2.get(j).getValue().equals("Base_Column_List")) {
											attributes2.remove(j);
											e2.addAttribute(new Attribute("refid", "Base_Column_List_Dy"));
										}
									}
								}

							}
						}
						
						//自动生成主键
						if(isGenerator){
							if(attribute.getValue().equals("insert")){
								for(Element ele:e.getElements()){
									if(ele instanceof TextElement){
										ab=ele.getFormattedContent(1);
										if(ab.indexOf("(")!=-1){
											int lastIndex=ab.indexOf(",")==-1?ab.indexOf(")"):ab.indexOf(",");
											
											ab=ab.substring(ab.indexOf("(")+1, lastIndex);
											ab=convertColumn(ab);
											break;
										}
									}
									
								}
								
								if(StringUtils.isNotBlank(ab)){
									e.addAttribute(new Attribute("useGeneratedKeys", "true"));
									e.addAttribute(new Attribute("keyProperty", ab));
								}
							}
							
							if(attribute.getValue().equals("insertSelective")){
								
								
								if(StringUtils.isNotBlank(ab)){
									e.addAttribute(new Attribute("useGeneratedKeys", "true"));
									e.addAttribute(new Attribute("keyProperty", ab));
								}
							}
							
						}
						
						
						
					}

					// List<Attribute> sqlAttr = e.getAttributes();
					// for (Attribute attribute : sqlAttr) {
					// if (attribute.getValue().equals("BaseResultMap")) {
					// for (Element element2 : e.getElements()) {
					// if (element2 instanceof XmlElement) {
					// XmlElement x2 = (XmlElement) element2;
					// List<Attribute> attributes2 = x2.getAttributes();
					// for (Attribute attribute2 : attributes2) {
					// String name = attribute2.getName();
					// String value = attribute2.getValue();
					// if (name.equals("column")) {
					// columns.add(value);
					// }
					// if (name.equals("property")) {
					// fields.add(value);
					// }
					// }
					// }
					//
					// }
					// }
					// }
				}
				Field attributesField = XmlElement.class.getDeclaredField("attributes");
				attributesField.setAccessible(true);
				attributesField.set(rootElement, rootAttribute);
				document.setRootElement(rootElement);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			projects.add(gxf.getTargetProject());

			File targetFile;
			String source;
			try {
				File directory = shellCallback.getDirectory(gxf.getTargetProject(), gxf.getTargetPackage());
				targetFile = new File(directory, gxf.getFileName());
				if (targetFile.exists()) {
					if (gxf.isMergeable()) {
						source = XmlFileMergerJaxp.getMergedSource(gxf, targetFile);
					} else if (shellCallback.isOverwriteEnabled()) {
						source = gxf.getFormattedContent();
						warnings.add(getString("Warning.11", //$NON-NLS-1$
								targetFile.getAbsolutePath()));
					} else {
						source = gxf.getFormattedContent();
						targetFile = getUniqueFileName(directory, gxf.getFileName());
						warnings.add(getString("Warning.2", targetFile.getAbsolutePath())); //$NON-NLS-1$
					}
				} else {
					source = gxf.getFormattedContent();
				}
			} catch (ShellException e) {
				warnings.add(e.getMessage());
				continue;
			}

			callback.checkCancel();
			callback.startTask(getString("Progress.15", targetFile.getName())); //$NON-NLS-1$
			writeFile(targetFile, source, "UTF-8"); //$NON-NLS-1$
		}

		for (

		GeneratedJavaFile gjf : generatedJavaFiles)

		{
			logger.info("生成java:" + gjf.getFileName());
			projects.add(gjf.getTargetProject());

			File targetFile;
			String source;
			try {
				File directory = shellCallback.getDirectory(gjf.getTargetProject(), gjf.getTargetPackage());
				targetFile = new File(directory, gjf.getFileName());
				String packageFullName = gjf.getTargetPackage();
				String DaoName = gjf.getFileName().replace("Mapper", "Dao").replace(".java", "");
				String modelName = DaoName.replace("Dao", "").replace(".java", "");
				if (gjf.getFileName().contains("Mapper")) {
					targetFile = new File(directory, gjf.getFileName().replace("Mapper", "Dao"));
				}
				if (gjf.getFileName().contains("Example")) {
					if (!(new File(directory + "/query/")).exists()) {
						(new File(directory + "/query/")).mkdirs();
					}
					targetFile = new File(directory + "/query/", gjf.getFileName());
					// targetFile = new File(directory, gjf.getFileName());

				}
				if (targetFile.exists()) {
					if (shellCallback.isMergeSupported()) {
						source = shellCallback.mergeJavaFile(gjf.getFormattedContent(), targetFile.getAbsolutePath(),
								MergeConstants.OLD_ELEMENT_TAGS, gjf.getFileEncoding());
					} else if (shellCallback.isOverwriteEnabled()) {
						source = gjf.getFormattedContent();
						warnings.add(getString("Warning.11", //$NON-NLS-1$
								targetFile.getAbsolutePath()));
					} else {
						source = gjf.getFormattedContent();
						targetFile = getUniqueFileName(directory, gjf.getFileName());
						warnings.add(getString("Warning.2", targetFile.getAbsolutePath())); //$NON-NLS-1$
					}
				} else {
					source = gjf.getFormattedContent();
				}

				Random r = new Random();
				StringBuffer sb = new StringBuffer();

				if (gjf.getFileName().contains("Mapper")) {
					if (baseDaoFullName != null) {
						String line = "package " + packageFullName + ";" + "\r\n\r\n" + "import " + baseDaoFullName
								+ ";" + "\r\n" + "import " + packageFullName.replace("dao", "model") + "." + modelName
								+ ";" + "\r\n" + "import " + packageFullName.replace("dao", "model") + ".query."
								+ modelName + "Example;" + "\r\n\r\n" + "public interface " + DaoName
								+ " extends BaseDao<" + modelName + ", " + modelName + "Example> {" + "\r\n\r\n" + "}";
						sb.append(line);

					} else {
						String[] lines = source.split("\r\n");
						if (lines.length == 1) {
							lines = source.split("\r");
							if (lines.length == 1) {
								lines = source.split("\n");
							}
						}
						for (int i = 0; i < lines.length; i++) {
							String line = lines[i];
							if (line.contains("Mapper")) {
								line = line.replace("Mapper", "Dao");
							}

							sb.append(line + "\r\n");

						}
					}
				}

				if (!gjf.getFileName().contains("Example") && !gjf.getFileName().contains("Mapper")) {
					String[] lines = source.split("\r\n");
					if (lines.length == 1) {
						lines = source.split("\r");
						if (lines.length == 1) {
							lines = source.split("\n");
						}
					}
					for (int i = 0; i < lines.length; i++) {
						long nextLong = Math.abs(r.nextLong());
						String line = lines[i];
						if (i == 2) {
							line = "import io.swagger.annotations.ApiModelProperty;\r\n\r\nimport " + baseModel + ";\r\n\r\n" + line + "\r\n";
							// +"import java.util.Map;\r\n"
							// +"import java.util.LinkedHashMap;\r\n";
						}

						if (line.contains("public class") && line.contains("{")) {
							line = line.replace("{", "extends BaseModel<"+modelName+"> {")
									+ "\r\n    private static final long serialVersionUID = " + nextLong + "L;\r\n";
						}

						if (line.contains("public void set")) {
							line = line.replace("public void set",
									"public " + gjf.getFileName().replace(".java", "") + " set");
							i++;
							line = line + "\r\n" + lines[i] + "\r\n\t\treturn this;";
						}

						// if (line.equals("}")) {
						// line = "\r\n public Map<String, Object> toMap(){\r\n"
						// + " Map<String, Object> map=new
						// LinkedHashMap<String,Object>();\r\n";
						// for (String field : fields) {
						// line = line + " map.put(\"" + field + "\",this." +
						// field
						// + ");\r\n";
						// }
						// line=line+ " return map;\r\n";
						// line = line + " }\r\n";
						//
						// sb.append(line+"\r\n}");
						// continue;
						// }

						sb.append(line + "\r\n");
					}
				}

				if (gjf.getFileName().contains("Example")) {

					String[] lines = source.split("\r\n");
					if (lines.length == 1) {
						lines = source.split("\r");
						if (lines.length == 1) {
							lines = source.split("\n");
						}
					}
					for (int i = 0; i < lines.length; i++) {
						long nextLong = Math.abs(r.nextLong());
						String line = lines[i];
						if (i == 0) {
							line = line.replace(gjf.getTargetPackage(), gjf.getTargetPackage() + ".query");
						}

						if (i == 2) {
							line = "import java.io.Serializable;\r\n" + "import " + baseExample + ";\r\n" + line;
						}

						if (line.contains("class") && line.contains("{")) {

							if (line.contains("Example")) {
								line = line.trim().replace("{", "extends BaseExample {")
										+ "\r\n\r\n\tprivate static final long serialVersionUID = " + nextLong
										+ "L;\r\n";
							}

							if (line.contains("GeneratedCriteria") || line.contains("Criteria")
									|| line.contains("Criterion"))
								line = line.trim().replace("{", "implements Serializable {")
										+ "\r\n\r\n\tprivate static final long serialVersionUID = " + nextLong
										+ "L;\r\n";
						}
						sb.append(line + "\r\n");
					}

				}

				callback.checkCancel();
				callback.startTask(getString("Progress.15", targetFile.getName())); //$NON-NLS-1$

				if (!sb.toString().equals("")) {
					writeFile(targetFile, sb.toString(), gjf.getFileEncoding());
				} else {
					writeFile(targetFile, source, gjf.getFileEncoding());
				}
			} catch (

			ShellException e)

			{
				e.printStackTrace();
				warnings.add(e.getMessage());
			}
		}

		for (

		String project : projects)

		{
			shellCallback.refreshProject(project);
		}

		callback.done();

	}

	private File getUniqueFileName(File directory, String fileName) {
		File answer = null;

		// try up to 1000 times to generate a unique file name
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < 1000; i++) {
			sb.setLength(0);
			sb.append(fileName);
			sb.append('.');
			sb.append(i);

			File testFile = new File(directory, sb.toString());
			if (!testFile.exists()) {
				answer = testFile;
				break;
			}
		}

		if (answer == null) {
			throw new RuntimeException(getString("RuntimeError.3", directory.getAbsolutePath())); //$NON-NLS-1$
		}

		return answer;
	}

	private void writeFile(File file, String content, String fileEncoding) throws IOException {
		FileOutputStream fos = new FileOutputStream(file, false);
		OutputStreamWriter osw;
		if (fileEncoding == null) {
			osw = new OutputStreamWriter(fos);
		} else {
			osw = new OutputStreamWriter(fos, fileEncoding);
		}

		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(content);
		bw.close();
	}
	
	
	public boolean isGenerator() {
		return isGenerator;
	}



	public void setGenerator(boolean isGenerator) {
		this.isGenerator = isGenerator;
	}



	private String convertColumn(String columnName){
		columnName = columnName.toLowerCase();
		String[] names = columnName.split("_");
		if (names.length == 1) {
			return names[0];
		}
		StringBuilder sb = new StringBuilder(names[0]);
		for (int i = 1; i < names.length; i++) {
			String first = names[i].substring(0, 1);
			String a= first.toUpperCase() + names[i].substring(1);
			sb.append(a);
		}
		return sb.toString();
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getBaseModel() {
		return baseModel;
	}

	public void setBaseModel(String baseModel) {
		this.baseModel = baseModel;
	}

	public String getBaseExample() {
		return baseExample;
	}

	public void setBaseExample(String baseExample) {
		this.baseExample = baseExample;
	}
}