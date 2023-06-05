package user.sirin.job.itemProcessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import user.sirin.job.domain.ProcessorInfo;

@Slf4j
public class ClassifierItemProcessor2 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {

    @Override
    public ProcessorInfo process(ProcessorInfo item) throws Exception {
        log.info("ClassifierItemProcessor2 invoke");
        return item;
    }

}
