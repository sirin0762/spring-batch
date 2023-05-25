package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.sirin.job.decider.CustomDecider;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class JobExecutionDeciderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job executionDeciderJob() {
        return jobBuilderFactory.get("executionDeciderJob")
            .incrementer(new RunIdIncrementer())
            .start(executionDeciderStep1())
            .next(decider())
            .from(decider()).on("EVEN").to(executionDeciderEvenStep())
            .from(decider()).on("ODD").to(executionDeciderOddStep())
            .from(decider()).on("*").to(executionDeciderStep1())
            .end()
            .build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new CustomDecider();
    }

    @Bean
    public Step executionDeciderStep1() {
        return stepBuilderFactory.get("executionDeciderStep1")
            .allowStartIfComplete(true)
            .tasklet((contribution, chunkContext) -> {
                log.info("executionDeciderStep1 start");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step executionDeciderOddStep() {
        return stepBuilderFactory.get("executionDeciderOddStep")
            .tasklet((contribution, chunkContext) -> {
                log.info("executionDeciderOddStep start");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step executionDeciderEvenStep() {
        return stepBuilderFactory.get("executionDeciderEvenStep")
            .tasklet((contribution, chunkContext) -> {
                log.info("executionDeciderEvenStep start");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

}
