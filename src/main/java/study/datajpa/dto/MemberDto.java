package study.datajpa.dto;

import lombok.Getter;
import study.datajpa.entity.Member;

@Getter
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    // 아래처럼 생성자를 직접 엔티티를 받아서 사용해도 됨!
    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.teamName = member.getTeam().getName();
    }
}
