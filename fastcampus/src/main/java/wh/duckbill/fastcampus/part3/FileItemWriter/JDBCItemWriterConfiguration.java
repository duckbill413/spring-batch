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
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wh.duckbill.fastcampus.part3.FileItemReader.CustomItemReader;
import wh.duckbill.fastcampus.part3.Person;

import javax.sql.DataSource;
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
public class JDBCItemWriterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job JDBCtemWriterJob() throws Exception {
        return this.jobBuilderFactory.get("JDBCItemWriterJob")
                .incrementer(new RunIdIncrementer())
                .start(this.JDBCItemWriterStep())
                .build();
    }

    @Bean
    public Step JDBCItemWriterStep() throws Exception {
        return this.stepBuilderFactory.get("JDBCItemWriterStep")
                .<Person, Person>chunk(10)
                .reader(this.itemReader())
                .writer(this.jdbcBatchItemWriter())
                .build();
    }

    private ItemWriter<Person> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<Person> personJdbcBatchItemWriter = new JdbcBatchItemWriterBuilder<Person>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("insert into person(name, age, address) values(:name, :age, :address)")
                .build();
        personJdbcBatchItemWriter.afterPropertiesSet();
        return personJdbcBatchItemWriter;
    }

    private ItemReader<Person> itemReader() {
        return new CustomItemReader<>(this.getItems());
    }

    private List<Person> getItems() {
        List<Person> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add(new Person(
                    i + 1,
                    "test name " + i,
                    "test age " + i,
                    "test address " + i
            ));
        }
        return items;
    }
}
