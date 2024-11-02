package data.saad.customerapi;

import data.saad.customerapi.repository.CustomerRepository;
import org.hibernate.jdbc.Expectation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CustomerApiApplication  {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApiApplication.class, args);

    }
}
