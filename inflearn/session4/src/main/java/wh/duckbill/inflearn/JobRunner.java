package wh.duckbill.inflearn;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 섹션 4. 스프링 배치 도메인 이해
 * Job
 */
@Component
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {
  private final JobLauncher jobLauncher;

  private final Job job;

  @Override
  public void run(ApplicationArguments args) throws Exception {
//    JobParameters jobParameters = new JobParametersBuilder()
//        .addString("key", UUID.randomUUID().toString())
//        .addString("name", "duckbill")
//        .toJobParameters();
//    jobLauncher.run(job, jobParameters);
  }
}
