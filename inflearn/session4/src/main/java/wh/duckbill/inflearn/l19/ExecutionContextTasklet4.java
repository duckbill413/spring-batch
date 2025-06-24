package wh.duckbill.inflearn.l19;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet4 implements Tasklet {
  @Override
  public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
    System.out.println("step4 was executed");

    // 실패 후 재시작 하는 경우 실행됨
    // JobExecutionContext 의 name 을 가져와서 출력하게 됨
    System.out.println("name: " + chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("name"));
    return RepeatStatus.FINISHED;
  }
}
