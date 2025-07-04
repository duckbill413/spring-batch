package wh.duckbill.inflearn.l19;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet2 implements Tasklet {
  @Override
  public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
    System.out.println("step2 was executed");

    ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();

    System.out.println("jobName: " + jobExecutionContext.get("jobName") + ", stepName: " + stepExecutionContext.get("stepName"));

    String stepName = chunkContext.getStepContext().getStepName();
    if (stepExecutionContext.get("stepName") == null) {
      stepExecutionContext.put("stepName", stepName);
    }

    return RepeatStatus.FINISHED;
  }
}
