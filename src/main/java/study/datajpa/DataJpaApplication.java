package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing // Auditing 적용하려면 반드시 넣어야 함
@SpringBootApplication // 스프링부트는 현재 패키지부터 모든 하위패키지 까지 등록된 컴포넌트스캔(빈으로 등록된 것)을 끌어옴
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
//		return new AuditorAware<String>() {
//			@Override
//			public Optional<String> getCurrentAuditor() {
//				return Optional.of(UUID.randomUUID().toString());
//			}
//		};

		// 람다로 구현 (인터페이스에 메서드 하나면 람다로 바꿀 수 있음)
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
