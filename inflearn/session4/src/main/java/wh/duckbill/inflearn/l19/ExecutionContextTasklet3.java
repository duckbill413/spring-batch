package wh.duckbill.inflearn.l19;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet3 implements Tasklet {
  @Override
  public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
    // 예외 발생 Tasklet
    // 실패후 Job 을 재시작 하는 경우 실패하기전 Step 을 재활용하는지 확인
    System.out.println("step3 was executed");

    Object name = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("name");

    // 실패 후 재시작할 경우 DB 의 StepExecutionContext 정보를 가져오기 때문에 name 이 null 이 아니게 될 것
    if (name == null) {
      chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("name", "duckbill");
      throw new RuntimeException("step3 was failed");
    }

    return RepeatStatus.FINISHED;
  }
}
