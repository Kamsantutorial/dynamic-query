package com.example.crudrestapi;

import com.example.crudrestapi.util.QRCodeGenerateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrudRestApiApplicationTests {

	@Test
	void contextLoads() {
		QRCodeGenerateUtil.generate();
	}

}
