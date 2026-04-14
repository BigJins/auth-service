package allmart.authservice.application;

import allmart.authservice.application.provided.AddressManager;
import allmart.authservice.application.required.CustomerRepository;
import allmart.authservice.application.required.SavedAddressRepository;
import allmart.authservice.domain.customer.Customer;
import allmart.authservice.domain.customer.SavedAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService implements AddressManager {

    private final CustomerRepository customerRepository;
    private final SavedAddressRepository savedAddressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SavedAddress> findAll(Long customerId) {
        return savedAddressRepository.findAllByCustomer_CustomerId(customerId);
    }

    @Override
    @Transactional
    public SavedAddress add(Long customerId, String zipCode, String roadAddress,
                             String detailAddress, String label, boolean isDefault) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("소비자를 찾을 수 없습니다."));

        if (isDefault) {
            // 기존 기본 주소 해제
            savedAddressRepository.findAllByCustomer_CustomerId(customerId)
                    .forEach(SavedAddress::unsetDefault);
        }

        return savedAddressRepository.save(
                SavedAddress.create(customer, zipCode, roadAddress, detailAddress, label, isDefault)
        );
    }

    @Override
    @Transactional
    public SavedAddress update(Long customerId, Long addressId, String zipCode, String roadAddress,
                                String detailAddress, String label, boolean isDefault) {
        SavedAddress address = getAddressOwnedBy(customerId, addressId);

        if (isDefault) {
            savedAddressRepository.findAllByCustomer_CustomerId(customerId)
                    .forEach(SavedAddress::unsetDefault);
        }

        address.update(zipCode, roadAddress, detailAddress, label, isDefault);
        return savedAddressRepository.save(address);
    }

    @Override
    @Transactional
    public void delete(Long customerId, Long addressId) {
        SavedAddress address = getAddressOwnedBy(customerId, addressId);
        savedAddressRepository.delete(address);
    }

    private SavedAddress getAddressOwnedBy(Long customerId, Long addressId) {
        SavedAddress address = savedAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));
        if (!address.getCustomer().getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("본인의 주소만 수정할 수 있습니다.");
        }
        return address;
    }
}
