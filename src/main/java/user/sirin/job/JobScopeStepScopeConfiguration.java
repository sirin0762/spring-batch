package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.sirin.job.listenter.JobScopeListener;
import user.sirin.job.listenter.StepScopeListener;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobScopeStepScopeConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobScopeStepScopeJob() {
        return jobBuilderFactory.get("jobScopeStepScopeJob")
            .listener(new JobScopeListener())
            .start(jobScopeStepScopeStep1(null))
            .next(jobScopeStepScopeStep2())
            .build();
    }

    @Bean
    @JobScope
    public Step jobScopeStepScopeStep1(@Value("#{jobParameters['message']}") String message) {
        log.info("message = {}", message);
        return stepBuilderFactory.get("JobScopeStepScopeStep1")
            .listener(new StepScopeListener())
            .tasklet(tasklet1(null, null))
            .build();
    }

    @Bean
    public Step jobScopeStepScopeStep2() {
        return stepBuilderFactory.get("JobScopeStepScopeStep2")
            .tasklet(tasklet2())
            .build();
    }

    @Bean
    @StepScope
    public Tasklet tasklet1(
        @Value("#{jobExecutionContext['name']}") String name,
        @Value("#{stepExecutionContext['name2']}") String name2
    ) {
        return (contribution, chunkContext) -> {
            log.info("JobScopeStepScopeStep1 is start");
            log.info("name = {}", name);
            log.info("name2 = {}", name2);
            return RepeatStatus.FINISHED;
        };
    }

    public Tasklet tasklet2() {
        return (contribution, chunkContext) -> {
            log.info("JobScopeStepScopeStep2 is start");
            return RepeatStatus.FINISHED;
        };
    }


}
