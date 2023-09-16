package org.sylwia.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE c.email=?1")
    Optional<Customer> findCustomerByEmail(String email);

}
