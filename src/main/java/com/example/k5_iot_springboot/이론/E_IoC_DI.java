package com.example.k5_iot_springboot.이론;

/*
* === 제어의 역전(IoC) VS 의존성 주입(DI) ===
*
* 1. 제어의 역전(Inversion of Control)
*   : 프로그램의 제어 흐름을 개발자가 직접 통제하지 않고 외부 컨테이너(스프링 컨테이너)에 위임하는 방식
*   - 제어의 권한이 컨테이너에게 있어 객체의 생명 주기를 컨테이너가 관리함
*   - IoC 자체는 개념(이론) 임 이를 구현하는 대표적인 방법이 DI임
*
* 2. 의존성 주입(Dependency Injection)
*   : 클래스가 필요로 하는 객체(의존성, Dependency)를 외부에서 주입(Injection)받아 사용하는 방식
*   - 객체 간의 결합도는 감소, 유연성과 재사용성 증가 & 확장성, 유지보수성, 테스트 용이성이 향상됨
*   >> 생성자 주입 방식(권장)
*   >> 필드 주입 방식
*   >> 세터 주입 방식
*
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 1. 전통적인 자바 프로그래밍 방식
class Book1 {
    private String title;

    public Book1(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}

class BookStore1 {
    private Book1 book;

    public BookStore1() {
        // Book1 객체를 생성자가 직접 생성하고 있음 -> 인스턴스화
        // : 개발자가 객체 생성과 관리(수명 주기)를 모두 책임지고있음

        // >> Book1이 변경되면 BookStore1도 같이 수정해야되는 상황이 나옴 (결합도가 높은 상태임)
        //      : 확장성, 유지보수성, 테스트에 어려움이 생길 수도 있음
        this.book = new Book1("Spring Boot 기초");
    }

    public void displayBook() {
        System.out.println("Book: " + book.getTitle());
    }
}


// 2. 스프링 제어의 역전 프로그래밍 방식
@Component
// : 스프링 컨테이너가 해당 객체를 관리하도록 설정
// >> 스프링 빈(Bean) 으로 인식됨
// >> 스프링 컨테이너에 의해 관리되는 재사용 가능한 소프트웨어 컴포넌트임
class Book2 {
    private String title;

    public Book2() {
        this.title = "스프링 기초";
    }

    public String getTitle() {
        return this.title;
    }
}


@Component
class BookStore2 {
    private Book2 book;


    // 스프링이 Book2 객체를 생성해 자동으로 BookStore2에 넣어줌 (매개변수로 전달되는거임)
    // - 개발자가 new 연산자 사용 없이 스프링 컨테이너가 객체를 직접 만들어서 "주입" 시킴
    @Autowired // 스프링 컨테이너에게 해당 타입의 밴을 찾아 주입하는 어노테이션
    public BookStore2(Book2 book) {
        this.book = book; // new 연산자가 엄슴!!
    }

    public void displayBook() {
        System.out.println("Book: " + book.getTitle());
    }
}


/*
 1. 스프링 컨테이너
    : 애플리케이션 내에서 객체(빈, Bean)의 생명 주기와 설정을 관리함
    - 제어의 역전을 실현, 의존성 주입을 지원함

 */


public class E_IoC_DI {

}
