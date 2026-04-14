package allmart.authservice.application.required;

import allmart.authservice.domain.member.Member;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface MemberRepository extends Repository<Member, Long> {
    Member save(Member member);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
}