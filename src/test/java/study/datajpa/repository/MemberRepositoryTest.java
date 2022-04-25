package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("1-1. 스프링 데이터 JPA - 기본 내장 메소드 활용")
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
    @DisplayName("1-2. 스프링 데이터 JPA - 기본 내장 메소드 활용한 CRUD")
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

    @Test
    @DisplayName("2-1. 스프링 데이터 JPA - 이름 따라 쿼리생성하는 쿼리 메소드 선언 및 사용")
    void findByUsernameAndAgeGreaterThan() {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        // then
        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
        assertThat(members.get(0).getAge()).isEqualTo(20);
        assertThat(members.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("2-2. 스프링 데이터 JPA - 이름 따라 쿼리생성하는 쿼리 메소드 선언 및 사용2")
    void findHellBy() {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        Member member3 = new Member("CCC", 20);
        Member member4 = new Member("DDD", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        // when
        List<Member> helloBy = memberRepository.findHelloBy();
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy();

        // then
        assertThat(helloBy.size()).isEqualTo(4);
        assertThat(top3HelloBy.size()).isEqualTo(3);
    }

    // namedQuery 기법
    @Test
    @DisplayName("3. 스프링 데이터 JPA - namedQuery 기법")
    void namedQuery() {
        // given
        Member member1 = new Member("AAA", 10);
        memberRepository.save(member1);

        // when
        List<Member> members = memberRepository.findByUser("AAA");

        //then
        assertThat(members.get(0)).isEqualTo(member1);
        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
    }

    @Test
    @DisplayName("4-1. 스프링 데이터 JPA - @Query에 JPQL 적용한 기법 1")
    void testQuery() {
        // given
        Member member1 = new Member("AAA", 10);
        memberRepository.save(member1);

        // when
        List<Member> members = memberRepository.findUser("AAA", 10);

        //then
        assertThat(members.get(0)).isEqualTo(member1);
        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
        assertThat(members.get(0).getAge()).isEqualTo(10);
    }

    // @Query - 단순히 값 하나만 셀렉트하여 조회하는 기법 (username안 셀렉트해서 String들만 반환하게끔 하는 예시)
    @Test
    @DisplayName("4-2. 스프링 데이터 JPA - @Query에 JPQL 적용: 단순히 값하나 셀렉트 조회 기법")
    void findUsernameList() {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<String> members = memberRepository.findUsernameList();

        //then
        assertThat(members.get(0)).isEqualTo("AAA");
        assertThat(members.get(1)).isEqualTo("BBB");
        assertThat(members.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("4-3. 스프링 데이터 JPA - @Query에 JPQL 적용:  DTO 조회 기법")
    void findMemberDto() {
        // given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10, team);
        Member member2 = new Member("BBB", 20, team);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<MemberDto> memberDto = memberRepository.findMemberDto();
        MemberDto findMemberDto1 = memberDto.get(0);
        MemberDto findMemberDto2 = memberDto.get(1);

        //then
        assertThat(memberDto.size()).isEqualTo(2);
        assertThat(findMemberDto1.getUsername()).isEqualTo("AAA");
        assertThat(findMemberDto1.getId()).isEqualTo(member1.getId());
        assertThat(findMemberDto1.getTeamName()).isEqualTo("teamA");
        assertThat(findMemberDto2.getUsername()).isEqualTo("BBB");
        assertThat(findMemberDto2.getId()).isEqualTo(member2.getId());
        assertThat(findMemberDto2.getTeamName()).isEqualTo("teamA");
    }

    @Test
    @DisplayName("4-4. 스프링 데이터 JPA - @Query에 JPQL 적용:  컬렉션 파라미터 바인딩 기법")
    void findByNames() {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        //then
        assertThat(members.get(0)).isEqualTo(member1);
        assertThat(members.get(1)).isEqualTo(member2);
        assertThat(members.size()).isEqualTo(2);
    }


    @Test
    @DisplayName("5. 스프링 데이터 JPA - 유연한 반환 타입")
    void returnType() {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findListByUsername("AAA"); // 컬렉션 (다건)
        Member findMember = memberRepository.findMemberByUsername("AAA"); // 단건
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("AAA"); // 단건

        List<Member> results = memberRepository.findListByUsername("ㄴㅇㄹㄴㅁㄹ"); // 컬렉션 (다건) - 없는값 조회
        Member resultOne = memberRepository.findMemberByUsername("ㅁㅇㄴㄹㅁㄴ"); // 단건 - 없는값 조회

        //then
        assertThat(members.get(0)).isEqualTo(member1);
        assertThat(findMember).isEqualTo(member1);
        assertThat(optionalMember.get()).isEqualTo(member1);

        assertThat(results.size()).isEqualTo(0); // 컬렉션 (다건) 조회는 없는 값을 조회하면 null이 아닌 빈배열을 반환한다는 점 주의!
        assertThat(resultOne).isEqualTo(null); // 단건 조회는 null 반환
        // 차이점 기억하기 -> 스프링 데이터 JPA는 단건 조회했을 때 없으면 null 반환 vs 그냥 JPA는 No어쩌고 에러반환
        // 개발자한테는 null주는게 편하지!
    }

    @Test
    @DisplayName("6-1. 스프링 데이터 JPA - 페이징 Page 활용 방식")
    void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        // pageable 조건을 만들어서 파람으로 넘겨야함
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); // 토탈카운트 쿼리도 같이날림 와우!
        List<Member> content = page.getContent(); // 내부 데이터를 꺼내고싶으면 getContent()쓰면 됨
        long totalElements = page.getTotalElements();// 토탈카운트 꺼내는 방법
        int number = page.getNumber();// 페이지 넘버도 꺼낼 수 있음
        int totalPages = page.getTotalPages(); // 토탈페이지도 꺼낼 수가 있음 ㅋㅋㅋ 미친
        boolean first = page.isFirst(); // 이게 첫번째 페이지냐?를 검증한 결과도 꺼낼 수 있음
        boolean next = page.hasNext(); // 다음 페이지가 존재하냐?를 검증한 결과도 꺼낼 수 있음

        //then
        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(number).isEqualTo(0);
        assertThat(totalPages).isEqualTo(2);
        assertThat(first).isTrue();
        assertThat(next).isTrue();


        // 번외
        // Page<Member> page = memberRepository.findByAge(age, pageRequest); 이 로직에서 Page<Member>를 response로 날려도 될까?
        // 절대 안된다. 외부에 엔티티를 직접 노출하는 것은 좋지 못한 방식. 항상 DTO를 통해 반환타입을 명확히 컨트롤해서 보내줘야함
        // 아래는 변환하는 방법 예시
        Page<MemberDto> result = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        // -> 이건 반환해도 됨! Page 형태로 내보내도 괜찮음. 그 안에 들어 있는 스펙들 page관련 정보들도 json형태로 잘 나가기 때문에
        // @ResponseBody로 내보내면 아주 괜찮은 형태로 반환이 된다는 점 참고!
    }

    @Test
    @DisplayName("6-2. 스프링 데이터 JPA - 페이징 Slice 활용 방식")
    void slicePaging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member1", 20));
        memberRepository.save(new Member("member1", 30));
        memberRepository.save(new Member("member1", 40));
        memberRepository.save(new Member("member1", 50));

        String username = "member1";
        // pageable 조건을 만들어서 파람으로 넘겨야함
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "age"));

        // when
        // Slice 요청한 사이즈에서 + 1 추가 데이터를 받음 (가령 size가 3이면 본래 limit=3으로 쿼리가 가야되는데 +1 해서 limit=4로 날라간다는 것)
        Slice<Member> page = memberRepository.findByUsername(username, pageRequest); // Slice는 토탈카운트 안받음
        List<Member> content = page.getContent(); // 내부 데이터를 꺼내고싶으면 getContent()쓰면 됨
        int number = page.getNumber();// 현재가 더보기 페이지 몇번째인지도 꺼낼 수 있음
        boolean first = page.isFirst(); // 이게 첫번째 페이지냐?를 검증한 결과도 꺼낼 수 있음
        boolean next = page.hasNext(); // 다음 더보기페이지가 존재하냐?를 검증한 결과도 꺼낼 있음

//        long totalElements = page.getTotalElements();// 말했듯 Slice는 전체 카운트 가져오지 않음
//        int totalPages = page.getTotalPages(); // 말했듯 Slice는 전체 페이지수도 가져오지 않음

        //then
        assertThat(content.size()).isEqualTo(3);
        assertThat(number).isEqualTo(0);
        assertThat(first).isTrue();
        assertThat(next).isTrue();
    }

    @Test
    @DisplayName("7. 스프링 데이터 JPA - 벌크성 업데이트")
    void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        // 맹점이 있음!
        // 벌크성 update는 변경감지 방식과는 다르게 영속성 컨텍스트는 건드리지 않고 db에 바로 쿼리를 날려버린다는 문제가 있는 거임
        // 그래서 조회시에 영속성 컨텍스트가 비어있지 않으면, db에서 데이터를 끌어와 채우질 않음.
        // 즉, 일단 1차캐싱(영속성컨텍스트)으로 접근해서 데이터를 가져옴
        // 따라서 아래와 같은 로직이 선행되어야 함 (일단 flush로 db에 벌크성 쿼리를 반영한 뒤, clear로 영속성 컨텍스트를 비워버리는 것!)
        // 아래 로직 이후에 find 쿼리를 날리면, 영속성 컨텍스트가 비어있음을 확인하고 db로부터 실제 데이터를 가져옴과동시에 영속성컨텍스트에 채워줌
//        em.flush(); // 다시보니 여기선 flush도 필요가 없긴함. update는 그냥 db에 바로 쿼리 꽂아주는지라 불필요
//        em.clear(); // 클리어만 있어도 됨. 근데 스프링 데이터 jpa는 이마저도 생략할 수 있게 해줌 (벌크성 업데이트 메서드 @Modifying에 클리어 옵션 넣음 됨)

        // em.flush(), em.clear()를 주석으로 두면 아래 출력문에서 member5의 age는 41이 아니고 40임
        // 즉, db에 반영되긴했지만 영속성 컨텍스트는 건드리지 않기 때문에 그대로 이전상태에 머물러 있는 것!
        // 허나 두개를 기입해두면 정상적으로 41로 출력됨을 확인할 수 있음.
        List<Member> result = memberRepository.findByUser("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    @DisplayName("8. 스프링 데이터 JPA - @EntityGraph")
    void findMemberLazy() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        // 지연로딩 방식으로 인해, N + 1 문제가 발생함
//        List<Member> members = memberRepository.findAll(); // @EntityGraph 적용 전 일때!
//        for (Member member : members) {
//            System.out.println("member.name = " + member.getUsername());
//            // 아래는 일단 실제 데이터 가져오기 전이기 때문에 프록시 객체를 넣어둠 (데이터를 가져오지 않았기 때문에 실제 객체를 만들수 없는 상황)
//            System.out.println("member.team.class = " + member.getTeam().getClass());
//            System.out.println("member.team.name = " + member.getTeam().getName()); // 여기서처럼 실제 데이터를 가져오려할때 쿼리가 추가로 계속 나감
//
//        }

        //  N + 1 문제를 해결하기 위한 방법 -> fetch join
//        List<Member> membersByFetchJoin = memberRepository.findMemberFetchJoin(); // fetch join
        List<Member> membersByEntityGraph = memberRepository.findAll(); // @EntityGraph 적용!
        for (Member member : membersByEntityGraph) {
            System.out.println("member.name = " + member.getUsername()); // 여기서 fetch join으로 한방의 쿼리로 다가져옴
            // 아래에선 프록시가 아닌 실제 객체가 잡힘 (데이터를 넣어둘 실제 객체가 있다는 것!)
            System.out.println("member.team.class = " + member.getTeam().getClass());
            System.out.println("member.team.name = " + member.getTeam().getName()); // 그냥 한방에 다 잘불러왔기 때문에 편안하게 접근!
        }

        //then
    }

    @Test
    @DisplayName("9-1. 스프링 데이터 JPA - JPA Hint")
    void queryHint() {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성 컨텍스트의 상태를 db에 반영 (동기화 하는 것)
        em.clear();

        // when
//        Member findMember = memberRepository.findById(member1.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername("member1"); // 이걸로 조회하면 변경감지 막을 수 있음
        findMember.setUsername("member2");

        em.flush(); // 트랜잭션 커밋을 기다리든, 아니면 이걸 중간에 넣든 ReadOnly조회를 사용하면 업데이트 쿼리 실행안됨
        //then
    }


    @Test
    @DisplayName("9-2. 스프링 데이터 JPA - JPA Lock")
    void lock() {
        // given
        memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성 컨텍스트의 상태를 db에 반영 (동기화 하는 것)
        em.clear();

        // when
        // for update 쿼리가 조회 시 where 문 끝에 포함되서 나감
        List<Member> findMember = memberRepository.findLockByUsername("member1");
    }


    @Test
    @DisplayName("10. 스프링 데이터 JPA - 사용자 정의 레포지터리")
    void callCustom() {
        // given
        List<Member> result = memberRepository.findMemberCustom();

        // when

        //then
    }

    @Test
    @DisplayName("11. 스프링 데이터 JPA - Auditing")
    void jpaEventBaseEntity() throws InterruptedException {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member); // @PrePersist 적용 시점

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush(); //@PreUpdate 적용 시점
        em.clear();

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember.createdDate = " + findMember.getCreatedDate());
//        System.out.println("findMember.updatedDate = " + findMember.getUpdatedDate()); // JpaBaseEntity 방식
        System.out.println("findMember.lastModifiedDate = " + findMember.getLastModifiedDate()); // BaseEntity 방식
        System.out.println("findMember.createdBy = " + findMember.getCreatedBy()); // BaseEntity 방식
        System.out.println("findMember.lastModifiedBy = " + findMember.getLastModifiedBy()); // BaseEntity 방식

    }
}