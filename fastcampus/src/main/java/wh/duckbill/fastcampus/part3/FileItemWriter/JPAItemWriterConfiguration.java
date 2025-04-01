package wh.duckbill.fastcampus.part3.FileItemWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wh.duckbill.fastcampus.part3.FileItemReader.CustomItemReader;
import wh.duckbill.fastcampus.part3.Person;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * author        : duckbill413
 * date          : 2023-01-30
 * description   :
 **/
@Slf4j
@Configuration
@RequiredArgsConstructor
public class JPAItemWriterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job JPAtemWriterJob() throws Exception {
        return this.jobBuilderFactory.get("JPAItemWriterJob")
                .incrementer(new RunIdIncrementer())
                .start(this.JPAItemWriterStep())
                .build();
    }

    @Bean
    public Step JPAItemWriterStep() throws Exception {
        return this.stepBuilderFactory.get("JDBCItemWriterStep")
                .<Person, Person>chunk(10)
                .reader(this.itemReader())
                .writer(this.jpaItemWriter())
                .build();
    }
    private ItemWriter<Person> jpaItemWriter() throws Exception {
        JpaItemWriter<Person> personJpaItemWriter = new JpaItemWriterBuilder<Person>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();
        personJpaItemWriter.afterPropertiesSet();
        return personJpaItemWriter;
    }
    private ItemReader<Person> itemReader() {
        return new CustomItemReader<>(this.getItems());
    }

    private List<Person> getItems() {
        List<Person> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add(new Person(
//                    i + 1,
                    "test name " + i,
                    "test age " + i,
                    "test address " + i
            ));
        }
        return items;
    }
}
