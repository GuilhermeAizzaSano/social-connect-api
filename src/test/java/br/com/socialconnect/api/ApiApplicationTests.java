package br.com.socialconnect.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Disabled("Requer Docker daemon ativo para execução com Testcontainers")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
