package com.yz.mall.sys;

// import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yunze
 * @since 2025/7/10 14:52
 */
// @RunWith(SpringRunner.class)
@SpringBootTest(classes = TestDataSourceConfig.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
@Transactional
public class BaseMapperTest {
}
