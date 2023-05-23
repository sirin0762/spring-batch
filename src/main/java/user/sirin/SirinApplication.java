package user.sirin;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SirinApplication {

    public static void main(String[] args) {
        SpringApplication.run(SirinApplication.class, args);
    }

}
