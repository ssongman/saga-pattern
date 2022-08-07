package com.ssongman.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssongman.dto.Board;
import com.ssongman.service.BoardService;

@RestController
@RequestMapping("/boardManager")
public class BoardController {
	
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/health")
	public String getHealth() {
		return "OK";
	}	
	
	@GetMapping("/getAll")
	public List<Board> getBoardAll() {
		return boardService.getBoardAll();
		
	}
	
	@GetMapping("/get/{id}")
	public String getBoardById(@PathVariable("id") Long Id) {
		Board board1 = boardService.getBoard1(Id);
		Board board2 = boardService.getBoard2(Id);
		
		return board1.getTitle() + " / " + board2.getTitle();
		
	}
	
	@GetMapping("/create/{id}")
	public Board createBoard(@PathVariable("id") Long Id) {
		Board board = boardService.createBoard(Id);		
		return board;
		
	}
	
	@GetMapping("/deleteAll")
	public String deleteAll() {
		boardService.deleteAll();
		return "OK";
		
	}

}
