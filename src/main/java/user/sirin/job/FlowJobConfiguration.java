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
public class FlowJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flowJob() {
        return jobBuilderFactory.get("flowJob")
            .start(flow1())
            .next(flowStep3())
            .next(flow2())
            .next(flowStep6())
            .end()
            .build();
    }

    @Bean
    public Flow flow1() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow1");
        return flowBuilder.start(flowStep1())
            .next(flowStep2())
            .end();
    }

    @Bean
    public Flow flow2() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow2");
        return flowBuilder.start(flowStep4())
            .next(flowStep5())
            .end();
    }

    @Bean
    public Step flowStep1() {
        return stepBuilderFactory.get("flowStep1")
            .allowStartIfComplete(true)
            .tasklet((ct, ch) -> {
                log.info("step 1 start");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step flowStep2() {
        return stepBuilderFactory.get("flowStep2")
            .tasklet((ct, ch) -> {
                log.info("step 2 start");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step flowStep3() {
        return stepBuilderFactory.get("flowStep3")
            .tasklet((ct, ch) -> {
                log.info("step 3 start");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step flowStep4() {
        return stepBuilderFactory.get("flowStep4")
            .tasklet((ct, ch) -> {
                log.info("step 4 start");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step flowStep5() {
        return stepBuilderFactory.get("flowStep5")
            .tasklet((ct, ch) -> {
                log.info("step 5 start");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step flowStep6() {
        return stepBuilderFactory.get("flowStep6")
            .tasklet((ct, ch) -> {
                log.info("step 6 start");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

}
