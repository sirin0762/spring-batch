package user.sirin.job.listenter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemWriteListener;
import user.sirin.job.domain.Customer;

import java.util.List;

@Slf4j
public class MultiThreadedWriteListener implements ItemWriteListener<Customer> {


    @Override
    public void beforeWrite(List<? extends Customer> items) {

    }

    @Override
    public void afterWrite(List<? extends Customer> items) {
        log.info("Thread  name : {}" + " / " + "write size : {}", Thread.currentThread().getName(), items.size());
    }

    @Override
    public void onWriteError(Exception exception, List<? extends Customer> items) {

    }

}
