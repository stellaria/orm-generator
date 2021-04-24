package com.lunacia.ormgenerator.mapper;

import com.lunacia.ormgenerator.pojo.Type;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TypeMapper {

	@Insert("INSERT INTO type(name) VALUES(#{name})")
	void insert (Type type);

	@Delete("DELETE FROM type WHERE id=#{id}")
	void delete (Type type);

	@Update("UPDATE type SET name=#{name}")
	void update (Type type);

	@Select("SELECT * FROM type WHERE id=#{id}")
	@Results({
		@Result(property = "pis", column = "id", many = @Many(select = "com.lunacia.ormgenerator.mapper.ProductInfoMapper.findProductInfosByTid")),
		@Result(property = "id", column = "id"),
	})
	Type findTypeById (Integer id);

}