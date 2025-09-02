package com.example.k5_iot_springboot.common.constants;

// URL 설계 패턴
// : RESTful하게 API 경로를 규칙적으로 설계하는 것
// - 각 Controller 의 고유 경로 지정

public class ApiMappingPattern {
    // == 공통 베이스/버전 == //
    public static final String API = "/api";
    public static final String V1 = "/v1";
    public static final String BASE = API + V1;

    // == 1. 책(C_Book) == //
    public static final class Books {
        private Books() {}

        public static final String ROOT = BASE + "/books";

    }

    // == 2. 게시글(D_Post) == //
    public static final class Posts {
        private Posts() {}

        public static final String ROOT = BASE + "/posts";
        public static final String ID_ONLY = "/{postId}";
        public static final String BY_ID = ROOT + "/{postId}";

        // 6) 작가 게시글 조회
        public static final String BY_AUTHOR = "/author/{author}";

        // 7) 키워드 제목 조회
        public static final String SEARCH_BY_TITLE = "/search";

        // 8) 댓글 탑5 게시글 조회
        public static final String TOP_BY_COMMENTS = "/top-comments";
    }

    // == 3. 댓글(D_Comment) == //
    /*
    * RESTful API 설계
    *   - 현재 구조) 댓글(Comment)가 게시글(Post) 엔티티에 포함되어 있음 -> 1 : N 의 관계를 가지고 있음 => 하위 댓글이 상위 게시글에 종속되도록 설계해야함
    *
    *   - 종속된 데이터에 대해 하위 리소스 표현을 사용
    *       : 댓글의 CRUD는 게시글 하위 리소스로 표현함
    *
    *       1) 댓글 생성(POST)  : /api/v1/posts/{postId}/comments
    * -- 댓글 조회가 없는 이유는 따로 조회하는 페이지가 없기 때문
    *       2) 댓글 수정(PUT)   : /api/v1/posts/{postId}/comments/{commentId}
    *       3) 댓글 삭제(DELETE): /api/v1/posts/{postId}/comments/{commentId}
    *
    * */
    public static final class Comments {
        private Comments() {}

        public static final String ROOT = Posts.BY_ID + "/comments";
        public static final String ID_ONLY = "/{commentId}";
        public static final String BY_ID = ROOT + "/{commentId}";


    }


    // === 4. 게시글(F_Board) === //
    public static final class Boards {
        private Boards() {}

        public static final String ROOT = BASE + "/boards";

        public static final String ID_ONLY = "/{boardId}";
    }


    public static final String BOOK_API = "/api/v1/books";



    // === 5. 제품(I_Product) === //
    public static final class Products {
        private Products() {}

        public static final String ROOT = BASE + "/products";

        public static final String ID_ONLY = "/{productId}";
    }

    // === 6. 수량(I_Stocks) === //
    public static final class Stocks {
        private Stocks () {}

        public static final String ROOT = BASE + "/stocks";
        public static final String ADJUST = "/adjust";

        public static final String PRODUCT_ID = "/{productId}";
    }

}
