package user.sirin.job.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {

    private Long id;
    private String name;
    private int age;
    private String year;

    public Customer(String name) {
        this.name = name;
    }

}
