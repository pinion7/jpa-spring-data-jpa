package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

/**
 * 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성
 * 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음
 * 아래처럼 다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶다면?
 *      - JPA 직접 사용( EntityManager )
 *      - 스프링 JDBC Template 사용
 *      - MyBatis 사용
 *      - 데이터베이스 커넥션 직접 사용 등등...
 *      - Querydsl 사용
 * extends 뒤에 사용자 정의 커스텀 interface를 하나 만들어서 추가하면 됨. (여기에선 MemberRepositoryCustom 인터페이스가 해당)
 * 구현체는 따로 이 커스텀 인터페이스를 상속받은 클래스를 생성하여 구현 (여기에선 MemberRepositoryImpl 클래스에 해당)
 * 대신 구현체 이름 짓는 규칙이 있음 JpaRepository를 상속받은 인터페이스의 이름에 Impl을 붙여서 지어야함.
 * 즉, Jpa레포의 인터페이스가 MemberRepository 라면, 사용자 정의 구현체 이름은 MemberRepositoryImpl 이어야 함.
 * (신기한건 구현체는 위와 같은 규칙을 따라야 하지만, 사용자 정의 커스텀 interface 이름은 아무렇게나 지어도 됨!)
 */
// JpaRepository<엔티티, pk_id의 타입> 넣음 됨
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    /**
     * 1. 메소드 이름으로 쿼리 생성
     *
     * 1) 쿼리 메소드 필터 조건
     * 스프링 데이터 JPA 공식 문서 링크: (https://docs.spring.io/spring-data/jpa/docs/current/ reference/html/#jpa.query-methods.query-creation)
     *
     * 2) 스프링 데이터 JPA가 제공하는 쿼리 메소드 기능
     * 조회: find...By ,read...By ,query...By get...By,
     *      관련 링크: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/ #repositories.query-methods.query-creation
     *      예:) findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다.
     * COUNT: count...By 반환타입 long
     * EXISTS: exists...By 반환타입 boolean
     * 삭제: delete...By, remove...By 반환타입 long
     * DISTINCT: findDistinct, findMemberDistinctBy
     * LIMIT: findFirst3, findFirst, findTop, findTop3
     *      관련 링크: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/ #repositories.limit-query-result
     *
     * 3) 참고: 이 기능은 엔티티의 필드명이 변경되면 인터페이스에 정의한 메서드 이름도 꼭 함께 변경해야 한다. (그래서 조건이 2개를 초과하면 이름이 너무 길어져서 안쓰는걸 추천)
     * 그렇지 않으면 애플리케이션을 시작하는 시점에 오류가 발생한다.
     * 이렇게 애플리케이션 로딩 시점에 오류를 인지할 수 있는 것이 스프링 데이터 JPA의 매우 큰 장점이다.
     */
    // 예시 1-1)
    // JpaRepository는 구현체를 직접 만들지 않아도, 알아서 프록시 구현체를 만들고 거기에 이렇게 메소드 이름만으로 자동으로 쿼리를 생성해줌!
    // 이걸 쿼리 메소드 기능이라 부름! 스프링 데이터 JPA의 강력한 편의 기능!
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 예시 1-2)
    // 가령 By 뒤에 조건을 안넣으면 전체 조회로 인식함
    List<Member> findHelloBy();

    // 예시 1-3)
    // limit도 줄수가 있음
    List<Member> findTop3HelloBy();


    /**
     * 2. 메소드 이름으로 JPA NamedQuery 호출
     * 일단 NamedQuery기법은 실무에서 거의 안쓴다는 점 참고하길! 그래도 이런 기능이 있다는 것만 인식
     * NamedQuery를 JpaRepository를 상속받은 인터페이스에 적용할 수 있음 (장점: 애플리케이션 로딩 시점에 sql을 파싱해서 잘못된 sql 문법오류을 체킹해줌!)
     * 메소드 명은 아무거나 쓸 수는 있고, Member 엔티티에 NamedQuery에 명시한 이름을 기입해주면 됨.
     * 다만, NamedQuery에 명시한 이름을 기준으로 메서드를 먼저 찾아주는 작업을 함!
     * 즉 메서드 명이 경로명과 같으면, 경로를 기입하기 위해 추가하는 @Query(name = "Member.findByUser") -> 이 부분을 생략해줘도 됨.
     * 그리고 쿼리문에 :username 이런식으로 param을 받으면 여기에도 @Param("username") 라는 형태로 받아줘야함
     */
    @Query(name = "Member.findByUser") // 경로명이랑 메소드명이 같으면 생략 가능
    List<Member> findByUser(@Param("username") String username);


    /**
     * 3. @Query, 리포지토리 메소드에 쿼리 정의하기 (메서드에 JPQL 쿼리 작성)
     *
     * 그냥 쿼리를 직접 이 JpaRepository를 상속받은 인터페이스에서 작성하는 기법. -> NamedQuery를 안쓰는 이유임
     * 실행할 메서드에 정적 쿼리를 직접 작성하므로 이름 없는 Named 쿼리라 할 수 있음
     * JPA Named 쿼리처럼 애플리케이션 실행 시점에 문법 오류를 발견할 수 있음(매우 큰 장점!)
     * 참고: 실무에서는 메소드 이름으로 쿼리 생성 기능은 파라미터가 증가하면 메서드 이름이 매우 지저분해진다. 따라서 @Query 기능을 자주 사용하게 된다.
     *
     * 파라미터 바인딩 방식: @Param을 쓰는 방식을 뚯하며, 이름 기반의 방식을 써야함 (위치 기반은 순서가 바뀌면 큰 장애 발생)
     */
    // 대표 예시 1
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    // 추가 예시 1) 단순히 값 하나를 조회 (username이 스트링 타입이라 알아서 string타입을 담은 list가 타입변환 되어 반환)
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // 추가 예시 2) DTO로 조회
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 추가 예시 3) 컬렉션 파라미터 바인딩 방식. ex) in :파라미터명
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);


    /**
     * 4. 유연한 반환타입 방식
     * 스프링 데이터 JPA는 유연한 반환 타입 지원
     *
     * 조회 결과가 많거나 없으면?
     * 컬렉션 -> 결과 없음: 빈 컬렉션 반환
     * 단건 조회 -> 결과 없음: null 반환
     *         -> 결과가 2건 이상: javax.persistence.NonUniqueResultException 예외 발생 (단건조회했는데 2개이상 뜨니까 깜놀한듯)
     *
     * 상세설명: 단건으로 지정한 메서드를 호출하면 스프링 데이터 JPA는 내부에서 JPQL의 Query.getSingleResult() 메서드를 호출한다.
     * 이 메서드를 호출했을 때 조회 결과가 없으면 javax.persistence.NoResultException 예외가 발생하는데 개발자 입장에서 다루기가 상당히 불편하다.
     * 스프링 데이터 JPA는 단건을 조회할 때 이 예외가 발생하면 예외를 무시하고 대신에 null 을 반환해주는 것이다.
     *
     * 반환 타입 참고 공식 링크: https://docs.spring.io/spring-data/jpa/docs/current/reference/ html/#repository-query-return-types
     */
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건을 Optional을 감싼


    /**
     * 5. 페이징
     * 1) 무슨 디비를 쓰든 아래 추상화 기능을 적용가능하게 만들어둠(관계형을 쓰든 NoSql을 쓰든)
     * org.springframework.data.domain.Sort : 정렬 기능
     * org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)
     *
     * 2) 특별한 반환 타입:
     * org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징 -> 전통적인 페이지네이션에서 사용
     * org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능 (내부적으로 limit + 1조회) -> 더보기 페이지네이션에서 사용
     * List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
     *
     * 3) 카운트 쿼리 분리
     * 토탈 카운트 쿼리는 사실 여러단계의 left outer 조인을 진행할때, 해당 조인을 진행할 필요가 없음
     * 그런데 @Query에 countQuery를 따로 작성해주지 않으면 그냥 다 조인해서 토탈카운트 쿼리가 나감.
     * 성능 이슈가 심각하게 드러날 수 있기 때문에 고려해서 분리해줘야함!
     * 참고로 전통적인 페이지네이션보다 더보기 페이지네이션이 성능상 훨씬 유리
     */
    @Query(value = "select m from Member m left join m.team t", // sort같은 경우 더 복잡해지는 상황에 잘 안먹을수가있음. 그럴땐 여기에 직접 작성해주기.
            countQuery = "select count(m) from Member m") // 이런식으로 카운트 쿼리는 따로 분리하여 날릴수있음 (성능 향상)
    Page<Member> findByAge(int age, Pageable pageable);
    Slice<Member> findByUsername(String username, Pageable pageable);


    /**
     * 6. 벌크성 수정 쿼리 - 변경감지는 한건한건 쿼리를 날리는거임. 근데 한번에 업데이트 쿼리른 날려야하는 경우가 있음
     */
    // @Modifying이 있어야 마지막에 .executeUpdate()를 실행함 (안그러면 getResultList() 같은걸 붙여버림)
    // clearAutomatically = true를 옵션으로 추가하면, 실제 로직에 em.clear()를 생략할 수도 있음.
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


    /**
     * 7. @EntityGraph
     * 연관된 엔티티들을 SQL 한번에 조회하는 방법
     * 사실 객체 Member와 Team은 지연로딩 관계이다.
     * 따라서 MemberRepositoryTest의 findMemberLazy 테스트를 보면, team의 실제 데이터를 조회하는 시점마다 쿼리가 실행된다. (N+1 문제 발생)
     *
     * N+1 문제를 해결하기 위해, 연관된 엔티티를 한번에 조회해야하는데 그때 필요한 것이 페치 조인이다.
     * 스프링 데이터 JPA는 JPA가 제공하는 엔티티 그래프 기능을 편리하게 사용하게 도와준다.
     * 이 기능을 사용하면 JPQL 없이 페치 조인을 사용할 수 있다. (JPQL + 엔티티 그래프도 가능)
     *
     * 즉, @EntityGraph 사실상 페치 조인(FETCH JOIN)의 간편 버전 -> LEFT OUTER JOIN 사용
     * 다만 실무에서 사용하기는 하지만 간단간단한 것에 국한.
     * 복잡한 쿼리를 날려야할 때는 결국 JPQL 써서 fetch join 해야될 일이 더 많기는 함
     * 그리고 실무에서는 거의 안쓰지만 @NamedEntityGraph라는 것도 있다는 것 가볍게 알아두기
     *
     * 아래는 7번과 관련된 5가지 예시들
     */
    // 1) EntityGraph를 안쓴 순수 JPQL 방식의 fetch join
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // 2) 오버라이딩 + EntityGraph 조합의 fetch join
    // 걍 findMemberFetchJoin 새로파고 위에 fetch join을 위한 jpql 쓰기 귀찮지? 근데 member랑 연관된거 싹다 한방쿼리로 불러오고는 싶지?
    // 그때 사용하는게 @EntityGraph인거임. 옵션으로 fetch join할 거 아래처럼 명시만 잘해주면 됨.
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // 3) 새로운 findAll 형태의 메소드 + EntityGraph 조합의 fetch join
    // 오버라이딩은 굳이 하고 싶진 않고 새로만들되 jpql은 findAll형태로 간단하게 쓰면서, fetch join 쿼리를 추가하싶다면 아래처럼 해도 됨!
    @Query("select m from Member m")
    @EntityGraph(attributePaths = {"team"})
    List<Member> findMemberEntityGraph();

    // 4) 파라미터 쿼리 + EntityGraph 조합의 fetch join
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // 5) NamedEntityGraph 로 fetch join
    @EntityGraph("Member.all")
    List<Member> findNamedEntityGraphByUsername(@Param("username") String username);


    /**
     * 8. JPA Hint & Lock
     * JPA Hint - JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)
     * org.springframework.data.jpa.repository.QueryHints 어노테이션을 사용
     *
     * Lock
     * org.springframework.data.jpa.repository.Lock 어노테이션을 사용
     * db에 셀렉트 날릴때 다른 접근은 불허하도록. 함부로 손대지마! 라는 목적으로 Lock을 걸 수 있다는 개념인듯?
     * JPA가 제공하는 락은 JPA 책 16.1 트랜잭션과 락 절을 참고
     * 근데 실시간 트래픽이 많은 서비스에선 Lock 걸면 안됨
     * 트래픽이 적고 돈을 맞추거나 하는 게 중요할 때 PESSIMISTIC Lock을 사용할 수 있음
     */
    // 1) JPA Hint
    // 성능적으로 최적화 하는데 사용할 수 있긴한데, 아주 애용되진 않음. 진짜 중요한 조회 중에 지속적인 성능문제가 있을때 넣음
    // 즉, redis비롯한 할수있는 튜닝 다 했는데도 계속적인 문제가 발생할 때 추가로 하는 정도
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // 2) JPA Hint + paging
    // forCounting : 반환 타입으로 Page 인터페이스를 적용하면 추가로 호출하는 페이징을 위한 count 쿼리도 쿼리 힌트 적용(기본값 true )
    @QueryHints(value = { @QueryHint(name = "org.hibernate.readOnly",
            value = "true")},
            forCounting = true)
    Page<Member> findReadOnlyByUsername(String name, Pageable pageable);

    // 3) Lock
    // select for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);


    /**
     * 9. Projection
     * 주의
     *  - 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능 프로젝션 대상이 ROOT가 아니면
     *  - LEFT OUTER JOIN 처리
     *  - 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산
     *
     * 정리
     *  - 프로젝션 대상이 root 엔티티면 유용하다.
     *  - 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다! 실무의 복잡한 쿼리를 해결하기에는 한계가 있다.
     *  - 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL을 사용하자
     */
    // 1) 인터페이스 기반 Closed Projections
    // 프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA가 제공
    List<UsernameOnly> findProjectionsByUsername(String username);

    // 3) 클래스 기반 Projection
    // 다음과 같이 인터페이스가 아닌 구체적인 DTO 형식도 가능 생성자의 파라미터 이름으로 매칭
    List<UsernameOnlyDto> findProjectionsWithDtoByUsername(String username);

    // 4) 동적 Projections
    // 다음과 같이 Generic type을 주면, 동적으로 프로젝션 데이터 번경 가능
    <T> List<T> findProjectionsByUsername(String username, Class<T> type);


    /**
     * 10. Native Query
     * 가급적 네이티브 쿼리는 사용하지 않는게 좋음, 정말 어쩔 수 없을 때 사용
     * 최근에 나온 궁극의 방법 -> 스프링 데이터 Projections 활용
     *
     * 1) 스프링 데이터 JPA 기반 네이티브 쿼리
     *  - 페이징 지원
     *  - 반환 타입
     *      - Object[]
     *      - Tuple
     *      - DTO(스프링 데이터 인터페이스 Projections 지원)
     *  - 제약
     *      - Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있음(믿지 말고 직접 처리)
     *      - JPQL처럼 애플리케이션 로딩 시점에 문법 확인 불가
     *      - 동적 쿼리 불가
     *  - JPQL은 위치 기반 파리미터를 1부터 시작하지만 네이티브 SQL은 0부터 시작
     *  - 네이티브 SQL을 엔티티가 아닌 DTO로 변환은 하려면
     *      - DTO 대신 JPA TUPLE 조회
     *      - DTO 대신 MAP 조회
     *      - @SqlResultSetMapping -> 복잡
     *      - Hibernate ResultTransformer를 사용해야함 -> 복잡
     *      - https://vladmihalcea.com/the-best-way-to-map-a-projection-query-to-a-dto-with-jpa- and-hibernate/
     *      - 네이티브 SQL을 DTO로 조회할 때는 JdbcTemplate or myBatis 권장
     *
     * 2) Projections를 함께 활용
     *  - 예) 스프링 데이터 JPA 네이티브 쿼리 + 인터페이스 기반 Projections 활용
     *
     * 3) 동적 네이티브 쿼리
     *  - 하이버네이트를 직접 활용
     *  - 스프링 JdbcTemplate, myBatis, jooq같은 외부 라이브러리 사용
     */
    // 1) 네이티브 쿼리 only
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    // 2) 네이티브 쿼리 + Projections를 함께 활용 (+ Dto도 만들어서 적용)
    @Query(value = "select m.member_id as id, m.username, t.name as teamName " + "from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);

    // 3) 동적 네이티브 쿼리 (하이버네이트를 직접 활용)

}




/** 기타 참고

 */