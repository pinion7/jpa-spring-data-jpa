package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

// JpaBaseEntity보다 자동화된 방식 (AuditingEntityListener.class 자체가 스프링 데이터 jpa가 제공하는 방식)
// BaseTimeEntity에는 createdDate, lastModifiedDate만 두고,
// 만약 등록자 수정자도 필요하다면 BaseTimeEntity를 상속받은 객체(ex: BaseEntity)를 활용하는 방식이 좋음
// 등록자 수정자까지는 테이블에 필요 없는 경우도 많기 때문에 이렇게 분리해주는 게 좋음.
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
