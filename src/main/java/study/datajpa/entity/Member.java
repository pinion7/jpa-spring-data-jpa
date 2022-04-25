package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"}) // 객체 찍을때 바로 출력이 되는 것? (연관관계필드는 toString 하면 무한루프돌수있음)
@NamedQuery(
        name="Member.findByUser",
        query="select m from Member m where m.username = :username"
) // NamedQuery는 실무에서는 안쓰는 기능이지만 소개
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team")) // 얘도 실무에서 안쓰지만 존재한다는 것 참고
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id") //fk명
    private Team team;

    // 엔티티는 기본적으로 default생성자(파라미터없는)가 있어야함.
    // 그리고 private으로 만들면 안됨. jpa가 프록시 기술같은거쓸때 자동으로 생성자쓸 수 있어서, 대신 public보단 protected 정도로 열기
//    protected Member() {} // @NoArgsConstructor(access = AccessLevel.PROTECTED) 써두됨

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    // 바꿀때 member내에 있는 team 뿐만아니라, team에 속하는 해당 멤버도 바꿔주게끔 세팅
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
