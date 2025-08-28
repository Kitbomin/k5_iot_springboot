package com.example.k5_iot_springboot.entity;

import com.example.k5_iot_springboot.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "articles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 아무런 매개변수가 없는 기본 생성자를 Protected 접근 제어자로 생성함
// : 외부 클래스에서(엔티티 정의 부분 외의) 객체를 직접 생성하는 것을 방지함
// - 무분별한 객체 생성을 억제함
// cf) JPA 에서 엔티티 생성 시 기본 생성자가 반드시 필요함!
//      >> JPA 를 위한 생성자를 프로젝트 전역에서 접근하는 것을 방지함 -> 여기서 쓸건 여기서만 써라 하는 얘기...
public class H_Article extends BaseTimeEntity {

    /** PK */
    @Id @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** 제목 */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /** 내용 */
    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARCHAR) // LongText랑 맞물리게 설정
    @Column(name = "content", updatable = true)
    private String content;

    /** 작성자(작성자 : 게시글 = 1 : 多)
     *  게시글 : G_User = 多 : 1
     * */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // 선택지는 없음
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "fk_articles_author"))
    // fetch: 가져오다, LAZY: 게으른
    // - 연관된 엔티티를 필요할 때만 DB 에서 꺼내올거임
    private G_User author;

    private H_Article(String title, String content, G_User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    /** 생성자
     * create를 만든 이유는 여기서 일을 다 처리하고 싶기 때문*/
    public static H_Article create(String title, String content, G_User author) {
        return new H_Article(title, content, author);
    }

    /** 수정역할 메서드 */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
