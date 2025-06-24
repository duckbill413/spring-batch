package wh.duckbill.inflearn.l19;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class ExecutionContextConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  private final ExecutionContextTasklet1 executionContextTasklet1;
  private final ExecutionContextTasklet2 executionContextTasklet2;
  private final ExecutionContextTasklet3 executionContextTasklet3;
  private final ExecutionContextTasklet4 executionContextTasklet4;

  @Bean
  public Job BatchJob_l19() {
    return this.jobBuilderFactory.get("Job_l19")
        .start(step1_l19())
        .next(step2_l19())
        .next(step3_l19())
        .next(step4_l19())
        .build();
  }

  @Bean
  public Step step1_l19() {
    return stepBuilderFactory.get("step1_l19")
        .tasklet(executionContextTasklet1)
        .build();
  }

  @Bean
  public Step step2_l19() {
    return stepBuilderFactory.get("step2_l19")
        .tasklet(executionContextTasklet2)
        .build();
  }

  @Bean
  public Step step3_l19() {
    return stepBuilderFactory.get("step3_19")
        .tasklet(executionContextTasklet3)
        .build();
  }

  @Bean
  public Step step4_l19() {
    return stepBuilderFactory.get("step4_19")
        .tasklet(executionContextTasklet4)
        .build();
  }
}
