package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.sirin.job.domain.Customer;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JdbcCursorConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job jdbcCursorItemReaderJob() {
        return jobBuilderFactory.get("jdbcCursorItemReaderJob")
            .incrementer(new RunIdIncrementer())
            .start(jdbcCursorItemReaderStep1())
            .next(jdbcCursorItemReaderStep2())
            .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep1() {
        return stepBuilderFactory.get("jdbcCursorItemReaderJob")
            .<Customer, Customer>chunk(5)
            .reader(jdbcCursorItemReader())
            .writer((items) -> {
                items.forEach(i -> log.info("item = {}", i));
            })
            .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep2() {
        return stepBuilderFactory.get("jdbcCursorItemReaderJob")
            .tasklet(((contribution, chunkContext) -> {
                log.info("jdbcCursorItemReaderStep2 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

    @Bean
    public ItemReader<Customer> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Customer>()
            .name("jdbcCursorItemReader")
            .fetchSize(5)
            .dataSource(dataSource)
            .sql("SELECT name, age, year FROM customer WHERE name LIKE ?")
            .beanRowMapper(Customer.class)
            .queryArguments("N%")
            .build();

    }

}
