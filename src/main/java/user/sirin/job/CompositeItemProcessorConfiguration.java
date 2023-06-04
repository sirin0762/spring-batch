package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class CompositeItemProcessorConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job compositeItemProcessorJob() {
        return jobBuilderFactory.get("compositeItemProcessorJob")
            .incrementer(new RunIdIncrementer())
            .start(compositeItemProcessorStep1())
            .build();
    }

    @Bean
    public Step compositeItemProcessorStep1() {
        return stepBuilderFactory.get("compositeItemProcessorJob")
            .<String, String>chunk(5)
            .reader(new ListItemReader<>(
                List.of("User", "Aser", "User","User","User","Aser","User")
            ))
            .processor(compositeItemProcessor())
            .writer((items) -> {
                items.forEach(i -> log.info("item = {}", i));
            })
            .build();
    }


    @Bean
    public ItemProcessor<String, String> compositeItemProcessor() {
        return new CompositeItemProcessorBuilder<String, String>()
            .delegates(
                List.of(
                    (ItemProcessor<String, String>) item -> item.toUpperCase(),
                    (ItemProcessor<String, String>) item -> item + UUID.randomUUID().toString().substring(0, 8),
                    (ItemProcessor<String, String>) item -> item.startsWith("U") ? item : null

                )
            )
            .build();
    }

}
