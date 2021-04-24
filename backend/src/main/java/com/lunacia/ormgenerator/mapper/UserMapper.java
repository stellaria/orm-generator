package com.lunacia.ormgenerator.mapper;

import com.lunacia.ormgenerator.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

	@Insert("INSERT INTO t_user(t_username,t_password) VALUES(#{t_username},#{t_password})")
	void insert (User user);

	@Delete("DELETE FROM t_user WHERE t_id=#{id}")
	void delete (User user);

	@Update("UPDATE t_user SET t_username=#{t_username},t_password=#{t_password}")
	void update (User user);

	@Select("SELECT * FROM t_user WHERE t_id=#{id}")
	@Results({
		@Result(property = "records", column = "t_id", many = @Many(select = "com.lunacia.ormgenerator.mapper.RecordMapper.findRecordsByT_uid")),
		@Result(property = "id", column = "t_id"),
	})
	User findUserById (Integer id);

}