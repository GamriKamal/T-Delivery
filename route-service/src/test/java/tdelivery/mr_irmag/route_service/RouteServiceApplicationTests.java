package tdelivery.mr_irmag.route_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "eureka.client.enabled=false")
class RouteServiceApplicationTests extends TestContainerBase{

	@Test
	void contextLoads() {
	}

}
