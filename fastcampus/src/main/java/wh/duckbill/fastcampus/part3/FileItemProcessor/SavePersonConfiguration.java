package wh.duckbill.fastcampus.part3.FileItemProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import wh.duckbill.fastcampus.part3.Person;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SavePersonConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job savePersonJob() throws Exception {
        return this.jobBuilderFactory.get("savePersonJob")
                .incrementer(new RunIdIncrementer())
                .start(this.savePersonStep(null))
                .listener(new SavePersonListener.SavePersonJobExecutionListener())
                .listener(new SavePersonListener.SavePersonAnnotationJobExecutionListener())
                .build();
    }

    @Bean
    @JobScope
    public Step savePersonStep(@Value("#{jobParameters[allow_duplicate]}") Boolean allowDuplicate) throws Exception {
        return this.stepBuilderFactory.get("savePersonStep")
                .<Person, Person>chunk(10)
                .reader(this.itemReader())
//                .processor(new DuplicateValidationProcessor<>(Person::getName, allowDuplicate))
                .processor(itemProcessor(allowDuplicate))
                .writer(this.compositeItemWriter())
                .listener(new SavePersonListener.SavePersonStepExecutionListener())
                .faultTolerant() // Skip과 같은 예외처리 메소드 지원
                .skip(NotFoundNameException.class)
                .skipLimit(2)
                .build();
    }

    private ItemProcessor<? super Person, ? extends Person> itemProcessor(Boolean allowDuplicate) throws Exception {
        DuplicateValidationProcessor<Person> personDuplicateValidationProcessor =
                new DuplicateValidationProcessor<>(Person::getName, allowDuplicate);

        ItemProcessor<Person, Person> validationProcessor = item -> {
            if (item.isNotEmptyName())
                return item;

            throw new NotFoundNameException();
        };

        return new CompositeItemProcessorBuilder<Person, Person>()
            .delegates(new PersonValidationRetryProcessor(), validationProcessor, personDuplicateValidationProcessor)
            .build();
    }

    public CompositeItemWriter<Person> compositeItemWriter() throws Exception {
        CompositeItemWriter<Person> compositeItemWriter = new CompositeItemWriterBuilder<Person>()
                .delegates(jpaItemWriter(), itemLogWriter())
                .build();
        compositeItemWriter.afterPropertiesSet();
        return compositeItemWriter;
    }

    private ItemWriter<? super Person> jpaItemWriter() throws Exception {
        JpaItemWriter<Person> personJpaItemWriter = new JpaItemWriterBuilder<Person>()
                .entityManagerFactory(entityManagerFactory)
                .build();
        personJpaItemWriter.afterPropertiesSet();
        return personJpaItemWriter;
    }

    private ItemWriter<? super Person> jdbcItemWriter() {
        JdbcBatchItemWriter<Person> personJdbcBatchItemWriter = new JdbcBatchItemWriterBuilder<Person>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("insert into person(name, age, address) values(:name, :age, :address)")
                .build();
        personJdbcBatchItemWriter.afterPropertiesSet();

        return personJdbcBatchItemWriter;
    }

    private ItemWriter<? super Person> itemLogWriter() {
        return items -> log.info("person size: {}", items.size());
    }

    private FlatFileItemReader<Person> itemReader() throws Exception {
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("name", "age", "address");
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> new Person(
                fieldSet.readString(0),
                fieldSet.readString(1),
                fieldSet.readString(2)));

        FlatFileItemReader<Person> itemReader = new FlatFileItemReaderBuilder<Person>()
                .name("savePersonItemReader")
                .encoding("UTF-8")
                .linesToSkip(1)
                .resource(new ClassPathResource("person.csv"))
                .lineMapper(lineMapper)
                .build();

        itemReader.afterPropertiesSet();
        return itemReader;
    }
}