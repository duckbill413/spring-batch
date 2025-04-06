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
  public Job helloJob(){
    return jobBuilderFactory.get("helloJob")
        .start(this.helloStep())
        .build();
  }

  @Bean
  public Step helloStep(){
    return stepBuilderFactory.get("helloStep")
        .tasklet((contribution, chunkContext) -> {
          System.out.println("hello spring batch");
          return RepeatStatus.FINISHED;
        }).build();
  }
}
