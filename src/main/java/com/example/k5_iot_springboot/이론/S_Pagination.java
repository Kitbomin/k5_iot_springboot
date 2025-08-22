package com.example.k5_iot_springboot.이론;

/*
* === 페이지네이션(Pagination) ===
* : 여러 개의 데이터를 페이지를 기반으로 나누어 요청하는 방식
*
* 1. 페이지네이션의 목적
*   - 성능 비용: 대용량 데이터 전체 조회 방지, DB/네트워크 부하 절감
*   - UX: 목록을 나누어 보여주거나 "더보기/무한스크롤" 제공
*
* 2. 페이지네이션 기본 용어
*   - page: 몇 번째 페이지인지 (index 처럼 0부터 시작함)
*   - size: 한 페이지에 담을 데이터 수
*   - sort: 정렬 기준 (ex. createdAt & desc -> 최신순 )
*   - totalElements/totalPages: 전체 개수/ 전체 페이지 수 (Offset 방식에서만 사용함) (커서기반은 사용하지 않음)
*   - Page VS Slice
*       >> Page : 전체 개수(count) 포함 -> '총 몇 페이지인지' 같은 정보가 필요할 때 사용
*       >> Slice: 전체 개수 없이 '다음 페이지가 더 있는지(hasNext)' 만 판단 -> 성능측에서 유리하고, 무한 스크롤/더보기 에 적합함
*
* 3. 페이지네이션 종류 (2개)
*   1) Offset 기반 (page/size/정렬)
*       - 요청: /boards?page=0&size=10&sort=createdAt,desc
*
*       - 장점: 직관적, "총 페이지 수" 안내가 가능함
*       - 단점: 뒤 페이지로 갈 수록 count(*) 에 대한 비용이 증가함 (대용량 데이터에선 부담스러움)
*
*   => 관리자 목록, 정확한 "총 건수/총 페이지 수"가 필요한 화면, 페이지 점프가 필요한 UI에 많이 사용함
*
*       +) 페이지 번호(page) 와 크기(size)로 건너뛸 개수(Offset = page * size) 를 계산하여 LIMIT size OFFSET offset
*
*   2) Cursor(Keyset) 기반 (cursorId/size)
*       - 요청: /boards/cursor?cursorId=마지막아이템id&size=10
*
*       - 장점: 빠름(대용량 데이터에 유리), 인피니트 스크롤(무한스크롤)에 최적임
*       - 단점: "총 페이지 수" 계산이 어려움, 정렬을 특정 키로 고정해야함(id, createdAt + id)
*                   >> 최신순 인기순 정렬같은거 못함
*
*   => 데이터가 클 경우(수십만~수천만 건), 무한 스크롤/피드형 목록, 실시간성의 성능이 중요한 화면에서 많이 사용됨
*
*       +) 마지막으로 본 키(key) 이후 데이터를 키 조건(id < lastId) 로 조회
*           앞 부분을 통째로 건너뛰지 않고 검색해서 찾아감
*           >> 정렬 키가 반드시 유니크/일관 되어야함
*
* 4. 페이지네이션 API 설계
*   1) Offset 기반(일반 목록)
*       - EndPoint: Get /boards
*       - Query: page, size(기본 10, 최대 100 권장), sort (request param)
*       - 응답: content(목록) + meta(페이지정보) 구조
*
*   2) Cursor 기반(무한스크롤/더보기)
*       - EndPoint: Get /boards/cursor
*       - Query: cursorId(처음 호출 시 생략 - 최신부터), size(기본 10, 최대 100 권장)
*       - 응답: content(목록) + hasNext + nextCursor (다음 요청에 바로 사용하기 위해 다음 값의 존재 여부를 받아와서 보여줌)
*
* */



public class S_Pagination {
}
