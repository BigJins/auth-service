package allmart.authservice.application.required;

import allmart.authservice.domain.customer.Customer;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends Repository<Customer, Long> {
    Customer save(Customer customer);
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Customer> findAll();
}
