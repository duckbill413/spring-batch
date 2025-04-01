package wh.duckbill.fastcampus.part4.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import wh.duckbill.fastcampus.part4.model.Users;
import wh.duckbill.fastcampus.part4.model.UsersRepository;
import wh.duckbill.fastcampus.part5.JobParametersDecide;
import wh.duckbill.fastcampus.part5.OrderStatistics;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * author        : duckbill413
 * date          : 2023-02-07
 * description   :
 **/
@Slf4j
@Configuration
@RequiredArgsConstructor
public class UsersConfiguration {
    private final int CHUNK_SIZE = 1000;
    private final String JOB_NAME = "userJob";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final UsersRepository usersRepository;
    private final DataSource dataSource;

    /**
     * Users grade job job.
     * 1. 유저 정보와 주문 정보를 저장하는 saveUserStep 실행
     * 2. 유저의 주문 금액에 맞는 등급을 부여하는 userLevelUpStep 실행
     * 3. date 파라메터가 존재하면 orderStatisticsStep이 실행되고 없다면 실행되지 않는다.
     *
     * @return the job
     * @throws Exception the exception
     */
    @Bean(JOB_NAME)
    public Job usersGradeJob() throws Exception {
        return this.jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(this.saveUserStep())
                .next(this.userLevelUpStep())
                .listener(new UsersItemListener(usersRepository))
                .next(new JobParametersDecide("date"))
                .on(JobParametersDecide.CONTINUE.getName())
                .to(this.orderStatisticsStep(null, null))
                .build()
                .build();
    }

    @Bean(JOB_NAME + "_orderStatisticsStep")
    @JobScope
    public Step orderStatisticsStep(@Value("#{jobParameters[date]}") String date,
                                    @Value("#{jobParameters[path]}") String path) throws Exception {
        return this.stepBuilderFactory.get(JOB_NAME + "_orderStatisticsStep")
                .<OrderStatistics, OrderStatistics>chunk(CHUNK_SIZE)
                .reader(orderStatisticsReader(date))
                .writer(orderStatisticsWriter(date, path))
                .build();
    }

    private ItemWriter<? super OrderStatistics> orderStatisticsWriter(String date, String path) throws Exception {
        YearMonth yearMonth = YearMonth.parse(date);
        String fileName = yearMonth.getYear() + "년_" + yearMonth.getMonthValue() + "월_일별_주문_금액.csv";

        BeanWrapperFieldExtractor<OrderStatistics> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"amount", "date"});

        DelimitedLineAggregator<OrderStatistics> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(", ");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<OrderStatistics> fileItemWriter = new FlatFileItemWriterBuilder<OrderStatistics>()
                .name(JOB_NAME + "_orderStatisticsWriter")
                .encoding("UTF-8")
                .resource(new FileSystemResource(path + fileName))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("총액, 날짜"))
                .append(false)
                .build();
        fileItemWriter.afterPropertiesSet();
        return fileItemWriter;
    }

    private ItemReader<? extends OrderStatistics> orderStatisticsReader(String date) throws Exception {
        YearMonth yearMonth = YearMonth.parse(date);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", yearMonth.atDay(1));
        parameters.put("endDate", yearMonth.atEndOfMonth());

        Map<String, Order> sortKey = new HashMap<>();
        sortKey.put("created_date", Order.ASCENDING);

        JdbcPagingItemReader<OrderStatistics> itemReader = new JdbcPagingItemReaderBuilder<OrderStatistics>()
                .name(JOB_NAME + "_orderStatisticsReader")
                .dataSource(dataSource)
                .rowMapper((rs, rowNum) -> OrderStatistics.builder()
                        .amount(rs.getString(1))
                        .date(LocalDate.parse(rs.getString(2), DateTimeFormatter.ISO_DATE))
                        .build())
                .pageSize(CHUNK_SIZE)
                .selectClause("sum(price) as amount, created_date")
                .fromClause("orders")
                .whereClause("created_date >= :startDate and created_date <= :endDate")
                .groupClause("created_date")
                .parameterValues(parameters)
                .sortKeys(sortKey)
                .build();
        itemReader.afterPropertiesSet();
        return itemReader;
    }

    @Bean(JOB_NAME + "_saveUserStep")
    public Step saveUserStep() {
        return this.stepBuilderFactory.get(JOB_NAME + "_saveUserStep")
                .tasklet(new SaveUsersTasklet(usersRepository))
                .build();
    }

    @Bean(JOB_NAME + "_userLevelUpStep")
    public Step userLevelUpStep() throws Exception {
        return this.stepBuilderFactory.get(JOB_NAME + "_userLevelUpStep")
                .<Users, Users>chunk(CHUNK_SIZE)
                .reader(this.loadUsersData())
                .processor(this.checkUsersData())
                .writer(this.fixUsersGradeData())
                .build();
    }

    private ItemReader<? extends Users> loadUsersData() throws Exception {
        JpaPagingItemReader<Users> jpaPagingItemReader = new JpaPagingItemReaderBuilder<Users>()
                .name(JOB_NAME + "_loadUsersData")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select u from Users u")
                .build();
        jpaPagingItemReader.afterPropertiesSet();
        return jpaPagingItemReader;
    }

    private ItemProcessor<? super Users, ? extends Users> checkUsersData() {
        return user -> {
            if (user.availableLevelUp())
                return user;

            return null;
        };
    }

    private ItemWriter<? super Users> fixUsersGradeData() throws Exception {
        return users -> users.forEach(user -> {
            user.levelUp();
            usersRepository.save(user);
        });
    }
}
