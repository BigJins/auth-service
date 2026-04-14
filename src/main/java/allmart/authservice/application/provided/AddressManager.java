package allmart.authservice.application.provided;

import allmart.authservice.domain.customer.SavedAddress;

import java.util.List;

public interface AddressManager {
    List<SavedAddress> findAll(Long customerId);
    SavedAddress add(Long customerId, String zipCode, String roadAddress, String detailAddress, String label, boolean isDefault);
    SavedAddress update(Long customerId, Long addressId, String zipCode, String roadAddress, String detailAddress, String label, boolean isDefault);
    void delete(Long customerId, Long addressId);
}