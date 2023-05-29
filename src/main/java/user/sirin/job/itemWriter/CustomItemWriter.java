package user.sirin.job.itemWriter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import user.sirin.job.domain.Customer;

import java.util.List;

@Slf4j
public class CustomItemWriter implements ItemWriter<Customer> {

    @Override
    public void write(List<? extends Customer> items) throws Exception {
        items.forEach((i) -> log.info("name = {}", i.getName()));
    }

}
