package wh.duckbill.fastcampus.part2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author        : duckbill413
 * date          : 2023-01-20
 * description   :
 * - Step 간의 데이터 공유를 확인해 볼 수 있는 예제
 **/
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SharedConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    /**
     * Share job job.
     * shareStep 과 shareStep2 를 등록하여 실행
     * @return the job
     */
    @Bean
    public Job shareJob(){
        return jobBuilderFactory.get("shareJob")
                .incrementer(new RunIdIncrementer())
                .start(this.shareStep())
                .next(this.shareStep2())
                .build();
    }

    /**
     * Share step step.
     * Execution Context의 임의의 값을 삽입
     * Job과 Step의 이름과 run.id parameter를 꺼내서 로그 출력
     * @return the step
     */
    @Bean
    public Step shareStep(){
        return stepBuilderFactory.get("shareStep")
                .tasklet((contribution, chunkContext) -> {
                    StepExecution stepExecution = contribution.getStepExecution();
                    ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
                    stepExecutionContext.putString("stepKey", "step execution context");

                    JobExecution jobExecution = stepExecution.getJobExecution();
                    JobInstance jobInstance = jobExecution.getJobInstance();
                    ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
                    jobExecutionContext.putString("jobKey", "job execution context");
                    JobParameters jobParameters = jobExecution.getJobParameters();

                    log.info("jobName : {}, stepName : {}, parameter : {}",
                            jobInstance.getJobName(),
                            stepExecution.getStepName(),
                            jobParameters.getLong("run.id"));

                    return RepeatStatus.FINISHED;
                }).build();
    }

    /**
     * Share step 2 step.
     * Execution Context의 값을 꺼내에 로그 출력
     * @return the step
     */
    @Bean
    public Step shareStep2(){
        return stepBuilderFactory.get("shareStep2")
                .tasklet((contribution, chunkContext) -> {
                    StepExecution stepExecution = contribution.getStepExecution();
                    ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();

                    JobExecution jobExecution = stepExecution.getJobExecution();
                    ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();

                    log.info("jobKey: {}, stepKey : {}",
                            jobExecutionContext.getString("jobKey", "emptyJobKey"),
                            stepExecutionContext.getString("stepKey", "emptyStepKey"));

                    return RepeatStatus.FINISHED;
                }).build();
    }
}
