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
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    void testMember() {
        // given
        Member member = new Member("memberA", 15);
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        //then
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void basicCRUD() {
        // given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when1: 단건 조회, 다건 조회, 카운트 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        List<Member> all = memberRepository.findAll();
        long count = memberRepository.count();

        // then1
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);
        assertThat(all.size()).isEqualTo(2);
        assertThat(count).isEqualTo(2);

        // when2: 업데이트 검증
        // find 단건하면 그냥 영속성컨텍스트에서 꺼내버려서 업데이트 쿼리 나가기도 전에 아래 검증로직이 발동함. 그래서 다건으로 조회해서 검증 진행
        findMember1.setUsername("updatedMember");
        Member updatedMember = memberRepository.findAll().get(0);
        // then2
        assertThat(findMember1.getUsername()).isEqualTo(updatedMember.getUsername());

        // when3: 삭제, 새로운 카운트 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long count2 = memberRepository.count();

        // then3
        assertThat(count2).isEqualTo(0);

    }

}