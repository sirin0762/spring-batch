package user.sirin.job.listenter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import user.sirin.job.domain.Customer;

@Slf4j
public class MultiThreadedProcessListener implements ItemProcessListener<Customer, Customer> {

    @Override
    public void beforeProcess(Customer item) {

    }

    @Override
    public void afterProcess(Customer item, Customer result) {
        log.info("Thread  name : {}" + " / " + "process item : {}", Thread.currentThread().getName(), item.getId());
    }

    @Override
    public void onProcessError(Customer item, Exception e) {

    }

}
