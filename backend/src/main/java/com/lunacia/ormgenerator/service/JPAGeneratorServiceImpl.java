package com.lunacia.ormgenerator.service;


import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class JPAGeneratorServiceImpl implements JPAGeneratorService {

	@Override
	public String entityGenerator (String entityName, String tableName, String packageName, Map<String, String> field) throws IOException {
		File temp = fileHelper(packageName, entityName, "entity");
		BufferedWriter output = new BufferedWriter(new FileWriter(temp));
		StringBuilder sb = new StringBuilder();
		sb.append("\n");

		//文件头部分
		output.write("package "+packageName+".entity;\n\n");

		//import部分
		output.write("import javax.persistence.*;\n\n");


		output.write("@Entity\n@Table(name=\""+tableName+"\")\n");
		output.write("public class "+firstLetterUppercase(entityName)+" {\n\n");

		for(Map.Entry<String, String> e : field.entrySet()) {
			String column = e.getKey().substring(0, e.getKey().indexOf(';'));
			String property = e.getValue().substring(0, e.getValue().indexOf(';'));
			String type = e.getValue().substring(e.getValue().indexOf(';')+1);
			//添加实体
			if (property.equals("id")) {
				output.write("\t@Id\n");
				output.write("\t@GeneratedValue(strategy=GenerationType.IDENTITY)\n");
			}
			output.write("\t@Column(name=\""+column+"\")\n");
			output.write("\tprivate "+firstLetterUppercase(type)+" "+property+";\n");
			//增加getter和setter
			sb.append("\tpublic "+firstLetterUppercase(type)+" get"+firstLetterUppercase(property)+" () {\n\t\treturn "+property+";\n\t}\n");
			sb.append("\tpublic void set"+firstLetterUppercase(property)+"("+firstLetterUppercase(type)+" "+property+") {\n\t\tthis."+property+"="+property+";\n\t}\n");
		}

		output.write(sb.toString());
		output.write("\n}");
		output.close();

		return temp.getAbsolutePath();
	}

	@Override
	public String entityGenerator (String entityName, String tableName, String packageName, Map<String, String> field, Map<String, String> refer, Map<String, String> type) throws IOException {
		File temp = fileHelper(packageName, entityName, "entity");
		BufferedWriter output = new BufferedWriter(new FileWriter(temp));
		StringBuilder sb = new StringBuilder();
		sb.append("\n");

		//包名
		output.write("package "+packageName+".entity;\n\n");
		//import 部分
		output.write("import javax.persistence.*;\n\n");
		for (Map.Entry<String, String> e : refer.entrySet()) {
			String thatEntity = e.getValue().substring(0, e.getValue().indexOf('.'));
			output.write("import "+packageName+".entity."+firstLetterUppercase(thatEntity)+";\n");
		}
		output.write("\nimport java.util.*;\n\n");

		//文件头部分
		output.write("@Entity\n@Table(name=\""+tableName+"\")\n");
		output.write("public class "+firstLetterUppercase(entityName)+" {\n\n");

		for(Map.Entry<String, String> e : field.entrySet()) {
			String column = null;
			if (!e.getKey().equals("NULL")){
				column = e.getKey().substring(0, e.getKey().indexOf(';'));
			}
			String property = e.getValue().substring(0, e.getValue().indexOf(';'));
			String propertyType = e.getValue().substring(e.getValue().indexOf(';')+1);
			//添加实体
			if (property.equals("id")) {
				output.write("\t@Id\n");
				output.write("\t@GeneratedValue(strategy=GenerationType.IDENTITY)\n");
			}
			//处理级联查找
			if (refer.get(firstLetterUppercase(entityName)+"."+property) != null || refer.get(firstLetterLowercase(entityName)+"."+property) != null) {
				String that = refer.get(firstLetterUppercase(entityName)+"."+property) != null ?
						refer.get(firstLetterUppercase(entityName)+"."+property) : refer.get(firstLetterLowercase(entityName)+"."+property);
				String cType = type.get(firstLetterUppercase(entityName)+"."+property) != null?
						type.get(firstLetterUppercase(entityName)+"."+property):type.get(firstLetterLowercase(entityName)+"."+property);
				String thatEntity = that.substring(0, that.indexOf('.'));
				String thatProperty = that.substring(that.indexOf('.')+1);
				switch (cType) {
					case "1:n": {
						output.write("\t@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)\n");
						if (thatProperty.equals("id")) {
							output.write("\t@JoinColumn(name=\""+thatProperty+"\", insertable = false, updatable = false)\n");
						} else {
							output.write("\t@JoinColumn(name=\""+thatProperty+"\")\n");
						}
						output.write("\tprivate List<"+firstLetterUppercase(thatEntity)+"> "+property+";\n");
						break;
					}
					case "n:1": {
						output.write("\t@ManyToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)\n");
						if (thatProperty.equals("id")) {
							output.write("\t@JoinColumn(name=\""+thatProperty+"\", insertable = false, updatable = false)\n");
						} else {
							output.write("\t@JoinColumn(name=\""+thatProperty+"\")\n");
						}
//						output.write("\t@JoinColumn(name=\""+column+"\")\n");
						output.write("\tprivate "+firstLetterUppercase(thatEntity)+" "+property+";\n");
						break;
					}
					case "1:1": {
						output.write("\t@OneToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)\n");
//						if (thatProperty.equals("id")) {
//							output.write("\t@JoinColumn(name=\""+thatProperty+"\", insertable = false, updatable = false)\n");
//						} else {
//							output.write("\t@JoinColumn(name=\""+thatProperty+"\")\n");
//						}
						output.write("\t@JoinColumn(name=\""+column+"\")\n");
						output.write("\tprivate "+firstLetterUppercase(thatEntity)+" "+property+";\n");
						break;
					}
				}
			} else {
				output.write("\t@Column(name=\""+column+"\")\n");
				output.write("\tprivate "+firstLetterUppercase(propertyType)+" "+property+";\n");
			}

			//添加getter和setter
			sb.append("\tpublic "+firstLetterUppercase(propertyType)+" get"+firstLetterUppercase(property)+" () {\n\t\treturn "+property+";\n\t}\n");
			sb.append("\tpublic void set"+firstLetterUppercase(property)+"("+firstLetterUppercase(propertyType)+" "+property+") {\n\t\tthis."+property+"="+property+";\n\t}\n");
		}
		output.write(sb.toString());
		output.write("\n}");
		output.close();


		return temp.getAbsolutePath();
	}

	@Override
	public String repositoryGenerator (String entityName, String tableName, String packageName, Map<String, String> field) throws IOException {
		File temp = fileHelper(packageName, entityName, "repository");
		BufferedWriter output = new BufferedWriter(new FileWriter(temp));

		//包名
		output.write("package "+packageName+".repository;\n\n");

		//import部分
		output.write("import org.springframework.data.jpa.repository.JpaRepository;\n");
		output.write("\nimport "+packageName+".entity."+firstLetterUppercase(entityName)+";\n\n");

		String idType = null;
		for (Map.Entry<String, String> e : field.entrySet()){
			String property = e.getValue().substring(0, e.getValue().indexOf(';'));
			String type = e.getValue().substring(e.getValue().indexOf(';')+1);
			if (property.equals("id")) {
				idType = type;
				break;
			}
		}

		System.out.println("public interface "+firstLetterUppercase(entityName)+"Repository extends JpaRepository<" +
				firstLetterUppercase(entityName)+","+firstLetterUppercase(idType)+"> {\n\n}");
		//文件头部分
		output.write("public interface "+firstLetterUppercase(entityName)+"Repository extends JpaRepository<" +
				firstLetterUppercase(entityName)+","+firstLetterUppercase(idType)+"> {\n\n}");

		output.close();
		return temp.getAbsolutePath();
	}

	@Override
	public String repositoryGenerator (String entityName, String tableName, String packageName, Map<String, String> field, Map<String, String> refer, Map<String, String> type) throws IOException {
		File temp = fileHelper(packageName, entityName, "repository");
		BufferedWriter output = new BufferedWriter(new FileWriter(temp));

		//包名
		output.write("package "+packageName+".repository;\n\n");

		//import部分
		output.write("import org.springframework.data.jpa.repository.JpaRepository;\n");
		output.write("\nimport "+packageName+".entity."+firstLetterUppercase(entityName)+";\n\n");

		String idType = null;
		for (Map.Entry<String, String> e : field.entrySet()){
			String property = e.getValue().substring(0, e.getValue().indexOf(';'));
			String propertyType = e.getValue().substring(e.getValue().indexOf(';')+1);
			if (property.equals("id")) {
				idType = propertyType;
				break;
			}
		}


		//文件头部分
		output.write("public interface "+firstLetterUppercase(entityName)+"Repository extends JpaRepository<" +
				firstLetterUppercase(entityName)+","+firstLetterUppercase(idType)+"> {\n\n}");

//		//级联方法
//		for (Map.Entry<String, String> e : type.entrySet()){
//			String cascadeKey = e.getKey();
//			String cascadeType = e.getValue();
//		}
		output.close();
		return temp.getAbsolutePath();
	}

	private File fileHelper(String packageName, String entityName, String type) throws IOException {
		String path = ResourceUtils.getURL("classpath:").getPath();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		File dir = new File(path + sdf.format(System.currentTimeMillis())+"/"+packageName+"/"+type);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File temp = null;
		if (type.equals("repository")) {
			temp = new File(dir.getAbsolutePath()+"/"
					+firstLetterUppercase(entityName)+firstLetterUppercase(type)+".java");
		} else {
			temp = new File(dir.getAbsolutePath() + "/"
					+ firstLetterUppercase(entityName) + ".java");
		}
		temp.createNewFile();
		return temp;
	}

	private String firstLetterUppercase(String s) {
		return s.substring(0,1).toUpperCase()+s.substring(1);
	}
	private String firstLetterLowercase(String s) {
		return s.substring(0,1).toLowerCase()+s.substring(1);
	}
}
