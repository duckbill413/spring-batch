package wh.duckbill.inflearn;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 섹션 4. 스프링 배치 도메인 이해
 * JobInstance
 */
@Component
@RequiredArgsConstructor
public class JobParameterTest implements ApplicationRunner {
  private final JobLauncher jobLauncher;
  private final Job job;

  @Override
  public void run(ApplicationArguments args) throws Exception {
//    JobParameters jobParameters = new JobParametersBuilder()
//        .addString("key", UUID.randomUUID().toString())
//        .addString("name", "user1")
//        .addLong("seq", 2L)
//        .addDate("date", new Date())
//        .addDouble("price", 100.0)
//        .toJobParameters();

//    jobLauncher.run(job, jobParameters);
  }
}
