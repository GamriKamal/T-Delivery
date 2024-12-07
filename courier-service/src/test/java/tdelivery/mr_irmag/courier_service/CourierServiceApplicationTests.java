package tdelivery.mr_irmag.courier_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "eureka.client.enabled=false")
class CourierServiceApplicationTests {
    @Test
    void contextLoads() {
    }

}
