package user.sirin.job.itemReader.flatFile;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import user.sirin.job.domain.Customer;

import java.util.Objects;

public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {

    @Override
    public Customer mapFieldSet(FieldSet fs) throws BindException {
        if (Objects.isNull(fs)) return null;

        Customer customer = new Customer();
        customer.setName(fs.readString("name"));
        customer.setAge(fs.readInt("age"));
        customer.setYear(fs.readString("year"));

        return customer;
    }

}
