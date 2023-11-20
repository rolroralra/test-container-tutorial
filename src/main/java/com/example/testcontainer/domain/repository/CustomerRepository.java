package com.example.testcontainer.domain.repository;

import com.example.testcontainer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {}
