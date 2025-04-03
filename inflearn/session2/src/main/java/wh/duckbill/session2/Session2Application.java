package wh.duckbill.session2;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @EnableBatchProcessing
 * 총 4개의 설정 클래스를 실행시키며 스프링 배치의 모든 초기화 및 실행 구성이 이루어짐
 * 스프링 부트 배치의 자동 설정 클래스가 실행됨으로 빈으로 등록된 모든 Job 을 검색하여 초기화와 동시에 Job 을 수행하도록 구성됨
 */
@SpringBootApplication
@EnableBatchProcessing
public class Session2Application {

  public static void main(String[] args) {
    SpringApplication.run(Session2Application.class, args);
  }

}
