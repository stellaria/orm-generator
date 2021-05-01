package com.lunacia.ormgenerator.service;


import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GeneratorServiceImpl implements GeneratorService {

	@Override
	public String basePOJOGenerator(String entityName, String tableName, String packageName, List<String> field) throws IOException {
		Resource resource = new ClassPathResource("/templates/template.java");
		File f = resource.getFile();
		BufferedReader br = new BufferedReader(new FileReader(f));
		File temp = fileHelper(entityName, "pojo");
		BufferedWriter output = new BufferedWriter(new FileWriter(temp));

		String className = br.readLine();
		String head = className.replace("#{Template}", firstLetterUppercase(entityName));
		output.write("package "+packageName+";\n\n");
		output.write(head+"\n\n");

		//添加域
		for (int i = 0; i < field.size(); i++) {
			String name = field.get(i).substring(0, field.get(i).indexOf(';'));
			String type = field.get(i).substring(field.get(i).indexOf(';')+1);
			output.write("\tprivate "+ firstLetterUppercase(type) +" "+ name +";\n");
		}
		output.write("\n");
		//增加getter和setter
		for (int i = 0; i < field.size(); i++) {
			String name = field.get(i).substring(0, field.get(i).indexOf(';'));
			String type = field.get(i).substring(field.get(i).indexOf(';')+1);
			output.write("\tpublic "+ firstLetterUppercase(type) +" get"+firstLetterUppercase(name)+"() {return this."+name+";}\n");

			output.write("\tpublic void set"+firstLetterUppercase(name)+"("+firstLetterUppercase(type)+" "+name+") {this."+name+"="+name+";}\n");

		}
		output.write("\n}");
		output.close();
		br.close();

		return temp.getAbsolutePath();
	}

	@Override
	public String baseMapperGenerator(String entityName, String tableName, String packageName, List<String> field) throws IOException {
		File temp = fileHelper(entityName, "mapper");
		BufferedWriter output = new BufferedWriter(new FileWriter(temp));
		output.write("package "+packageName+";\n\n");
		output.write("@Mapper\n");
		output.write("public interface UserMapper {\n\n");
		//增
		StringBuilder sb = new StringBuilder();
		sb.append("\t@Insert(\"INSERT INTO "+tableName+"(");
		for (int i = 0; i < field.size(); i++) {
			String s = field.get(i).substring(0, field.get(i).indexOf(';'));
			if (s.contains("id")) continue;
			sb.append(s);
			if (i < field.size()-1) sb.append(", ");
		}
		sb.append(") VALUES(");
		for (int i = 0; i < field.size(); i++) {
			String s = field.get(i).substring(0, field.get(i).indexOf(';'));
			if (s.equals("t_id")) continue;
			sb.append("#{"+s+"}");
			if (i < field.size()-1) sb.append(", ");
		}
		sb.append(")\")\n");
		sb.append("\t@Options(useGeneratedKeys = true, keyProperty = \""+field.get(0).substring(0, field.get(0).indexOf(';'))+"\")\n");
		output.write(sb.toString());
		output.write("\tvoid insert("+firstLetterUppercase(entityName)
				+" "+firstLetterLowercase(entityName)
				+");\n\n");

		//删
		output.write("\t@Delete(\"DELETE FROM "+tableName+" WHERE "+field.get(0).substring(0, field.get(0).indexOf(';'))+"=#{"+field.get(0).substring(0, field.get(0).indexOf(';'))+"}\")\n");
		output.write("\tvoid delete("+firstLetterUppercase(entityName)
				+" "+firstLetterLowercase(entityName)
				+");\n\n");
		//改
		StringBuilder sb1 = new StringBuilder();
		sb1.append("\t@Update(\"UPDATE "+tableName+" SET ");
		for (int i = 0; i < field.size(); i++) {
			String s = field.get(i).substring(0, field.get(i).indexOf(';'));
			if (s.equals("t_id")) continue;
			sb1.append(s+"=#{"+s+"}");
			if (i < field.size() - 1) sb1.append(", ");
		}
		sb1.append(" WHERE t_id=#{t_id})\")\n");
		output.write(sb1.toString());
		output.write("\tvoid update("+firstLetterUppercase(entityName)
				+" "+firstLetterLowercase(entityName)
				+");\n\n");

		//查
		output.write("\t@Select(\"SELECT * FROM "+tableName+" WHERE "+field.get(0).substring(0, field.get(0).indexOf(';'))+"=#{"+field.get(0).substring(0, field.get(0).indexOf(';'))+"}\")\n");
		output.write("\t"+firstLetterUppercase(entityName)+
				" find"+firstLetterUppercase(entityName)+"ById"+"("+firstLetterUppercase(entityName)
				+" "+firstLetterLowercase(entityName)
				+");\n");

		output.write("\t@Select(\"SELECT * FROM "+tableName+")\n");
		output.write("\tList<"+firstLetterUppercase(entityName)+"> find"+firstLetterUppercase(entityName)+"s();\n");
		output.write("}");

		output.close();

		return temp.getAbsolutePath();
	}

	@Override
	public String cascadePOJOGenerator (String entityName, String tableName, String packageName, Map<String, String> field, Map<String, String> refer) throws IOException {
		File temp = fileHelper(entityName, "pojo");
		BufferedWriter output = new BufferedWriter(new FileWriter(temp));
		output.write("package "+packageName+";\n\n");
		//导入部分
		for (Map.Entry<String, String> e: refer.entrySet()) {
			String otherEntity = e.getValue().substring(0, e.getValue().indexOf('.'));
			output.write("import "+packageName+"."+firstLetterUppercase(otherEntity)+";\n");

		}
		output.write("import java.util.*;\n");

		output.write("\npublic class "+firstLetterUppercase(entityName)+" {\n\n");

		//生成域
		for (Map.Entry<String, String> e:field.entrySet()) {
			String name = e.getKey();
			String type = e.getValue();
			//private Type name;
			output.write("\tprivate "+firstLetterUppercase(type)+" "+name+";\n");
		}
		output.write("\n");
		//生成getter和setter
		for (Map.Entry<String, String> e:field.entrySet()) {
			String type = e.getValue();
			String name = e.getKey();
			//public Type getName() {return this.name;}
			output.write("\tpublic "+firstLetterUppercase(type)+" get"+firstLetterUppercase(name)+"() {return this."+name+";}\n");
			//public void setName(Type name) {this.name=name);
			output.write("\tpublic void set"+firstLetterUppercase(name)+"("+ firstLetterUppercase(type)+ " "+name+") {this."+name+"="+name+";}\n");
		}

		output.write("}");
		output.close();

		return temp.getAbsolutePath();
	}


	/**
	 *
	 * @param entityName
	 * @param tableName
	 * @param packageName
	 * @param field key为数据库列名和数据库类型，用;分割，value为实体类属性名和属性类型，用;分割
	 * @param refer 关联column key,value均为为实体类.实体属性
	 * @param type 对每个关联实体指明1：n还是n：1，没有多对多,key为实体名.属性名 value为1:n或n:1
	 * @return
	 * @throws IOException
	 */
	@Override
	public String cascadeMapperGenerator (String entityName, String tableName, String packageName, Map<String, String> field,
	                                      Map<String, String> refer, Map<String, String> type) throws IOException {
		File temp = fileHelper(entityName, "mapper");
		BufferedWriter output = new BufferedWriter(new FileWriter(temp));

		//文件头
		output.write("package "+packageName +";\n\n");
		//import部分
		output.write("import "+packageName.replace(".mapper", ".pojo.")+firstLetterUppercase(entityName)+";\n");
		output.write("import org.apache.ibatis.annotations.*;\n\n");
		output.write("import java.util.*;\n\n");

		//类名
		output.write("@Mapper\npublic interface "+firstLetterUppercase(entityName)+"Mapper {\n\n");



		String tableId = "id";
		String tableIdType = "int";
		Map<String, String> linkMap = new LinkedHashMap<>(); //获得key为属性的map

		//增
		StringBuilder sbf = new StringBuilder(); //前半段
		StringBuilder sbb = new StringBuilder(); //后半段
		sbf.append("\t@Insert(\"INSERT INTO "+tableName+"(");
		sbb.append("VALUES(");
		for (Map.Entry<String, String > e : field.entrySet()) {
			if (e.getKey().equals("NULL")) {continue;}
			String column = e.getKey().substring(0, e.getKey().indexOf(';'));
			String property = e.getValue().substring(0,e.getValue().indexOf(';'));
			linkMap.put(property, e.getKey());
			if (property.equals("id")) { //跳过id 并记录id的列名
				tableId = column;
				tableIdType = e.getValue().substring(e.getValue().indexOf(';')+1);
				continue;
			}
			if (refer.get(entityName+"."+property) == null) {
				sbf.append(column+",");
				sbb.append("#{"+property+"},");
			} else {
				sbf.append(column+",");
				sbb.append("#{"+refer.get(entityName+"."+property)+"},");
			}
//			if (count < size) {sbf.append(", "); sbb.append(", ");}
		}
//		for (String s : field) {
//			if (s.equals(field.get(0))) {count++;continue;}
//			count++;
//			if (refer.get(entityName+"."+s) == null) {
//				sbf.append(s);
//				sbb.append("#{"+s+"}");
//			} else {
//				sbf.append(s); count++;
//				sbb.append("#{"+refer.get(s)+"}");
//			}
//
//			if (count < size - 1) {sbf.append(", ");sbb.append(", ");}
//		}
		sbf.deleteCharAt(sbf.toString().lastIndexOf(','));
		sbb.deleteCharAt(sbb.toString().lastIndexOf(','));
		sbf.append(") ");
		sbb.append(")\")");
		output.write(sbf.toString()+sbb.toString()+"\n");
		output.write("\tvoid insert("+firstLetterUppercase(entityName)+" " +entityName+");\n\n");

		//删
		output.write("\t@Delete(\"DELETE FROM "+tableName+" WHERE "+tableId+"=#{id}\")\n");
		output.write("\tvoid delete("+firstLetterUppercase(entityName)+" "+firstLetterLowercase(entityName)+");\n\n");

		//改
		sbf = new StringBuilder(); //初始化
		output.write("\t@Update(\"UPDATE "+tableName+" SET ");
		for(Map.Entry<String, String> e : field.entrySet()) {
			if (e.getKey().equals("NULL")) {continue;} //跳过一对多的属性
			String column = e.getKey().substring(0, e.getKey().indexOf(';'));
			String property = e.getValue().substring(0, e.getValue().indexOf(';'));
			if (property.equals("id")) {
				continue;
			}
			if (refer.get(entityName+"."+property) == null) { //无关联
				sbf.append(column+"=#{"+property+"},");
			} else {
				if (type.get(entityName+"."+property).equals("n:1")) {
					sbf.append(column+"=#{"+refer.get(entityName+"."+property)+"},");
				} else {
					continue;
				}
			}
//			if (count < size) sbf.append(", ");
		}
//		for (int i = 0; i < field.size(); i++) {
//			if (field.get(i).equals("id")) continue;
//			if (refer.get(entityName+"."+field.get(i)) == null) {
//				sbf.append(columns.get(i)+"=#{"+field.get(i)+"}");
//				if (i < field.size()-1)
//					sbf.append(", ");
//			} else {
//				sbf.append(columns.get(i)+"=#{"+refer.get(field.get(i))+"}");
//				if (i < field.size()-1)
//					sbf.append(", ");
//			}
//		}
		sbf.deleteCharAt(sbf.toString().lastIndexOf(','));
		output.write(sbf.toString()+"\")\n");
		output.write("\tvoid update("+firstLetterUppercase(entityName)+" "+firstLetterLowercase(entityName)+");\n\n");

		//查
		output.write("\t@Select(\"SELECT * FROM "+tableName+" WHERE "+tableId+"=#{id}\")\n");
		output.write("\t@Results({\n");
		Map<String, String> findMap = new HashMap<>(); //生成配套方法标记
		for (Map.Entry<String, String> e : refer.entrySet()) {
			String leftEntity = e.getKey().substring(0, e.getKey().indexOf('.')); //当前实体名
			String leftProperty = e.getKey().substring(e.getKey().indexOf('.')+1); //当前实体的属性名
			String rightEntity = e.getValue().substring(0, e.getValue().indexOf('.')); //关联实体名
			String rightProperty = e.getValue().substring(e.getValue().indexOf('.')+1); //关联属性名
			String column = linkMap.get(leftProperty)==null? tableId : linkMap.get(leftProperty).substring(0, linkMap.get(leftProperty).indexOf(';')); //当前实体属性对应的表列名，当找不到对应列时，即为一对多级联，该实体对应一的部分，此时property为关联属性名，column为主键（关联）
			System.out.println(column);
			if (type.get(e.getKey()).equals("1:n")) { //一对多，找list
				cascadeHandler(output, leftProperty, column, packageName,
						firstLetterUppercase(rightEntity)+"Mapper",
						"find"+firstLetterUppercase(rightEntity)+"sBy"+firstLetterUppercase(rightProperty),
						"many", tableId);
			} else { //多对一，一对一 找一个
				cascadeHandler(output, leftProperty, column, packageName,
						firstLetterUppercase(rightEntity)+"Mapper",
						"find" + firstLetterUppercase(rightEntity) + "By" + firstLetterUppercase(rightProperty),
						"one", tableId);
				findMap.put(leftProperty, type.get(e.getKey()));
			}
		}
		output.write("\t})\n");
		//固定生成由id查找，名字固定，类型固定，参数固定
		output.write("\t"+firstLetterUppercase(entityName)+" find"+firstLetterUppercase(entityName)+"ById("+tableIdType+" id);\n\n");

		//方法名生成规则：find+当前实体类名(s)+By+当前类被关联的表的列的名称
		for (Map.Entry<String, String> e : findMap.entrySet()) {
			String column = linkMap.get(e.getKey()); //反向查找当前属性对应的列名
			System.out.println(column);
			System.out.println(e.getKey());
			String columnName = column.substring(0, column.indexOf(';'));
			String columnType = column.substring(column.indexOf(';')+1);
			output.write("\t@Select(\"SELECT * FROM "+tableName+" WHERE "+columnName+"=#{"+columnName+"}\")\n");
			if (e.getValue().equals("n:1")) {
				output.write("\tList<"+firstLetterUppercase(entityName)+"> " +
						"find"+firstLetterUppercase(entityName)+"sBy"+firstLetterUppercase(columnName)+
						"("+columnType+" "+columnName+");\n");
			} else {
				output.write("\t"+firstLetterUppercase(entityName)+" " +
						"find"+firstLetterUppercase(entityName)+"By"+firstLetterUppercase(columnName)+
						"("+columnType+" "+columnName+");\n");
			}
		}
		output.write("}");
		output.close();

		return temp.getAbsolutePath();
	}

	/**
	 * 在输出流中添加一个@Result
	 * @param output BufferedWriter
	 * @param property 实体类名
	 * @param column 表列名
	 * @param thatMapper 对应的Mapper名
	 * @param packageName
	 * @param type 一对多、 多对多
	 * @param method 命名规则：find+关联实体类名(s)+by+关联的列的名称 e.g. findRecordsByT_uid 此方法在recordMapper中，为UserMapper所调用，级联：1user对多records，即user的id与record的t_uid关联
	 * @throws IOException
	 */
	public void cascadeHandler(BufferedWriter output, String property, String column,
	                           String packageName, String thatMapper, String method, String type, String tableId) throws IOException {
		output.write("\t\t@Result(property = \""+property+"\", column = \""+column+"\", "
				+type+" = @"+type.substring(0,1).toUpperCase()+type.substring(1)
				+"(select = \""+packageName+"."+thatMapper+"."+method+"\")),\n");
		if (column.equals(tableId)) {
			output.write("\t\t@Result(property = \"id\", column = \""+tableId+"\"),\n");
		}
	}


	private File fileHelper(String entityName, String type) throws IOException {
		String path = ResourceUtils.getURL("classpath:").getPath();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		File dir = new File(path + sdf.format(System.currentTimeMillis())+"/"+type);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File temp = null;
		if (type.equals("mapper")) {
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
