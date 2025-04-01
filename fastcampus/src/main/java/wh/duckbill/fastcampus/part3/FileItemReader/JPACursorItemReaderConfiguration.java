package wh.duckbill.fastcampus.part3.FileItemReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wh.duckbill.fastcampus.part3.Person;

import javax.persistence.EntityManagerFactory;
import java.util.stream.Collectors;

/**
 * author        : duckbill413
 * date          : 2023-01-24
 * description   :
 * ListItemReader 과 같은 기능을 재현
 **/
@Slf4j
@Configuration
@RequiredArgsConstructor
public class JPACursorItemReaderConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaCursorItemReaderJob() throws Exception {
        return this.jobBuilderFactory.get("JpaCursorItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(this.jpaCursorStep())
                .build();
    }

    @Bean
    public Step jpaCursorStep() throws Exception {
        return stepBuilderFactory.get("jdbcCursorStep")
                .<Person, Person> chunk(10)
                .reader(this.jpaCursorItemReader())
                .writer(itemWriter())
                .build();
    }
    private JpaCursorItemReader<Person> jpaCursorItemReader() throws Exception {
        JpaCursorItemReader jpaCursorItemReader = new JpaCursorItemReaderBuilder<Person>()
                .name("jpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select p from Person p")
                .build();
        jpaCursorItemReader.afterPropertiesSet();
        return jpaCursorItemReader;
    }
    private ItemWriter<Person> itemWriter() {
        return items -> log.info(items.stream().map(Person::getName).collect(Collectors.joining(", ")));
    }
}
