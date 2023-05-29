package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.sirin.job.domain.Customer;
import user.sirin.job.itemProcessor.CustomItemProcessor;
import user.sirin.job.itemReader.CustomItemReader;
import user.sirin.job.itemReader.CustomItemStreamReader;
import user.sirin.job.itemWriter.CustomItemStreamWriter;
import user.sirin.job.itemWriter.CustomItemWriter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ItemStreamConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemStreamJob() {
        return jobBuilderFactory.get("itemStreamJob")
            .start(itemStreamStep1())
            .build();

    }

    @Bean
    public Step itemStreamStep1() {
        return stepBuilderFactory.get("itemStreamStep1")
            .<String, String> chunk(3)
            .reader(new CustomItemStreamReader(
                List.of(
                    "user1",
                    "user2",
                    "user3",
                    "user4",
                    "user5"
                )
            ))
            .writer(new CustomItemStreamWriter())
            .build();
    }

}
