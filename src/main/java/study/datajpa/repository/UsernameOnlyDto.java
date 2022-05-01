package study.datajpa.repository;

/**
 * 3. 클래스 기반 Projection
 *  - 다음과 같이 인터페이스가 아닌 구체적인 DTO 형식도 가능 생성자의 파라미터 이름으로 매칭
 */
public class UsernameOnlyDto {

    private final String username;

    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
