package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String username;

    // 엔티티는 기본적으로 default생성자(파라미터없는)가 있어야함.
    // 그리고 private으로 만들면 안됨. jpa가 프록시 기술같은거쓸때 자동으로 생성자쓸 수 있어서, 대신 public보단 protected 정도로 열기
    protected Member() {}

    public Member(String username) {
        this.username = username;
    }
}
