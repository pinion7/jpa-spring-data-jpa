package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

// JpaRepository를 상속하면 @Repository 어노테이션 없어도 컴포넌트 스캔으로 인식함
public interface TeamRepository extends JpaRepository<Team, Long> {
}
