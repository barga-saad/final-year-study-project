package data.saad.customerapi.controller;

import data.saad.customerapi.exception.ResourceNotFoundException;
import data.saad.customerapi.model.Customer;
import data.saad.customerapi.repository.CustomerRepository;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/customerapi")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/customer")
    public List<Customer> getAllCustomers(){

        return customerRepository.findAll();
    }

    @GetMapping("/customer/{idCustomer}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer idCustomer){
       Customer customer = customerRepository.findById(idCustomer).orElseThrow(() -> new ResourceNotFoundException("Customer not exist with id:" + idCustomer));
       return ResponseEntity.ok(customer);
    }



}
