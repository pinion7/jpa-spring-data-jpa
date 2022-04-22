package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;



    @Test
    void testMember() {
        // given
        Member member = new Member("memberA", 10);
        Member savedMember = memberJpaRepository.save(member);

        // when
        Member findMember = memberJpaRepository.find(savedMember.getId());

        //then
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); // 이게 중요 (같은 트랜잭션 안에선 영속성 컨텍스트의 동일성을 보장 -> 저장 대상이나 조회대상이나 같기 때문에 이를 보장!)
    }

    @Test
    void basicCRUD() {
        // given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when1: 단건 조회, 다건 조회, 카운트 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        List<Member> all = memberJpaRepository.findAll();
        long count = memberJpaRepository.count();

        // then1
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);
        assertThat(all.size()).isEqualTo(2);
        assertThat(count).isEqualTo(2);

        // when2: 업데이트 검증
        // find 단건하면 그냥 영속성컨텍스트에서 꺼내버려서 업데이트 쿼리 나가기도 전에 아래 검증로직이 발동함. 그래서 다건으로 조회해서 검증 진행
        findMember1.setUsername("updatedMember");
        Member updatedMember = memberJpaRepository.findAll().get(0);
        // then2
        assertThat(findMember1.getUsername()).isEqualTo(updatedMember.getUsername());

        // when3: 삭제, 새로운 카운트 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);
        long count2 = memberJpaRepository.count();

        // then3
        assertThat(count2).isEqualTo(0);

    }
    

}