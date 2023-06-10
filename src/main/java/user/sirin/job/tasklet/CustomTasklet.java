package user.sirin.job.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class CustomTasklet implements Tasklet {

    private int sum = 0;
    private final Object lock = new Object();

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        synchronized(lock) {
            for (int i = 0; i < 100000; i++) {
                sum++;
            }
        }
        log.info("Thread name : {}, Step name : {}, sum : {}",
            Thread.currentThread().getName(),
            contribution.getStepExecution().getStepName(),
            sum
        );

        return RepeatStatus.FINISHED;
    }

}
