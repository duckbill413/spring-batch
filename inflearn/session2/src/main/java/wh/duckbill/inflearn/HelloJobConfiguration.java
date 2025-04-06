package wh.duckbill.inflearn;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HelloJobConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job helloJob() {
    return jobBuilderFactory.get("helloJob")
        .start(this.helloStep1())
        .next(this.helloStep2())
        .build();
  }

  @Bean
  public Step helloStep1() {
    return stepBuilderFactory.get("helloStep1")
        .tasklet((contribution, chunkContext) -> {
          System.out.println("==================================");
          System.out.println("hello spring batch 1");
          System.out.println("==================================");
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Step helloStep2() {
    return stepBuilderFactory.get("helloStep2")
        .tasklet((stepContribution, chunkContext) -> {
          System.out.println("==================================");
          System.out.println("hello spring batch 2");
          System.out.println("==================================");
          return RepeatStatus.FINISHED;
        }).build();
  }
}
