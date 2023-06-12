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
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
public class PartitioningConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job PartitioningJob() {
        return jobBuilderFactory.get("PartitioningJob")
            .incrementer(new RunIdIncrementer())
            .listener(new StopWatchJobListener())
            .start(masterStep())
            .build();
    }

    @Bean
    public Step masterStep() {
        return stepBuilderFactory.get("masterStep")
            .partitioner(slaveStep().getName(), partitioner())
            .step(slaveStep())
            .gridSize(4)
            .taskExecutor(taskExecutor())
            .build();
    }

    private Partitioner partitioner() {
        ColumnRangePartitioner partitioner = new ColumnRangePartitioner();
        partitioner.setTable("customer");
        partitioner.setColumn("id");
        partitioner.setDataSource(dataSource);
        return partitioner;
    }

    @Bean
    public Step slaveStep() {
        return stepBuilderFactory.get("slaveStep")
            .allowStartIfComplete(true)
            .<Customer, Customer>chunk(10)
            .listener(new MultiThreadedReadListener())
            .reader(partitioningItemReader(null, null))
            .listener(new MultiThreadedProcessListener())
            .processor(partitioningItemProcessor())
            .listener(new MultiThreadedWriteListener())
            .writer(partitioningItemWriter())
            .build();
    }

    @Bean
    @StepScope
    public ItemReader<Customer> partitioningItemReader(
        @Value("#{stepExecutionContext['minValue']}") Long minValue,
        @Value("#{stepExecutionContext['maxValue']}") Long maxValue
    ) {
        log.info("reading - min = {}, max = {}", minValue, maxValue);
        return new JdbcPagingItemReaderBuilder<Customer>()
            .dataSource(dataSource)
            .name("multiThreadedPagingItemReader")
            .fetchSize(300)
            .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
            .queryProvider(createQueryProvider(minValue, maxValue))
            .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Customer, Customer> partitioningItemProcessor() {
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
    @StepScope
    public ItemWriter<Customer> partitioningItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer>()
            .dataSource(dataSource)
            .sql("insert into customer2 values (:id, :name, :age, :year)")
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .build();
    }

    private TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setThreadNamePrefix("Async");
        return executor;
    }

    private PagingQueryProvider createQueryProvider(Long minValue, Long maxValue) {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("id, name, age, year");
        factoryBean.setFromClause("from customer");
        factoryBean.setWhereClause("where id >= " + minValue + " and id <= " + maxValue);
        factoryBean.setSortKeys(Map.of("id", Order.ASCENDING));
        try {
            return factoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
