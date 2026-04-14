package allmart.authservice.adapter.webapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SavedAddressRequest(
        @NotBlank @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다.")
        String zipCode,

        @NotBlank @Size(max = 200)
        String roadAddress,

        @Size(max = 200)
        String detailAddress,

        @Size(max = 30)
        String label,        // "집", "회사" 등 별칭 (선택)

        boolean isDefault
) {}
