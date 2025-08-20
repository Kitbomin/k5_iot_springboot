package com.example.k5_iot_springboot.repository;

import com.example.k5_iot_springboot.entity.D_Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
* Post 와 Comment의 관계가 1:N 의 관계
*
* D_Post post = postRepository.findById(id).get(); << 보통은 이렇게 많이 씀
* post.getComments.forEach(...);  << 댓글 접근
*
* == 코드 풀이 ==
* 1) 첫번째 쿼리: SELECT * FROM posts WHERE id = ?
*
* 2) 두번째 쿼리: LAZY 설정 코드를 "여버 번" 실행할 때마다 초기화를 위한 SELECT 문이 별도로 실행됨 -> 메모리가 아야해요
*
* ## 상황 1) 단일 Post만 조회하는 경우 ##
* -- 1번째 쿼리
*   SELECT * FROM posts WHERE id=?
* -- 2번째 쿼리
*   : 이후 post.getComments() 처음 호출 시 댓글 컬렉션 초기화용으로 딱 1번 실행됨
*   SELECT * FROM comments where post_id=?
*
* ## 상황 2) Post를 N개 먼저 가져온 뒤 각 Post 마다 getComments() 호출을 하는 경우 ##
* -- 1번째 쿼리
*   SELECT * FROM posts limit 20;
* -- 2번째 쿼리
*   SELECT * FROM comments where post_id=?
*       >> 총 20번 실행을 하게 됨
*
* 1번째 쿼리(1) + 2번째 쿼리(N)
*   >> 1+N 쿼리 실행 문제 발생
*
* */


@Repository
public interface D_PostRepository extends JpaRepository<D_Post, Long> {
    // 게시글 조회 + 댓글까지 즉시 로딩

    // 댓글까지 즉시 로딩
    @Query("""
        select distinct p
        from D_Post p
            left join fetch p.comments c
        where p.id = :id
""")
    Optional<D_Post> findByIdWithComments(@Param("id") Long id);

    // 전체 조회(댓글 제외)
    @Query("""
        select p
        from D_Post p
            order by p.id desc
""")
    List<D_Post> findAllOrderByIdDesc();


    // === 1. 쿼리 메서드 === //
    // : Spring Data JPA 가 메서드명을 파싱해 JPQL을 자동 생성해줌

    // EX1) findByAuthorOrderByIdDesc => where author = ? + order by id desc
    // EX2) findByTitleLikeIgnoreCaseOrderByIdDesc => where lower(title) like lower(?) + order by id desc

    List<D_Post> findByAuthorOrderByIdDesc(String author);
    List<D_Post> findByTitleContainingIgnoreCaseOrderByIdDesc(String keyword);

    // 8) 댓글이 가장 많은 상위 N개
    //  : 쿼리 메서드만으로는 집계/정렬이 불가능함 -> JPQL 또는 Native Query 사용

    // === 2. JPQL === //
    // : 그룹핑/집계/조인/서브쿼리 등 쿼리메서드로 표현이 어려운 경우 명시적으로 JPQL을 작성
    // - 엔티티명/필드명을 기준으로 작성 (DB에 독립적인 성질을 가지고 있음)

    // 6) 특정 작성자의 모든 게시글 (최신글 우선)
    @Query("""
        select P
        from D_Post P
        where P.author = :author
        order by P.id desc
""")
    List<D_Post> findByAuthorOrderByIdDesc_Jpql(@Param("author") String author);


    // === 3. Native SQL === //
    // : DB 벤더 특화 기능(극한 성능이 필요한 경우)
    // - 복잡한 통계/랭킹/리포트에 SQL 가독성이 JPQL 보다 뛰어남
    // >> DB를 기준으로 작성됨

    // +) 매핑 전략
    // [1] 엔티티 반환 가능 - 엔티티 필드와 동일한 별칭으로 모든 칼럼 선택 후 반환 => 데이터베이스랑 1:1 매칭이 됨
    //      >> 일부 컬럼만 선택할 경우 매핑 실패 OR 지연 로딩 문제 발생

    // [2] 인터페이스 프로젝션(권장)
    //      >> 결과 컬럼 별칭 <-> 인터페이스 getter 이름 매칭으로 타입 세이프 (캐스팅 할 필요 없음)

    // [3] Object 객체로 반환
    //      >> 각 필드별로 형 변환을 시키는 작업이 필요함 (인터페이스 프로젝션을 사용하지 않으면...)

    // 6) 특정 작성자의 모든 게시글(최신글 우선)
    @Query(value = """
        select * from posts
        where author = :author
        order by id desc 
    """, nativeQuery = true)
    List<D_Post> findByAuthorOrderByIdDesc_Native(@Param("author") String author);

    // 7) 제목 검색 조회 (JPQL & Native SQL) //
    /*
    * SELECT * FROM posts
    * WHERE
    *   title LIKE %keyword%
    * ORDER BY
    *   id DESC;
    * */

    @Query("""
        select P
        from D_Post P
        where lower(P.title) like lower(concat('%', :keyword, '%'))
        order by P.id desc
    """)
    List<D_Post> searchByTitleKeyword_Jpql(@Param("keyword") String keyword);

    @Query(value = """
        select * from posts
        where title like concat('%', :keyword, '%')
        order by id desc 
""", nativeQuery = true)
    List<D_Post> searchByTitleKeyword_Native(@Param("keyword") String keyword);

    // 8) 댓글 많은 게시글 TOP N개 (JPQL & Native SQL) //
    /*
    * SELECT * FROM
    * */

    @Query("""
        select P as post, count(C.id) as cnt
        from 
            D_Post P
                left join D_Comment C
                on C.post = P
        group by P
        order by cnt desc, P.id desc
    """)
    List<Object[]> findTopPostsByCommentCount_Jpql();
    // Object[]
    // : [0] -> D_Post
    // : [1] -> 댓글 수 Number (타입) --> 순회 돌면서 변환 시키기

    // 인터페이스 프로젝션 사용 //
    public interface PostWithCommentCountProjection {
        // 원하는 데이터
        Long getPostId(); // posts.id
        String getTitle(); // posts.title
        String getAuthor(); // posts.author
        Long getCommentCount(); // count(c.id)
    }

    @Query(value = """
        SELECT 
            p.id as postId,
            p.title as title,
            p.author as author,
            count(c.id) as commentCount
        FROM
            posts p 
            LEFT JOIN comments c
            ON c.post_id = p.id 
        GROUP BY 
            p.id, p.id, p.title, p.author
        ORDER BY 
            commentCount DESC, p.id DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<PostWithCommentCountProjection> findTopPostsByCommentCount_Native(@Param("limit") int limit);


    // 9) 특정 키워드 값을 가진 댓글이 포함된 게시글 찾기
    // 인터페이스 프로젝션 사용
    public interface PostListProjection {
        Long getId();
        String getTitle();
        String getContent();
        String getAuthor();

    }

    @Query(value = """
        select
            p.id        as id,
            p.title     as title,
            p.content   as content,
            p.author    as author
        from 
            posts p
            left join comments c
            on c.post_id = p.id
        where 
            c.content like concat('%', :keyword, '%') 
        group by p.id, p.title, p.content, p.author 
        order by p.id desc
""", nativeQuery = true)
    List<PostListProjection> findByCommentKeyword(@Param("keyword") String keyword);


    // 10) 특정 작성자의 게시글 중, 댓글 수가 minCount 이상인 게시글 조회
    @Query(value = """
        select 
            p.id as postId,
            p.title as title,
            p.author as author,
            count(c.id) as commentCount
        from
            posts p
            left join comments c
            on c.post_id = p.id
        where
            p.author = :author
        group by p.id, p.title, p.content, p.author
        having count(c.id) >= :minCount
        order by p.id desc

""", nativeQuery = true)
    List<PostWithCommentCountProjection> findAuthorPostsWithMinCount(@Param("author") String author, @Param("minCount") int minCount);
}

// EXISTS(select 1 from comments c where c.post_id = p.id and c.content like concat('%', :keyword, '%'))