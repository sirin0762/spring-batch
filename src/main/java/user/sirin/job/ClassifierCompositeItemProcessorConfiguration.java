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
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.sirin.job.domain.ProcessorInfo;
import user.sirin.job.itemProcessor.ClassifierItemProcessor1;
import user.sirin.job.itemProcessor.ClassifierItemProcessor2;
import user.sirin.job.itemProcessor.ClassifierItemProcessor3;
import user.sirin.job.itemProcessor.ProcessorClassifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class ClassifierCompositeItemProcessorConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job ClassifierCompositeItemProcessorJob() {
        return jobBuilderFactory.get("ClassifierCompositeItemProcessorJob")
            .incrementer(new RunIdIncrementer())
            .start(ClassifierCompositeItemProcessorStep1())
            .build();
    }

    @Bean
    public Step ClassifierCompositeItemProcessorStep1() {
        return stepBuilderFactory.get("ClassifierCompositeItemProcessorJob")
            .<ProcessorInfo, ProcessorInfo>chunk(5)
            .reader(
                new ItemReader<>() {
                    int i = 0;
                    @Override
                    public ProcessorInfo read() throws Exception {
                        i++;
                        ProcessorInfo processorInfo = ProcessorInfo.builder().id(i).build();
                        return i > 3 ? null : processorInfo;
                    }
                }
            )
            .processor(classifierCompositeItemProcessor())
            .writer((items) -> {
                items.forEach(i -> log.info("item = {}", i));
            })
            .build();
    }


    @Bean
    public ItemProcessor<? super ProcessorInfo, ? extends ProcessorInfo> classifierCompositeItemProcessor() {

        ClassifierCompositeItemProcessor<ProcessorInfo, ProcessorInfo> processor = new ClassifierCompositeItemProcessor<>();
        ProcessorClassifier<? super ProcessorInfo, ItemProcessor<?, ? extends ProcessorInfo>> classifier = new ProcessorClassifier<>();
        classifier.setProcessorMap(
            Map.of(
                1, new ClassifierItemProcessor1(),
                2, new ClassifierItemProcessor2(),
                3, new ClassifierItemProcessor3()
            )
        );
        processor.setClassifier(classifier);
        return processor;
    }

}
