package com.ssongman.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssongman.dto.Board;
import com.ssongman.feign.Board2Feign;
import com.ssongman.feign.BoardFeign;

@Service
public class BoardService {
	
	@Autowired
	private BoardFeign boardFeign;
	
	@Autowired
	private Board2Feign board2Feign;
	

	
	
	// [BoardManager]-------------------------------------------------
	
	public List<Board> getBoardAll() {
		List<Board> boards = new ArrayList<>();
		
		List<Board> board1s = getBoard1All();
		List<Board> board2s = getBoard2All();

		boards.addAll(board1s);
		boards.addAll(board2s);
		
		return boards;
	}
	
	public Board createBoard(Long boardId) {
		Board board = Board.builder()
				.title("title" + boardId.toString())
				.content("content" + boardId.toString())
				.writer("Song")
				.hits(5)
				.deleteYn('N')
			    .build();

		saveBoard1(board);
		saveBoard2(board);
		
		return board;
	}
	
	public String deleteAll() {
		deleteBoard1All();
		deleteBoard2All();
		return "OK";		
	}
	
	

	// [Access]-------------------------------------------------
	public List<Board> getBoard1All() {
		return boardFeign.getBoardList();
	}
	
	public List<Board> getBoard2All() {
		return board2Feign.getBoardList();
	}
	
	public Board getBoard1(Long boardId) {
		return boardFeign.findByBoardID(boardId);
	}
	
	public Board getBoard2(Long boardId) {
		return board2Feign.findByBoardID(boardId);
	}
	
	public Board saveBoard1(Board board) {
		return boardFeign.saveBoard(board);
	}
	
	public Board saveBoard2(Board board) {
		return board2Feign.saveBoard(board);
	}
	
	public String deleteBoard1All() {
		List<Board> boards = boardFeign.getBoardList();		
		boards.stream().forEach(x -> boardFeign.deleteBoardById(x.getId()));		
		return "OK";
	}
	
	public String deleteBoard2All() {
		List<Board> boards = board2Feign.getBoardList();		
		boards.stream().forEach(x -> board2Feign.deleteBoardById(x.getId()));		
		return "OK";
	}
	

}
