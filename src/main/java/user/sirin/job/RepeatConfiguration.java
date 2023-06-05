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
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class RepeatConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job RepeatJob() {
        return jobBuilderFactory.get("RepeatJob")
            .incrementer(new RunIdIncrementer())
            .start(RepeatStep1())
            .build();
    }

    @Bean
    public Step RepeatStep1() {
        return stepBuilderFactory.get("RepeatStep")
            .<String, String>chunk(5)
            .reader(
                new ItemReader<>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception {
                        i++;
                        return i > 3 ? null : "item" + i;
                    }
                }
            )
            .processor(new ItemProcessor<>() {

                RepeatTemplate repeatTemplate = new RepeatTemplate();

                @Override
                public String process(String item) throws Exception {

//                    repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(5));
//                    repeatTemplate.setCompletionPolicy(new TimeoutTerminationPolicy(3000));

                    CompositeCompletionPolicy compositeCompletionPolicy = new CompositeCompletionPolicy();
                    CompletionPolicy[] completionPolicies = new CompletionPolicy[]{
                        new SimpleCompletionPolicy(5),
                        new TimeoutTerminationPolicy(3000)
                    };

                    repeatTemplate.setExceptionHandler(new SimpleLimitExceptionHandler(3));

                    compositeCompletionPolicy.setPolicies(completionPolicies);
                    repeatTemplate.setCompletionPolicy(compositeCompletionPolicy);

                    repeatTemplate.iterate(new RepeatCallback() {
                        @Override
                        public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                            log.info("repeatTemplate is testing");

                            return RepeatStatus.CONTINUABLE;
                        }
                    });

                    return item;
                }
            })
            .writer((items) -> {
                items.forEach(i -> log.info("item = {}", i));
            })
            .build();
    }

}
