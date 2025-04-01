package wh.duckbill.fastcampus.part3.TaskAndChunk;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * author        : duckbill413
 * date          : 2023-01-21
 * description   :
 * Tasklet을 사용한 Task 기반 배치 처리
 * 100개의 문자열을 리스트에 입력하고 리스트의 사이즈를 출력
 * JobParameter를 활용하여 tasklet()에서의 chunkSize를 외부 파라메터를 불러와서 사용
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TaskJobParametersProcessingConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job taskJobParameterProcessingJob(){
        return jobBuilderFactory.get("taskJobParameterProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.taskJobParameterBaseStep())
                .build();
    }

    @Bean
    public Step taskJobParameterBaseStep(){
        return stepBuilderFactory.get("taskJobParameterBaseStep")
                .tasklet(this.tasklet())
                .build();
    }
    private Tasklet tasklet(){
        List<String> items = getItems();

        return (contribution, chunkContext) -> {
            StepExecution stepExecution = contribution.getStepExecution();
            JobParameters jobParameters = stepExecution.getJobParameters();

            String value = jobParameters.getString("chunkSize", "10");
            int chunkSize = StringUtils.isNotEmpty(value) ? Integer.parseInt(value) : 10;
            int fromIndex = (int) stepExecution.getReadCount();
            int toIndex = fromIndex + chunkSize;
            if (toIndex > items.size())
                toIndex = items.size();

            if (fromIndex >= items.size()){
                return RepeatStatus.FINISHED;
            }

            List<String> subList = items.subList(fromIndex, toIndex);

            log.info("task sub item size: {}", subList.size());

            stepExecution.setReadCount(toIndex);
            return RepeatStatus.CONTINUABLE;
        };
    }

    private List<String> getItems(){
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            items.add(i + " Hello");
        }
        return items;
    }
}
