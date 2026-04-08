package allmart.authservice.domain.customer;

import allmart.authservice.config.SnowflakeGenerated;
import allmart.authservice.domain.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 소비자가 저장해 둔 배송지 목록.
 * Customer당 여러 개 보유 가능, 주문 시 원하는 주소 선택 → DeliverySnapshot으로 스냅샷.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedAddress extends AbstractEntity {

    @Id
    @SnowflakeGenerated
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;

    private String zipCode;

    private String roadAddress;

    private String detailAddress;

    /** 주소 별칭 (예: "집", "회사") — nullable */
    private String label;

    private boolean isDefault = false;

    public static SavedAddress create(Customer customer, String zipCode, String roadAddress,
                                      String detailAddress, String label, boolean isDefault) {
        SavedAddress address = new SavedAddress();
        address.customer = customer;
        address.zipCode = zipCode.trim();
        address.roadAddress = roadAddress.trim();
        address.detailAddress = detailAddress.trim();
        address.label = (label != null && !label.isBlank()) ? label.trim() : null;
        address.isDefault = isDefault;
        return address;
    }

    public void update(String zipCode, String roadAddress, String detailAddress, String label, boolean isDefault) {
        this.zipCode = zipCode.trim();
        this.roadAddress = roadAddress.trim();
        this.detailAddress = detailAddress.trim();
        this.label = (label != null && !label.isBlank()) ? label.trim() : null;
        this.isDefault = isDefault;
    }

    public void unsetDefault() {
        this.isDefault = false;
    }
}
