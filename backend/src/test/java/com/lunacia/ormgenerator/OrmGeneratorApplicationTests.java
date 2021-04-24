package com.lunacia.ormgenerator;

import com.lunacia.ormgenerator.service.GeneratorService;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootTest
@MapperScan(value = "com.lunacia.ormgenerator.mapper")
class OrmGeneratorApplicationTests {

}
