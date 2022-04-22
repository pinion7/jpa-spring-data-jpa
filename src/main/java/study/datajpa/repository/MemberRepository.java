package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

// JpaRepository<엔티티, pk_id의 타입> 넣음 됨
public interface MemberRepository extends JpaRepository<Member, Long> {
}
