package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 위에 꺼랑 같은 결과가 나옴. 스프링이 알아서 그냥 도메인 클래스를 참고하여 컨버팅해주고, 바로 찾아낸 member를 파라미터로 인젝션해주는 것
    // 주의: 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 조회용으로만 사용해야 한다.
    // (트랜잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 변경해도 DB에 반영되지 않는다.)
    // -> 결과적으로 편해보이지만 권장하진 않음. 정 쓸거면 조회용으로만!
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<Member> list(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }

    // 이렇게 하면 @PageableDefault로 인해, yml 글로벌설정보다 우선권을 가져서 적용됨
    @GetMapping("/members2")
    public Page<Member> list2(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }

    // 사실 엔티티 객체를 그대로 노출하면 안됨 (설계를 그대로 드러내는 것이므로) 항상 DTO로 반환해야함!
    @GetMapping("/members3")
    public Page<MemberDto> list3(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
//        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        Page<MemberDto> map = page.map(MemberDto::new);
        return map;
    }

    // 데이터가 없어서 일단 'web확장 - 도메인 클래스 컨버터' 테스트를 위해 하나 넣어두겠음
//    @PostConstruct
    public void init() {
//        memberRepository.save(new Member("userA", 10);
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
