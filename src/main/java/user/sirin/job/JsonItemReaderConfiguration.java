package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import user.sirin.job.domain.Customer;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class JsonItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job JsonItemReaderJob() {
        return jobBuilderFactory.get("JsonItemReaderJob")
            .incrementer(new RunIdIncrementer())
            .start(JsonItemReaderStep1())
            .next(JsonItemReaderStep2())
            .build();
    }

    @Bean
    public Step JsonItemReaderStep1() {
        return stepBuilderFactory.get("JsonItemReaderJob")
            .<Customer, Customer>chunk(5)
            .reader(JsonItemReader())
            .writer((items) -> {
                items.forEach(i -> log.info("item = {}", i));
            })
            .build();
    }

    @Bean
    public Step JsonItemReaderStep2() {
        return stepBuilderFactory.get("JsonItemReaderJob")
            .tasklet(((contribution, chunkContext) -> {
                log.info("JsonItemReaderStep2 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

    @Bean
    public ItemReader<Customer> JsonItemReader() {
        return new JsonItemReaderBuilder<Customer>()
            .name("jsonItemReader")
            .resource(new ClassPathResource("customer.json"))
            .jsonObjectReader(new JacksonJsonObjectReader<>(Customer.class))
            .build();

    }

}
