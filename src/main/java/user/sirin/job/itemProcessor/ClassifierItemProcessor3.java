package user.sirin.job.itemProcessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import user.sirin.job.domain.ProcessorInfo;

@Slf4j
public class ClassifierItemProcessor3 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {

    @Override
    public ProcessorInfo process(ProcessorInfo item) throws Exception {
        log.info("ClassifierItemProcessor3 invoke");
        return item;
    }

}
