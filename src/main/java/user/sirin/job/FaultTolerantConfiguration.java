package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class FaultTolerantConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job FaultTolerantJob() {
        return jobBuilderFactory.get("FaultTolerantJob")
            .incrementer(new RunIdIncrementer())
            .start(FaultTolerantStep1())
            .build();
    }

    @Bean
    public Step FaultTolerantStep1() {
        return stepBuilderFactory.get("FaultTolerantStep")
            .<String, String>chunk(5)
            .reader(
                new ItemReader<>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception {
                        i++;

                        if (i == 1) {
                            throw new IllegalArgumentException("this exception is skipped");
                        }

                        return i > 3 ? null : "item" + i;
                    }
                }
            )
            .processor((ItemProcessor<String, String>) item -> {
                throw new IllegalArgumentException("this exception is skipped");
//                    return null;
            })
            .writer((items) -> {
                items.forEach(i -> log.info("item = {}", i));
            })
            .faultTolerant()
            .skip(IllegalArgumentException.class)
            .skipLimit(2)
            .retry(IllegalArgumentException.class)
            .retryLimit(2)
            .build();
    }

}
