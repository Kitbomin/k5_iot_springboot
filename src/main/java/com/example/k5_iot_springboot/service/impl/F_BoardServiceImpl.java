package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.dto.F_Board.request.BoardRequestDto;
import com.example.k5_iot_springboot.dto.F_Board.response.BoardResponseDto;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.entity.F_Board;
import com.example.k5_iot_springboot.repository.F_BoardRepository;
import com.example.k5_iot_springboot.service.F_BoardService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class F_BoardServiceImpl implements F_BoardService {
    private final F_BoardRepository boardRepository;

    // === 페이지네이션 공통: 안전한 Pageable 생성(화이트리스트 정렬) === //
    // - sort 엔 제약조건이 없음 -> 진짜 아무거나 다 정렬해버릴 수 있다는 소리
    // : 정렬 키를 그대로 신뢰할 경우, 존재하지 않는 필드나 JPA 동적 JPQL 에서 문자열 충돌 발생 가능선 존재
    private static final Set<String> ALLOWED_SORTS = Set.of("id", "title", "createdAt", "updatedAt"); //해당 내용을 제외한 키워드는 다 무시하겠단 정의
    //set과 list의 차이: set은 순서가 없고 중복이 허용되지 않음

    private Pageable buildPageable(int page, int size, String[] sortParams) {
        // 정렬 파라미터 파싱: ["createdAt,desc", "title,asc"] 형태
        // split 으로 ,를 기준으로 구분할거임

        Sort sort = Sort.by("createdAt").descending(); // 기본 정렬: 최신순
        // >> 정렬 파라미터가 없거나, 전부 화이트리스트에서 무시된 경우 디폴트 정렬을 사용하겠다는 정의

        if (sortParams != null && sortParams.length > 0) { // 배열에 주소값이 있고, 요소가 1개 이상 있다 == 빈 배열이 아닌경우
            // 정렬 순서를 보장할 리스트(순서O) - 여러 정렬 기준을 저장하기 위해
            List<Sort.Order> orders = new ArrayList<>();
            for (String p: sortParams) {
                if (p == null || p.isBlank()) continue; // 그다음 sortParams 들고오세용

                String[] t = p.split(","); // , 기준으로 스플릿 , t 에는 createdAt,desc 이런거 들어가있음

                String property = t[0].trim(); // ,기준 0번째 값을 들고오고 공백 제거

                // 화이트 리스트에 없는 속성을 무시할 로직
                if (!ALLOWED_SORTS.contains(property)) continue; // 저장하지 않고 패스

                // 기본 정렬 방향을 DESC로 선언 -> 피드/게시물은 최신순 정렬이 일반적임(권장)
                Sort.Direction dir = Sort.Direction.DESC;

                // t[1] 의 값을 들고올거임
                if (t.length > 1) { // 정렬 기준이 존재한다는
                    dir = "asc".equalsIgnoreCase(t[1].trim()) ?
                            Sort.Direction.ASC : Sort.Direction.DESC ;
                }

                // 검증을 다 거친 후의 값 저장
                // : 파싱한 정렬 기준 한 건을 Sort.Order 객체로 만들어 목록에 추가함
                // - 여러건이 쌓이면 ORDER BY prop1 dir1, prop2 dir2 ... 순서대로 적용이 됨
                orders.add(new Sort.Order(dir, property));
            }

            // 만약 다 돌고 orders가 비워지지 않을 경우 sort 값 재할당
            if (!orders.isEmpty()) sort = Sort.by(orders);

        }

        // sortParams 가 비워진 경우 || 유효한 정렬이 없는 경우에 호출됨
        return PageRequest.of(page, size, sort);
    }




    @Override
    @Transactional
    public ResponseDto<BoardResponseDto.DetailResponse> createBoard(BoardRequestDto.@Valid CreateRequest request) {
        F_Board board = F_Board.builder()
                .title(request.title())
                .content(request.content())
                .build();

        F_Board saved = boardRepository.save(board);
        BoardResponseDto.DetailResponse result = BoardResponseDto.DetailResponse.from(saved);

        return ResponseDto.setSuccess("SUCCESS", result);
    }

    @Override
    public ResponseDto<List<BoardResponseDto.SummaryResponse>> getAllBoards() {
        List<F_Board> boards = boardRepository.findAll();

        List<BoardResponseDto.SummaryResponse> result = boards.stream()
                .map(BoardResponseDto.SummaryResponse::from)
                .toList();

        return ResponseDto.setSuccess("SUCCESS", result);
    }

    @Override
    @Transactional
    public ResponseDto<BoardResponseDto.DetailResponse> updateBoard(Long boardId, BoardRequestDto.@Valid UpdateRequest request) {
        F_Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 게시글이 없습니다."));

        board.update(request.title(), request.content());

//        F_Board saved = boardRepository.save(board);

        // cf) updateAt의 데이터 확인
        //  : JPA Auditing이 flush/commit 시점에 @PreUpdate가 되면서 채워짐
        //  -> 영속성 컨텍스트가 DB에 반영될 때
        //  >> 서비스 안에서 DTO 변환이 곧바로 일어날 때 updatedAt 이 갱신 전 값으로 보여지게 됨 (일종의 지연)
        //      +) 다시 실행 시 커밋된 변경사항 확인 가능

        // cf) save() VS flush()
        // 1) save()
        //  : Spring Data JPA Repository 메서드
        //  - 새로운 엔티티 INSERT, 이미 존재하는 엔티티 UPDATE 등 >>> 영속 상태를 처리함
        //      +) findById 로 가져온 엔티티는 이미 영속상태를 가져 save()를 하지 안해도 커밋 시점에 자동 UPDATE 됨 => 롤백이 안됨

        // 2) flush()
        //  : JPA (EntityManager) 메서드에 속함
        //  - 해당 시점까지 영속성 컨텍스트(1차 캐시)에 쌓인 변경 내역(Dirty Checking 결과)를 즉시 DB에 반영
        //      >> 트랜잭션은 열린 상태(커밋이 안된 상태임 => 롤백이 됨!)

        boardRepository.flush(); // 변경 내용을 DB에 반영 (@PreUpdate 트리거 >> updatedAt 채워짐)

        BoardResponseDto.DetailResponse result = BoardResponseDto.DetailResponse.from(board);

        return ResponseDto.setSuccess("SUCCESS", result);
    }

    // cf) Page<T> VS Slice<T>
    // 1) Page<T>
    //  : 전체 개수(count 쿼리) 까지 실행해서 가져옴

    // 2) Slice<T>
    //  : count 쿼리 실행 X, 데이터 개수를 size + 1 로 요청해 다음 페이지 존재 여부만 판단

    @Override
    public ResponseDto<BoardResponseDto.PageResponse> getBoardsPage(Pageable pageable) {
//        Pageable pageable = buildPageable(page, size, sort);

        // cf) Pageable 인터페이스
        //      : 페이징과 정렬 정보를 추상화한 인터페이스
        //      >> 현재 페이지 번호, 한 페이지의 크기, 정렬 정보 반환, 다음 페이지 객체 생성, 이전 페이지 객체 생성을 담당함
        //      >> 특징
        //          : 실제 구현체는 PageRequest 사용 (PageRequest.of(page, size, sort))
        //          : JpaRepository의 findAll(Pageable pageable) 메서드에 전달


        Page<F_Board> pageResult = boardRepository.findAll(pageable);

        List<BoardResponseDto.SummaryResponse> content = pageResult.getContent().stream()
                .map(BoardResponseDto.SummaryResponse::from)
                .toList();

        BoardResponseDto.PageMeta meta = BoardResponseDto.PageMeta.from(pageResult);

        BoardResponseDto.PageResponse result = BoardResponseDto.PageResponse.builder()
                .content(content)
                .meta(meta)
                .build();

        return ResponseDto.setSuccess("SUCCESS", result);
    }



    @Override
    public ResponseDto<BoardResponseDto.SliceResponse> getBoardsByCursor(Long cursorId, int size) {
        // 커서는 최신순 id 기준으로 진행 (성능이 좋은 PK 정렬)
        // - 첫 호출: cursorId == null (Long.MAX_VALUE로 간주: 최신부터)
        long startId = (cursorId == null) ? Long.MAX_VALUE : cursorId;

        Slice<F_Board> slice = boardRepository
                .findByIdLessThanOrderByIdDesc(startId, PageRequest.of(0, size));

        List<BoardResponseDto.SummaryResponse> content = slice.getContent().stream()
                .map(BoardResponseDto.SummaryResponse::from)
                .toList();

        Long nextCursor = null;

        if (!content.isEmpty()) {
            nextCursor = content.get(content.size() - 1).id(); // 마지막 아이템의 id 반환
        }

        BoardResponseDto.SliceResponse result = BoardResponseDto.SliceResponse.builder()
                .content(content)
                .hasNext(slice.hasNext())
                .nextCursor(nextCursor)
                .build();

        return ResponseDto.setSuccess("SUCCESS", result);
    }
}
