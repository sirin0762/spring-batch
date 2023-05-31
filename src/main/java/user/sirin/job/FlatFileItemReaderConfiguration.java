package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import user.sirin.job.domain.Customer;
import user.sirin.job.itemReader.flatFile.CustomerFieldSetMapper;
import user.sirin.job.itemReader.flatFile.DefaultLineMapper;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FlatFileItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFileItemReaderJob() {
        return jobBuilderFactory.get("flatFileItemReaderJob")
            .incrementer(new RunIdIncrementer())
            .start(flatFileItemReaderStep1())
            .next(flatFileItemReaderStep2())
            .build();
    }

    @Bean
    public Step flatFileItemReaderStep1() {
        return stepBuilderFactory.get("flatFileItemReaderJob")
            .<Customer, Customer>chunk(5)
            .reader(itemReader())
            .writer((items) -> {
                items.forEach(i -> log.info("item = {}", i));
            })
            .build();
    }

    @Bean
    public Step flatFileItemReaderStep2() {
        return stepBuilderFactory.get("flatFileItemReaderJob")
            .tasklet(((contribution, chunkContext) -> {
                log.info("flatFileItemReaderStep2 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

//    @Bean
//    public ItemReader<Customer> itemReader() {
//        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//        itemReader.setResource(new ClassPathResource("/customer.csv"));
//
//        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
//        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
//        lineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
//        itemReader.setLineMapper(lineMapper);
//        itemReader.setLinesToSkip(1);
//        return itemReader;
//    }

    @Bean
    public ItemReader<Customer> itemReader() {
        return new FlatFileItemReaderBuilder<Customer>()
            .name("flatFile")
            .resource(new ClassPathResource("/customer.csv"))
//            .fieldSetMapper(new CustomerFieldSetMapper())
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
            .targetType(Customer.class)
            .linesToSkip(1)
//            .lineTokenizer(new DelimitedLineTokenizer())
            .delimited().delimiter(",")
            .names("name", "age", "year")
            .build();
    }

}
