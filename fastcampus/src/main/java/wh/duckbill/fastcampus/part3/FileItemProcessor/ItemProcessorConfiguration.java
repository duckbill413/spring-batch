package wh.duckbill.fastcampus.part3.FileItemProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wh.duckbill.fastcampus.part3.FileItemReader.CustomItemReader;
import wh.duckbill.fastcampus.part3.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * author        : duckbill413
 * date          : 2023-01-31
 * description   :
 **/
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ItemProcessorConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemProcessorJob() {
        return this.jobBuilderFactory.get("ItemProcessorJob")
                .incrementer(new RunIdIncrementer())
                .start(this.itemProcessorStep())
                .build();
    }

    @Bean
    public Step itemProcessorStep() {
        return this.stepBuilderFactory.get("ItemProcessorStep")
                .<Person, Person>chunk(10)
                .reader(this.itemReader())
                .processor(this.itemProcessor())
                .writer(this.itemWriter())
                .build();
    }

    private ItemWriter<? super Person> itemWriter() {
        return items -> items.forEach(x -> log.info("Person.Id : {}", x.getId()));
    }

    private ItemProcessor<? super Person, ? extends Person> itemProcessor() {
        return item -> {
            if (item.getId() % 2 == 0)
                return item;
            else
                return null;
        };
    }

    private ItemReader<? extends Person> itemReader() {
        return new CustomItemReader<>(this.getItems());
    }

    private List<Person> getItems() {
        List<Person> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            items.add(new Person(i + 1, "test name " + i, "test age " + i, "test address " + i));
        }
        return items;
    }
}
