package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.sirin.job.listenter.CustomExitStatusListener;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class CustomExitStatusConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job customExitStatusJob() {
        return jobBuilderFactory.get("customExitStatusJob")
            .start(customExitStatusStep1())
                .on("DO PASS")
                .to(customExitStatusStep2())
            .from(customExitStatusStep1())
                .on("COMPLETED")
                .to(customExitStatusStep3())
            .end()
            .build();
    }

    @Bean
    public Step customExitStatusStep1() {
        return stepBuilderFactory.get("customExitStatusStep1")
            .tasklet(((contribution, chunkContext) -> {
                log.info("customExitStatusStep1 is start");
                return RepeatStatus.FINISHED;
            }))
            .listener(new CustomExitStatusListener())
            .build();
    }

    @Bean
    public Step customExitStatusStep2() {
        return stepBuilderFactory.get("customExitStatusStep2")
            .tasklet(((contribution, chunkContext) -> {
                log.info("customExitStatusStep2 is start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

    @Bean
    public Step customExitStatusStep3() {
        return stepBuilderFactory.get("customExitStatusStep3")
            .tasklet(((contribution, chunkContext) -> {
                log.info("customExitStatusStep3 is start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

}
