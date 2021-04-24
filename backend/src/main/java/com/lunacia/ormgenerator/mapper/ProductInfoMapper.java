package com.lunacia.ormgenerator.mapper;

import com.lunacia.ormgenerator.pojo.ProductInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductInfoMapper {

	@Insert("INSERT INTO product_info(code,name,tid) VALUES(#{code},#{name},#{type.id})")
	void insert (ProductInfo productInfo);

	@Delete("DELETE FROM product_info WHERE id=#{id}")
	void delete (ProductInfo productInfo);

	@Update("UPDATE product_info SET code=#{code},name=#{name},tid=#{type.id}")
	void update (ProductInfo productInfo);

	@Select("SELECT * FROM product_info WHERE id=#{id}")
	@Results({
		@Result(property = "type", column = "tid", one = @One(select = "com.lunacia.ormgenerator.mapper.TypeMapper.findTypeById")),
	})
	ProductInfo findProductInfoById (Integer id);

	@Select("SELECT * FROM product_info WHERE tid=#{tid}")
	List<ProductInfo> findProductInfosByTid (Integer tid);
}