package user.sirin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.sirin.job.itemProcessor.CustomItemProcessor;
import user.sirin.job.itemReader.CustomItemReader;
import user.sirin.job.domain.Customer;
import user.sirin.job.itemWriter.CustomItemWriter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ItemReaderProcessorWriterConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemReaderProcessorWriterJob() {
        return jobBuilderFactory.get("itemReaderProcessorWriterJob")
            .start(itemReaderProcessorWriterStep1())
            .build();

    }

    @Bean
    public Step itemReaderProcessorWriterStep1() {
        return stepBuilderFactory.get("itemReaderProcessorWriterStep1")
            .<Customer, Customer> chunk(3)
            .reader(new CustomItemReader(List.of(
                new Customer("jay"),
                new Customer("riven"),
                new Customer("garen"),
                new Customer("mew"),
                new Customer("katarina"),
                new Customer("sirin")
            )))
            .processor(new CustomItemProcessor())
            .writer(new CustomItemWriter())
            .build();
    }

}
