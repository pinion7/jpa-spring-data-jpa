package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // 스프링부트는 현재 패키지부터 모든 하위패키지 까지 등록된 컴포넌트스캔(빈으로 등록된 것)을 끌어옴
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

}
