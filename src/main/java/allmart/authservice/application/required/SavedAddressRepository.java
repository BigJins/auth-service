package allmart.authservice.application.required;

import allmart.authservice.domain.customer.SavedAddress;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface SavedAddressRepository extends Repository<SavedAddress, Long> {
    SavedAddress save(SavedAddress address);
    Optional<SavedAddress> findById(Long id);
    List<SavedAddress> findAllByCustomer_CustomerId(Long customerId);
    void delete(SavedAddress address);
}
