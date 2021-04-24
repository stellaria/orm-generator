package com.lunacia.ormgenerator.mapper;

import com.lunacia.ormgenerator.pojo.Record;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RecordMapper {

	@Insert("INSERT INTO t_record(t_record,t_uid) VALUES(#{t_record},#{user.id})")
	void insert (Record record);

	@Delete("DELETE FROM t_record WHERE t_id=#{id}")
	void delete (Record record);

	@Update("UPDATE t_record SET t_record=#{t_record},t_uid=#{user.id}")
	void update (Record record);

	@Select("SELECT * FROM t_record WHERE t_id=#{id}")
	@Results({
		@Result(property = "user", column = "t_uid", one = @One(select = "com.lunacia.ormgenerator.mapper.UserMapper.findUserById")),
	})
	Record findRecordById (Integer id);

	@Select("SELECT * FROM t_record WHERE t_uid=#{t_uid}")
	List<Record> findRecordsByT_uid (Integer t_uid);
}