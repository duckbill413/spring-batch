package wh.duckbill.fastcampus.part3.FileItemReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wh.duckbill.fastcampus.part3.Person;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
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
public class JPAPagingItemReaderConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaPagingItemReaderJob() throws Exception {
        return this.jobBuilderFactory.get("JpaPagingItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(this.jpaPagingStep())
                .build();
    }

    @Bean
    public Step jpaPagingStep() throws Exception {
        return stepBuilderFactory.get("jdbcPagingStep")
                .<Person, Person> chunk(10)
                .reader(this.jpaPagingItemReader())
                .writer(itemWriter())
                .build();
    }
    private JpaPagingItemReader<Person> jpaPagingItemReader() throws Exception {
        HashMap<String, Object> paramValues = new HashMap<>();
        paramValues.put("id", 2);
        JpaPagingItemReader jpaPagingItemReader = new JpaPagingItemReaderBuilder<Person>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(2)
                .queryString("select p from Person p")
//                .queryString("select p from Person p where p.id = :id")
//                .parameterValues(paramValues)
                .build();
        jpaPagingItemReader.afterPropertiesSet();
        return jpaPagingItemReader;
    }
    private ItemWriter<Person> itemWriter() {
        return items -> log.info(items.stream().map(Person::getName).collect(Collectors.joining(", ")));
    }
}
