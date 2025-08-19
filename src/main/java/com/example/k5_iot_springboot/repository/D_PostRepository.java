package com.example.k5_iot_springboot.repository;

import com.example.k5_iot_springboot.entity.D_Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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


}
