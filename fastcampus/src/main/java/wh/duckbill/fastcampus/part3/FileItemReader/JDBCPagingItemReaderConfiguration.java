package wh.duckbill.fastcampus.part3.FileItemReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wh.duckbill.fastcampus.part3.Person;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
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
public class JDBCPagingItemReaderConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job jdbcPagingItemReaderJob() throws Exception {
        return this.jobBuilderFactory.get("jdbcPagingItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(this.jdbcPagingStep())
                .build();
    }

    @Bean
    public Step jdbcPagingStep() throws Exception {
        return stepBuilderFactory.get("jdbcPagingStep")
                .<Person, Person>chunk(10)
                .reader(this.jdbcPagingItemReader())
                .writer(itemWriter())
                .build();
    }

    private JdbcPagingItemReader<Person> jdbcPagingItemReader() throws Exception {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("id", 1); // Id 1인 사람만 조회

        JdbcPagingItemReader jdbcPagingItemReader = new JdbcPagingItemReaderBuilder<Person>()
                .name("jdbcPagingItemReader")
                .dataSource(dataSource)
                .pageSize(1)
                .fetchSize(1)
                .rowMapper((rs, rowNum) -> new Person(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("age"),
                        rs.getString("address")
                ))
                .queryProvider(this.createQueryProvider())
                .parameterValues(parameterValues)
                .build();
        jdbcPagingItemReader.afterPropertiesSet();
        return jdbcPagingItemReader;
    }

    private PagingQueryProvider createQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        queryProviderFactoryBean.setDataSource(dataSource);
        queryProviderFactoryBean.setSelectClause("select id, name, age, address");
        queryProviderFactoryBean.setFromClause("from person");
        queryProviderFactoryBean.setWhereClause("where id = :id"); // Where 절

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.DESCENDING);
        queryProviderFactoryBean.setSortKeys(sortKeys); // Order 절
        return queryProviderFactoryBean.getObject();
    }

    private ItemWriter<Person> itemWriter() {
        return items -> log.info(items.stream().map(Person::toString).collect(Collectors.joining("\n")));
    }
}
