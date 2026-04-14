package allmart.authservice.adapter.webapi;

import allmart.authservice.adapter.webapi.dto.SavedAddressRequest;
import allmart.authservice.adapter.webapi.dto.SavedAddressResponse;
import allmart.authservice.application.provided.AddressManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 소비자 저장 주소 API
 * Gateway가 JWT uid claim → X-User-Id 헤더로 주입 (customerId)
 *
 * GET    /auth/customers/addresses          → 내 주소 목록
 * POST   /auth/customers/addresses          → 주소 추가
 * PUT    /auth/customers/addresses/{id}     → 주소 수정
 * DELETE /auth/customers/addresses/{id}     → 주소 삭제
 */
@RestController
@RequestMapping("/auth/customers/addresses")
@RequiredArgsConstructor
public class AddressApi {

    private final AddressManager addressManager;

    @GetMapping
    public List<SavedAddressResponse> findAll(
            @RequestHeader("X-User-Id") Long customerId) {
        return addressManager.findAll(customerId).stream()
                .map(SavedAddressResponse::of)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavedAddressResponse add(
            @RequestHeader("X-User-Id") Long customerId,
            @Valid @RequestBody SavedAddressRequest request) {
        return SavedAddressResponse.of(
                addressManager.add(customerId, request.zipCode(), request.roadAddress(),
                        request.detailAddress(), request.label(), request.isDefault())
        );
    }

    @PutMapping("/{id}")
    public SavedAddressResponse update(
            @RequestHeader("X-User-Id") Long customerId,
            @PathVariable Long id,
            @Valid @RequestBody SavedAddressRequest request) {
        return SavedAddressResponse.of(
                addressManager.update(customerId, id, request.zipCode(), request.roadAddress(),
                        request.detailAddress(), request.label(), request.isDefault())
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader("X-User-Id") Long customerId,
            @PathVariable Long id) {
        addressManager.delete(customerId, id);
    }
}
