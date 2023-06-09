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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
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

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MultiThreadedItemConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job MultiThreadedItemJob() {
        return jobBuilderFactory.get("MultiThreadedItemJob")
            .incrementer(new RunIdIncrementer())
            .listener(new StopWatchJobListener())
            .start(multiThreadedItemStep())
            .build();
    }

    @Bean
    public Step multiThreadedItemStep() {
        return stepBuilderFactory.get("syncItemStep")
            .allowStartIfComplete(true)
            .<Customer, Customer>chunk(100)
            .reader(multiThreadedPagingItemReader())
            .listener(new MultiThreadedReadListener())
            .processor(multiThreadedItemProcessor())
            .listener(new MultiThreadedProcessListener())
            .writer(multiThreadedItemWriter())
            .listener(new MultiThreadedWriteListener())
            // taskExecutor를 통해 MultiThread-step으로 작동
            .taskExecutor(taskExecutor())
            .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Thread 갯수 설정
        executor.setCorePoolSize(4);
        // Thread Max 갯수 설정
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("Async");
        return executor;
    }


    @Bean
    public ItemReader<Customer> multiThreadedPagingItemReader() {
        return new JdbcPagingItemReaderBuilder<Customer>()
            .dataSource(dataSource)
            .name("multiThreadedPagingItemReader")
            .fetchSize(300)
            .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
            .queryProvider(createQueryProvider())
            .build();
    }

    @Bean
    public ItemProcessor<Customer, Customer> multiThreadedItemProcessor() {
        return item -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Customer(item.getId(), item.getName().toUpperCase(), item.getAge(), item.getYear());
        };
    }

    @Bean
    public ItemWriter<Customer> multiThreadedItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer>()
            .dataSource(dataSource)
            .sql("insert into customer2 values (:id, :name, :age, :year)")
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .build();
    }



    private PagingQueryProvider createQueryProvider() {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("id, name, age, year");
        factoryBean.setFromClause("from customer");
        factoryBean.setSortKeys(Map.of("id", Order.ASCENDING));
        try {
            return factoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
