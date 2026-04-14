package allmart.authservice.adapter.webapi.dto;

import allmart.authservice.domain.customer.SavedAddress;

public record SavedAddressResponse(
        Long id,
        String zipCode,
        String roadAddress,
        String detailAddress,
        String label,
        boolean isDefault
) {
    public static SavedAddressResponse of(SavedAddress address) {
        return new SavedAddressResponse(
                address.getId(),
                address.getZipCode(),
                address.getRoadAddress(),
                address.getDetailAddress(),
                address.getLabel(),
                address.isDefault()
        );
    }
}
