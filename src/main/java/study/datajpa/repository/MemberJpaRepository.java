package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

// update는 필요가없음 기본적으로 객체만 수정해도 변경감지를 통해 트랜잭션 커밋을 통해 반영함.
@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        //JPQL
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member); // null일 수도 아닐 수도 있음을 나타내기위하 옵셔널로 감싸는 것
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult(); // 카운트는 숫자고 단건이라 싱글result로 반환해야 함
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
