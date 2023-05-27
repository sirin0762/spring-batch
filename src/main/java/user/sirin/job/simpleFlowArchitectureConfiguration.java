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
public class simpleFlowArchitectureConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleFlowArchitectureJob() {
        return jobBuilderFactory.get("simpleFlowArchitectureJob")
            .start(simpleFlowArchitecture1())
            .next(simpleFlowArchitectureStep2())
            .end()
            .build();
    }

    @Bean
    public Flow simpleFlowArchitecture1() {
        FlowBuilder<Flow> builder = new FlowBuilder<>("simpleFlowArchitectureFlow1");
        return builder.start(simpleFlowArchitectureStep1())
            .next(simpleFlowArchitectureStep2())
            .build();
    }

    @Bean
    public Step simpleFlowArchitectureStep1() {
        return stepBuilderFactory.get("simpleFlowArchitectureStep1")
            .tasklet(((contribution, chunkContext) -> {
                log.info("simpleFlowArchitectureStep1 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

    @Bean
    public Step simpleFlowArchitectureStep2() {
        return stepBuilderFactory.get("simpleFlowArchitectureStep2")
            .tasklet(((contribution, chunkContext) -> {
                log.info("simpleFlowArchitectureStep2 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

    @Bean
    public Step simpleFlowArchitectureStep3() {
        return stepBuilderFactory.get("simpleFlowArchitectureStep3")
            .tasklet(((contribution, chunkContext) -> {
                log.info("simpleFlowArchitectureStep3 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

}




