package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class BatchStatusExitStatusConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job statusWithSimpleJob() {
        return jobBuilderFactory.get("statusWithSimpleJob")
            .start(statusStep1())
            .next(statusStep2())
            .build();
    }

    @Bean
    public Job statusWithFlowJob() {
        return jobBuilderFactory.get("statusWithFlowJob")
            .start(statusStep1())
            .on("FAILED").to(statusStep2())
            .end()
            .build();
    }

    @Bean
    public Step statusStep1() {
        return stepBuilderFactory.get("statusStep1")
            .tasklet(((contribution, chunkContext) -> {
                log.info("step 1 start");
                contribution.setExitStatus(ExitStatus.FAILED);
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

    @Bean
    public Step statusStep2() {
        return stepBuilderFactory.get("statusStep2")
            .tasklet(((contribution, chunkContext) -> {
                log.info("step 2 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }
}
