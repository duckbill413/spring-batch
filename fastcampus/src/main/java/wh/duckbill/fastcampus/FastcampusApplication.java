package wh.duckbill.fastcampus;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class FastcampusApplication {

  public static void main(String[] args) {
    SpringApplication.run(FastcampusApplication.class, args);
  }

}
