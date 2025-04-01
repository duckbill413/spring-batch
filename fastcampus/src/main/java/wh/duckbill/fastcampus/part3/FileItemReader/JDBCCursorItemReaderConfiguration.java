package wh.duckbill.fastcampus.part3.FileItemReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wh.duckbill.fastcampus.part3.Person;

import javax.sql.DataSource;
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
public class JDBCCursorItemReaderConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job jdbcCursorItemReaderJob() throws Exception {
        return this.jobBuilderFactory.get("jdbcCursorItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(this.jdbcCursorStep())
                .build();
    }

    @Bean
    public Step jdbcCursorStep() throws Exception {
        return stepBuilderFactory.get("jdbcCursorStep")
                .<Person, Person> chunk(10)
                .reader(this.jdbcCursorItemReader())
                .writer(itemWriter())
                .build();
    }

    private JdbcCursorItemReader<Person> jdbcCursorItemReader() throws Exception {
        JdbcCursorItemReader jdbcCursorItemReader = new JdbcCursorItemReaderBuilder<Person>()
                .name("jdbcCursorItemReader")
                .dataSource(dataSource)
                .sql("select id, name, age, address from person")
                .rowMapper((rs, rowNum) -> new Person(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)))
                .build();
        jdbcCursorItemReader.afterPropertiesSet();
        return jdbcCursorItemReader;
    }
    private ItemWriter<Person> itemWriter() {
        return items -> log.info(items.stream().map(Person::getName).collect(Collectors.joining(", ")));
    }
}
