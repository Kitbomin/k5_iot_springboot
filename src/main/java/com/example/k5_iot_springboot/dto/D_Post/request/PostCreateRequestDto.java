package com.example.k5_iot_springboot.dto.D_Post.request;


// record
// : Java 16에 도입된 새로운 class 선언 방식
// - 데이터를 담기 위한 불변 클래스임
// - DTO, VO, Entity 와 같은 데이터 전달용 클래스 생성 시 사용함

// >> 필드, 생성자, getter, equals(), hashCode(), toString() 등을 자동 생성함 => 어노테이션을 굳이 쓰지 않아도 된다는거

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
//: 클라이언트의 요청값을 역직렬화 할 때 (json -> entity로 변환) POJO(클래스) 에 없는 JSON 필드가 와도 에러가 발생하지 않고, 무시됨
public record PostCreateRequestDto(

        @NotBlank(message = "제목은 필수 입력 값입니다.")
        @Size(max = 200, message = "제목은 최대 200자 까지 입력 가능합니다.")
        String title,

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        @Size(max = 10_000, message = "내용은 최대 10,000 자 까지 입력 가능합니다.")
        // 어노테이션엔 1000자리 이상 숫자 입력시 , 대신 _ 을 쓸 수 있음
        String content,

        @NotBlank(message = "작성자는 필수 입력 값입니다.")
        @Size(max = 100, message = "작성자는 최대 100자까지 입력 가능합니다.")
        String author
) {}
