package user.sirin.job.itemProcessor;

import org.springframework.batch.item.ItemProcessor;
import user.sirin.job.domain.Customer;

public class CustomItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {
        if(customer.getName().equals("sirin")) {
            return null;
        }
        customer.setName(customer.getName().toUpperCase());
        return customer;
    }

}
