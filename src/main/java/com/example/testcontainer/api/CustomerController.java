package com.example.testcontainer.api;

import com.example.testcontainer.domain.Customer;
import com.example.testcontainer.domain.repository.CustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerRepository customerRepository;

    @GetMapping("/api/v1/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
