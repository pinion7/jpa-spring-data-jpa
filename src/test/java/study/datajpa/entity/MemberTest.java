package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    
    @PersistenceContext
    EntityManager em;
    
    @Test
    void testEntity() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 15, teamA);
        Member member3 = new Member("member3", 20, teamB);
        Member member4 = new Member("member4", 25, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // flush로 강제 db 인서트 쿼리를 날리고, clear로 영속성 컨텍스트를 날려버림
        em.flush();
        em.clear();
        
        //then
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team  = " + member.getTeam());
        }
    }
    
}