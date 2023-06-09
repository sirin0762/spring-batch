package user.sirin.job.listenter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import user.sirin.job.domain.Customer;

@Slf4j
public class MultiThreadedReadListener implements ItemReadListener<Customer> {

    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(Customer item) {
        log.info("Thread name : {}" + " / " + "read item : {}", Thread.currentThread().getName(), item.getId());
    }

    @Override
    public void onReadError(Exception ex) {

    }

}
