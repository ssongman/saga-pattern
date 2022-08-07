package com.ssongman.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ssongman.board.entity.Board;
import com.ssongman.board.service.BoardService;
import com.ssongman.saga.SagaDTO;
import com.ssongman.saga.SagaService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
//@RequiredArgsConstructor
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	@Autowired
	private SagaService sagaService;
	
	@GetMapping("/board/list")
	public List<Board> getBoardList() {
		return boardService.findAll();
	}

	@GetMapping("/board/{id}")
	public Board getBoard(@PathVariable("id") Long boardId) {
		return boardService.findById(boardId);
	}

	@PostMapping("/board/create")
	public String createBoardListPage(@RequestBody Board board) {
		
		// DB Save
    	log.info("[createBoardListPage] [DB Save]-------------");
		Board boardRtn =  boardService.save(board);
		
		// Transaction Save [Redis]
    	log.info("[createBoardListPage] [Transaction Save]-------------");
		SagaDTO sagaDtoRtn = sagaService.setSagaDto(SagaDTO.builder()
									.data(boardRtn)
									.build());
		
		return sagaDtoRtn.getSagaId();
	}

	@DeleteMapping("/board/{id}")
	public void deleteBoardById(@PathVariable("id") Long boardId) {
		boardService.deleteById(boardId);
		return;
	}

}
