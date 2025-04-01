package wh.duckbill.fastcampus.part3.TaskAndChunk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
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
 **/
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TaskSizeProcessingConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job taskSizeProcessingJob(){
        return jobBuilderFactory.get("taskSizeProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.taskSizeBaseStep())
                .build();
    }

    @Bean
    public Step taskSizeBaseStep(){
        return stepBuilderFactory.get("taskBaseStep")
                .tasklet(this.tasklet())
                .build();
    }

    private Tasklet tasklet(){
        List<String> items = getItems();

        return (contribution, chunkContext) -> {
            StepExecution stepExecution = contribution.getStepExecution();

            int chunkSize = 10;
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
