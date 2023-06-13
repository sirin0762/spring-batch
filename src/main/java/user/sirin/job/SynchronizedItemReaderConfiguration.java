package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import user.sirin.job.domain.Customer;
import user.sirin.job.listenter.MultiThreadedProcessListener;
import user.sirin.job.listenter.MultiThreadedReadListener;
import user.sirin.job.listenter.MultiThreadedWriteListener;
import user.sirin.job.listenter.StopWatchJobListener;
import user.sirin.job.partitioner.ColumnRangePartitioner;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SynchronizedItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job SynchronizedItemReaderJob() {
        return jobBuilderFactory.get("SynchronizedItemReaderJob")
            .incrementer(new RunIdIncrementer())
            .listener(new StopWatchJobListener())
            .start(SynchronizedItemReaderStep())
            .build();
    }

    @Bean
    public Step SynchronizedItemReaderStep() {
        return stepBuilderFactory.get("SynchronizedItemReaderStep")
            .allowStartIfComplete(true)
            .<Customer, Customer>chunk(10)
            .listener(new MultiThreadedReadListener())
            .reader(SynchronizedItemReaderItemReader())
            .listener(new MultiThreadedProcessListener())
            .processor(SynchronizedItemReaderItemProcessor())
            .listener(new MultiThreadedWriteListener())
            .writer(SynchronizedItemReaderItemWriter())
            .taskExecutor(taskExecutor())
            .build();
    }

    public ItemReader<Customer> SynchronizedItemReaderItemReader() {
        SynchronizedItemStreamReader<Customer> itemStreamReader = new SynchronizedItemStreamReader<>();
        JdbcCursorItemReader<Customer> itemReader = new JdbcCursorItemReaderBuilder<Customer>()
            .dataSource(dataSource)
            .name("multiThreadedPagingItemReader")
            .fetchSize(10)
            .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
            .sql("SELECT id, name, age, year FROM customer")
            .build();

        itemStreamReader.setDelegate(itemReader);
        return itemStreamReader;
    }

    public ItemProcessor<Customer, Customer> SynchronizedItemReaderItemProcessor() {
        return item -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Customer(item.getId(), item.getName().toUpperCase(), item.getAge(), item.getYear());
        };
    }

    public ItemWriter<Customer> SynchronizedItemReaderItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("insert into customer2 values (:id, :name, :age, :year)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    private TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setThreadNamePrefix("Async");
        return executor;
    }

}
