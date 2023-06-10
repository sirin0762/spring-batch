package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import user.sirin.job.domain.Customer;
import user.sirin.job.listenter.MultiThreadedProcessListener;
import user.sirin.job.listenter.MultiThreadedReadListener;
import user.sirin.job.listenter.MultiThreadedWriteListener;
import user.sirin.job.listenter.StopWatchJobListener;
import user.sirin.job.tasklet.CustomTasklet;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ParallelStepsConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ApplicationContext applicationContext;

    @Bean
    public Job ParallelStepsJob() {
        return jobBuilderFactory.get("ParallelStepsJob")
            .incrementer(new RunIdIncrementer())
            .listener(new StopWatchJobListener())
            .start(flow1())
            .split(applicationContext.getBean("taskExecutor", TaskExecutor.class)).add(flow2())
            .end()
            .build();
    }

    private Flow flow1() {

        TaskletStep step1 = stepBuilderFactory.get("step1")
            .tasklet(tasklet())
            .build();

        return new FlowBuilder<Flow>("flow1")
            .start(step1)
            .build();
    }

    private Flow flow2() {

        TaskletStep step2 = stepBuilderFactory.get("step2")
            .tasklet(tasklet())
            .build();

        TaskletStep step3 = stepBuilderFactory.get("step3")
            .tasklet(tasklet())
            .build();

        return new FlowBuilder<Flow>("flow1")
            .start(step2)
            .next(step3)
            .build();
    }

    @Bean
    public Tasklet tasklet() {
        return new CustomTasklet();
    }
}
