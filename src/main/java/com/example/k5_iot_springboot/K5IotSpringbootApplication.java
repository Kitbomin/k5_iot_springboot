package com.example.k5_iot_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication // 스프링 부트에서 필요한 기본 설정을 제공해줌
// 애플리케이션 구동 시 흐름
// 1) @SpringBootApplication 하위 패키지에서 @Component가 붙은 클래스를 모두 감지함
// 		- Controller, Service, Repository, config 설정 파일이 감지됨

// 2) 빈 생성: 컨테이너가 @Component 빈 객체 생성

// 3) 의존성 해결: 생성자에 의존이 필요한 경우 컨테이너가 생성한 빈을 주입시켜줌

// 4) 의존성 해결 후 해당 빈 사용 가능

public class K5IotSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(K5IotSpringbootApplication.class, args);
	}

}
