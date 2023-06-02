package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.sirin.job.domain.Customer;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JpaCursorItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaCursorItemReaderJob() {
        return jobBuilderFactory.get("jpaCursorItemReaderJob")
            .incrementer(new RunIdIncrementer())
            .start(jpaCursorItemReaderStep1())
            .next(jpaCursorItemReaderStep2())
            .build();
    }

    @Bean
    public Step jpaCursorItemReaderStep1() {
        return stepBuilderFactory.get("jpaCursorItemReaderJob")
            .<Customer, Customer>chunk(5)
            .reader(jpaCursorItemReader())
            .writer((items) -> {
                items.forEach(i -> log.info("item = {}", i));
            })
            .build();
    }

    @Bean
    public Step jpaCursorItemReaderStep2() {
        return stepBuilderFactory.get("jpaCursorItemReaderJob")
            .tasklet(((contribution, chunkContext) -> {
                log.info("jpaCursorItemReaderStep2 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

    @Bean
    public ItemReader<? extends Customer> jpaCursorItemReader() {
        return new JpaCursorItemReaderBuilder<Customer>()
            .name("jpaCursorItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT c FROM customer c")
            .parameterValues(Map.of("name", "N%"))
            .build();
    }

}
