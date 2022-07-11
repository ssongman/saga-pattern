package com.ssongman.board;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ssongman.board.entity.Board;
import com.ssongman.board.entity.BoardRepository;

@SpringBootTest
public class BoardTests {
	
    @Autowired
    BoardRepository boardRepository;

    @Test
    void save() {

        // 1. 게시글 파라미터 생성
        Board board = Board.builder()
                .title("1title")
                .content("1content")
                .writer("honggildong")
                .hits(0)
                .deleteYn('N')
                .build();

        // 2. 게시글 저장
        boardRepository.save(board);

        // 3. 1번 게시글 정보 조회
        Board entity = boardRepository.findById((long) 1).get();
        assertThat(entity.getTitle()).isEqualTo("1title");
        assertThat(entity.getContent()).isEqualTo("1content");
        assertThat(entity.getWriter()).isEqualTo("honggildong");
        
        return;
    }
    

    @Test
    void findAll() {

        // 1. 전체 게시글 수 조회
        long boardsCount = boardRepository.count();

        // 2. 전체 게시글 리스트 조회
        List<Board> boards = boardRepository.findAll();
        
        return;
    }

    @Test
    void delete() {

        // 1. 게시글 조회
        Board entity = boardRepository.findById((long) 1).get();

        // 2. 게시글 삭제
        boardRepository.delete(entity);        
        
        return;
    }

}
