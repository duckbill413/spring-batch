package wh.duckbill.inflearn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job job() {
    return jobBuilderFactory.get("job")
        .start(step1())
        .next(step2())
        .build();
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1")
        .tasklet((stepContribution, chunkContext) -> {
          log.info("step1 was executed");
          JobParameters jobParameters = stepContribution.getStepExecution().getJobExecution().getJobParameters();
          jobParameters.getParameters().forEach((key, value) -> log.info("{} : {}", key, value));
          return RepeatStatus.FINISHED;
        }).build();
  }

  @Bean
  public Step step2() {
    return stepBuilderFactory.get("step2")
        .tasklet(new CustomTasklet()).build();
  }
}
