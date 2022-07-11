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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	
	@GetMapping("/board/list")
	public List<Board> getBoardList() {
		return boardService.findAll();
	}

	@GetMapping("/board/{id}")
	public Board getBoard(@PathVariable("id") Long boardId) {
		return boardService.findById(boardId);
	}

	@PostMapping("/board/create")
	public Board createBoardListPage(@RequestBody Board board) {
		return boardService.save(board);
	}

	@DeleteMapping("/board/{id}")
	public void deleteBoardById(@PathVariable("id") Long boardId) {
		boardService.deleteById(boardId);
		return;
	}

}
