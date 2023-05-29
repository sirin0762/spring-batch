package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ChunkConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkJob() {
        return jobBuilderFactory.get("chunkJob")
            .start(chunkStep1())
            .build();

    }

    @Bean
    public Step chunkStep1() {
        return stepBuilderFactory.get("chunkStep1")
            .<String, String> chunk(5)
            .reader(new ListItemReader<>(List.of("h")))
            .processor((ItemProcessor<String, String>) item -> item + "1")
            .writer(items -> log.info("items = {}", items))
            .build();
    }

}
