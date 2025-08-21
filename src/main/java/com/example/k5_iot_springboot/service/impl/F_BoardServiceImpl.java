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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class F_BoardServiceImpl implements F_BoardService {
    private final F_BoardRepository boardRepository;

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
}
