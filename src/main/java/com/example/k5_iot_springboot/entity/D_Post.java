package com.example.k5_iot_springboot.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Comments;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// JPA 프록시 생성을 위한 기본 생성자: 외부에서 무분별하게 생성하지 못하도록 접근 수준을 protected로 제한함
@AllArgsConstructor //빈 생성자가 바탕이 되어야함
@ToString(exclude = "comments")// 서버 연관성과는 무관함
// 해당 속성값의 필드를 제외하고 ToString 메서드 내에서 필드값을 출력해줌
@Builder
public class D_Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("게시글 제목") // RDBMS 컬럼에 대한 주석 첨부
    @Column(nullable = false, length = 200)
    private String title;


    @Comment("게시글 내용")
    @Lob // 대용량 텍스트 저장 시 사용됨 -> RDBMS 에서 자동으로 TEXT(CLOB) 으로 매핑됨
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;


    @Comment("작성자 표시명 또는 ID")
    @Column(nullable = false, length = 100)
    private String author;

    @OneToMany (
            mappedBy = "post",
            // : 관계의 주인은 D_Comment.post 필드임을 지정함 -> 게시글과 댓글이 있을 때 주도권을 댓글이 가짐
            // : Post 내부에서는 읽기 전용 매핑, FK는 D_Comment와 연결된 테이블이 가짐

            cascade = CascadeType.ALL,
            // : 부모(D_Post)에 대한 persist/merge/remove 등이 자식(D_Comment)로 전이 됨
            // - 게시글 저장/삭제 시 댓글도 같이 처리된다는 뜻 (연쇄작용)

            orphanRemoval = true,
            // : 컬렉션에서 제거된 댓글은 고아 객체로 간주되어 DB 에서도 자동삭제 되는 옵션(실제 DELETE 수행됨)

            fetch = FetchType.LAZY
            // : 컬렉션을 지연로딩함
            // - 댓글이 필요할 때만 실제 SELECT를 수행해 불필요한 로딩을 방지함
    )
    // 1(포스트) : 多(댓글) 관계라는걸 명시함
    // - 컬렉션 타입은 기본 Lazy 설정이 됨
    // >> 해당 어노테이션 내에서 세부 옵션을 지정함

    // 이건 읽기 전용임
    private List<D_Comment> comments = new ArrayList<>();
    // : new ArrayList로 초기화 중
    // - 1 : N 관계 시 컬렉션은 NullPointException 방지를 위해 즉시 초기화를 수행해야함
    // - 그래야 JPA 가 내부적으로 컬렉션 프록시(중개자)로 교체가 가능함


    // === 생성 / 수정 메서드 === //
    // : 양방향 관계에서의 편의를 위한 메서드
    // : 엔티티는 @Setter를 제거함
    // -> 의도된 변경 메서드만 공개 -> 이거 다 Setter화 시키면 보안도 위협되고 딱 세팅할 값만 지정해두는거

    private D_Post(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }


    // 생성 메서드
    public static D_Post create(String title, String content, String author){
        return new D_Post(title, content, author);
    }



    // 수정 메서드
    // 이걸 하는 이유는 @Setter 설정을 안해서
    public void changeTitle(String title) {this.title = title;}
    public void changeContent(String content) {this.content = content;}


    // === 양방향 연관관계 편의 메서드(중복 방지 포함) === //

    /*
    * 연관관계 메서드
    * : Comment 를 Post 에 추가/삭제 할 때 사용함
    *   - Comments 리스트에 추가/삭제를 하면
    *       >> 해당 Comment 의 post 필드에 현재 Post 객체를 생성함
    *
    * -> 이걸 왜함?
    * - 해당 설정을 하지 않으면 JPA의 영속성 컨텍스트가 양방향 관계를 완전히 이해하지 못함
    *
    * cf) 영속성 컨텍스트
    *   : JPA 에서 엔티티 객체를 연구, 저장하는 환경을 의미함
    *   - 엔티티 매니저에 의해 관리, save(), remove(), find() 와 같은 작업을 수행함
    *   - 영속성 컨텍스트에 저장된 엔티티는 1차 캐시 공간에 보관됨
    *       >> 트랜잭션이 끝날 때 실제 DB에 반영함
    *
    * cf) 양방향 관계
    *   : 두 엔티티가 서로 참조하는 관계를 의미
    *   - Post 가 여러 개의 Comment 를 가짐 -> @OneToMany
    *   - Comment가 하나의 Post에 속함 -> @ManyToOne
    **/

    public void addComment(D_Comment comment) {
        if (comment == null) return;
        if (!this.comments.contains(comment)){ // 인자에 있는 값의 여부에 따라 값 달라짐
            // : 기존의 댓글 배열(comments)에 추가하려는 댓글(comment)이 포함되어있지 않은 경우
            this.comments.add(comment);
            comment.setPost(this); // Comment 내에도 게시글 정보를 저장하게 됨
        }
    }

    public void removeComment(D_Comment comment) {
        if (comment == null) return;
        if (this.comments.remove(comment)){
            comment.setPost(null);
        }
    }




}
