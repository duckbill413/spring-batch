package wh.duckbill.fastcampus;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "wh.duckbill.fastcampus")
@EntityScan(basePackages = "wh.duckbill.fastcampus")
@ComponentScan(basePackages = "wh.duckbill.fastcampus")
public class TestConfiguration {
}
