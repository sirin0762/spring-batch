package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import user.sirin.job.domain.Customer;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.Future;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AsyncItemConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job asyncItemJob() {
        return jobBuilderFactory.get("asyncItemJob")
            .start(syncItemStep())
            .next(asyncItemStep())
            .build();
    }

    @Bean
    public Step syncItemStep() {
        return stepBuilderFactory.get("syncItemStep")
            .<Customer, Customer>chunk(100)
            .reader(pagingItemReader())
            .processor(customeItemProcessor())
            .writer(customItemWriter())
            .build();
    }

    @Bean
    public Step asyncItemStep() {
        return stepBuilderFactory.get("asyncItemStep")
            .<Customer, Future<Customer>>chunk(100)
            .reader(pagingItemReader())
            .processor(asyncItemProcessor())
            .writer(asyncItemWriter())
            .build();
    }

    @Bean
    public AsyncItemWriter<Customer> asyncItemWriter() {
        AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(customItemWriter());
        return asyncItemWriter;
    }

    @Bean
    public AsyncItemProcessor<Customer, Customer> asyncItemProcessor() {
        AsyncItemProcessor<Customer, Customer> asyncItemProcessor = new AsyncItemProcessor<>();

        asyncItemProcessor.setDelegate(customeItemProcessor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());

        return asyncItemProcessor;
    }

    @Bean
    public ItemReader<Customer> pagingItemReader() {
        return new JdbcPagingItemReaderBuilder<Customer>()
            .dataSource(dataSource)
            .fetchSize(300)
            .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
            .queryProvider(createQueryProvider())
            .build();
    }

    @Bean
    public ItemProcessor<Customer, Customer> customeItemProcessor() {
        return item -> new Customer(item.getId(), item.getName().toUpperCase(), item.getAge(), item.getYear());
    }

    @Bean
    public ItemWriter<Customer> customItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer>()
            .dataSource(dataSource)
            .sql("insert into customer2 values (:id, :firstName, :lastName), :birthdate")
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
