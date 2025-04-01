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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import wh.duckbill.fastcampus.part3.FileItemReader.CustomItemReader;
import wh.duckbill.fastcampus.part3.Person;

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
public class CSVItemWriterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job CSVItemWriterJob() throws Exception {
        return this.jobBuilderFactory.get("CSVItemWriterJob")
                .incrementer(new RunIdIncrementer())
                .start(this.CSVItemWriterStep())
                .build();
    }

    @Bean
    public Step CSVItemWriterStep() throws Exception {
        return this.stepBuilderFactory.get("CSVItemWriterStep")
                .<Person, Person>chunk(10)
                .reader(this.itemReader())
                .writer(this.csvFileItemWriter())
                .build();
    }

    private ItemWriter<Person> csvFileItemWriter() throws Exception {
        BeanWrapperFieldExtractor fieldExtractor = new BeanWrapperFieldExtractor<Person>();
        fieldExtractor.setNames(new String[]{"id", "name", "age", "address"});

        DelimitedLineAggregator<Person> personDelimitedLineAggregator = new DelimitedLineAggregator<>(); // 각 필드의 값을 하나의 라인에 작성하기 위하여 구분값 필요
        personDelimitedLineAggregator.setDelimiter(", ");
        personDelimitedLineAggregator.setFieldExtractor(fieldExtractor); // mapping 설정 종료

        FlatFileItemWriter<Person> csvFileItemWriter = new FlatFileItemWriterBuilder<Person>()
                .name("csvFileItemWriter")
                .encoding("UTF-8")
                .resource(new FileSystemResource("output/test-output.csv"))
                .lineAggregator(personDelimitedLineAggregator)
                .headerCallback(writer -> writer.write("id, 이름, 나이, 주소")) // header
                .footerCallback(writer -> writer.write("------------------\n")) // footer
                .append(true) // 데이터 추가시 파일이 생성되는 것이 아닌 추가되도록 함
                .build();
        csvFileItemWriter.afterPropertiesSet();
        return csvFileItemWriter;
    }

    private ItemReader<Person> itemReader(){
        return new CustomItemReader<>(this.getItems());
    }
    private List<Person> getItems(){
        List<Person> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add(new Person(
                    i+1,
                    "test name " + i,
                    "test age " + i,
                    "test address " + i
            ));
        }
        return items;
    }
}
