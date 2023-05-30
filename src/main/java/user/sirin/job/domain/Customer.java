package user.sirin.job.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Setter
@Getter
public class Customer {

    private String name;
    private int age;
    private String year;

    public Customer(String name) {
        this.name = name;
    }

}
