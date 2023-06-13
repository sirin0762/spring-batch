package user.sirin.Job;

import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import user.sirin.job.MultiThreadedItemConfiguration;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest(classes = {MultiThreadedItemConfiguration.class, TestBatchConfig.class})
public class SimpleJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @After
    public void clear() {
        jdbcTemplate.execute("DELETE FROM customer2");
    }

    @Test
    public void simpleJob_test() throws Exception {
        // given
        JobParameters jobParameter = new JobParametersBuilder()
            .addString("name", "user1")
            .addLong("date", new Date().getTime())
            .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameter);
        StepExecution stepExecutions = ((List<StepExecution>) jobExecution.getStepExecutions()).get(0);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        // null 확인 떄매 한번 더 돔
        assertThat(stepExecutions.getCommitCount()).isEqualTo(11);
        assertThat(stepExecutions.getReadCount()).isEqualTo(100);
        assertThat(stepExecutions.getWriteCount()).isEqualTo(100);

    }
}
