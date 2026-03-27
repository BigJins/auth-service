package allmart.authservice.application.required;

import allmart.authservice.domain.customer.Customer;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface CustomerRepository extends Repository<Customer, Long> {
    Customer save(Customer customer);
    Optional<Customer> findByKakaoId(String kakaoId);
}