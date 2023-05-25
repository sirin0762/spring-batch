package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SimpleFlowConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleFlowJob() {
        return jobBuilderFactory.get("simpleFlowJob")
            .start(simpleFlow1())
            .next(simpleFlowStep2())
            .end()
            .build();
    }

    @Bean
    public Flow simpleFlow1() {
        FlowBuilder<Flow> builder = new FlowBuilder<>("simpleFlow1");
        return builder.start(simpleFlowStep1())
            .next(simpleFlowStep2())
            .build();
    }

    @Bean
    public Step simpleFlowStep1() {
        return stepBuilderFactory.get("simpleFlowStep1")
            .tasklet(((contribution, chunkContext) -> {
                log.info("simpleFlowStep1 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

    @Bean
    public Step simpleFlowStep2() {
        return stepBuilderFactory.get("simpleFlowStep2")
            .tasklet(((contribution, chunkContext) -> {
                log.info("simpleFlowStep2 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

    @Bean
    public Step simpleFlowStep3() {
        return stepBuilderFactory.get("simpleFlowStep3")
            .tasklet(((contribution, chunkContext) -> {
                log.info("simpleFlowStep3 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

}
