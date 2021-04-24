package com.lunacia.ormgenerator;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//规定不管数据库的主键叫什么名字，生成的pojo统一为id
@SpringBootApplication
@MapperScan(value = "com.lunacia.ormgenerator.mapper")
public class OrmGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrmGeneratorApplication.class, args);
	}

}
