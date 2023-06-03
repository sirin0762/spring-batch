package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import user.sirin.job.domain.Customer;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JdbcPagingItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job jdbcPagingItemReaderJob() throws Exception {
        return jobBuilderFactory.get("jdbcPagingItemReaderJob")
            .incrementer(new RunIdIncrementer())
            .start(jdbcPagingItemReaderStep1())
            .next(jdbcPagingItemReaderStep2())
            .build();
    }

    @Bean
    public Step jdbcPagingItemReaderStep1() throws Exception {
        return stepBuilderFactory.get("jdbcPagingItemReaderStep1")
            .<Customer, Customer>chunk(5)
            .reader(jdbcPagingItemReader())
            .writer((items) -> {
                items.forEach(i -> log.info("item = {}", i));
            })
            .build();
    }

    @Bean
    public Step jdbcPagingItemReaderStep2() {
        return stepBuilderFactory.get("jdbcPagingItemReaderStep2")
            .tasklet(((contribution, chunkContext) -> {
                log.info("jdbcPagingItemReaderStep2 start");
                return RepeatStatus.FINISHED;
            }))
            .build();
    }

    @Bean
    public ItemReader<Customer> jdbcPagingItemReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<Customer>()
            .name("jdbcPagingItemReader")
            .dataSource(dataSource)
            .fetchSize(10)
            .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
            .queryProvider(createQueryProvider())
            .parameterValues(Map.of("name", "A%"))
            .build();
    }

    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("id, name, age, year");
        factoryBean.setFromClause("from customer");
        factoryBean.setWhereClause("where name like :name");
        factoryBean.setSortKeys(Map.of("id", Order.ASCENDING));
        return factoryBean.getObject();
    }

}
