package com.example.k5_iot_springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = "post")
public class D_Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false) // optional은 자바에서 관장하는 역할
    // - Comment : Post = N : 1 관계에서 'N' 쪽 매핑임을 설정
    // - Lazy 설정으로 필요할 때만 게시글을 로딩하게 설정
    // - optional = false: FK가 반드시 존재해야함을 보장함(데이터 무결성) -> 게시글 없이 댓글은 존재할 수 없다는 뜻
    @JoinColumn(name = "post_id", nullable = false) // DB 에서 이럴거라고 정의하는거
    // - 외래키 컬럼명 지정
    // - NOT NULL 제약 조건 부여: FK 설정
    private D_Post post; // D_Post에서 매핑을 post로 지정해놓음


    @Comment("댓글 내용")
    @Column(nullable = false, length = 1000)
    private String content;

    @Comment("댓글 작성자 표시명 또는 ID")
    @Column(nullable = false, length = 100)
    private String commenter;


    // === 생성/수정 메서드 === //
    private D_Comment(String content, String commenter) {
        this.content = content;
        this.commenter = commenter;
    }

    public static D_Comment create(String content, String commenter) {
        return new D_Comment(content, commenter);
    }

    // 게시글 받아오기 -> public 설정 안해서 현재 패키지 내에서 접근 가능함
    // Post 에서만 댓글이 세팅되도록 가시성을 축소함(연관관계 일관성 유지)
    void setPost(D_Post post) {
        this.post = post;
    }


    public void changeContent(String content){
        this.content = content;
    }


}
