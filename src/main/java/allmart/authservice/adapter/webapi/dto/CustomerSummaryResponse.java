package allmart.authservice.adapter.webapi.dto;

import allmart.authservice.domain.customer.Customer;

public record CustomerSummaryResponse(
        Long customerId,
        String email,
        String name,
        String loginType
) {
    public static CustomerSummaryResponse of(Customer customer) {
        return new CustomerSummaryResponse(
                customer.getCustomerId(),
                customer.getEmail(),
                customer.getName(),
                customer.getLoginType().name()
        );
    }
}
