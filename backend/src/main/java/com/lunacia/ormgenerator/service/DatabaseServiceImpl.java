package com.lunacia.ormgenerator.service;

import com.lunacia.ormgenerator.mapper.DatabaseMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseServiceImpl implements DatabaseService{

	@Override
	public List<List<Map>> getSchemaInfo (String url, String username, String passwd, String type) {
		SqlSession session = null;
		try {
			 session = getSession(url, username, passwd, type);
		} catch (Exception e) {
			e.printStackTrace();
		}

		DatabaseMapper databaseMapper = session.getMapper(DatabaseMapper.class);

		List<Map> tables = databaseMapper.listTable();
		List<List<Map>> l = new LinkedList();
		for (Map m:tables) {
			List<Map> n = databaseMapper.listTableColumn((String) m.get("TABLE_NAME"));
			l.add(n);
		}


		session.close();
		return l;
	}

//	@Override
//	public void generatePOJO (String name, List<String> field) {
//		Resource resource = new ClassPathResource("/templates/template.java");
//		File f;
//		try {
//			f = resource.getFile();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

	private SqlSession getSession(String url, String username, String passwd, String type) throws Exception {
		String driver = null;
		if (type.equals("mysql8")) {
			driver = "com.mysql.cj.jdbc.Driver";
		} else if (type.equals("mysql5")) {
			driver = "com.mysql.jdbc.Driver";
		}

		PooledDataSource dataSource = new PooledDataSource();
		dataSource.setDriver(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(passwd);

		SqlSessionFactoryBean bean =  new SqlSessionFactoryBean();

		bean.setDataSource(dataSource);
		SqlSessionFactory sqlSessionFactory = bean.getObject();
		sqlSessionFactory.getConfiguration().addMapper(DatabaseMapper.class);

		SqlSession session = sqlSessionFactory.openSession();

		return session;
	}


}
