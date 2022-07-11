package com.ssongman.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssongman.board.entity.Board;
import com.ssongman.board.entity.BoardRepository;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;

	// save / Update
	@Transactional
	public Board save(Board board) {
		return boardRepository.save(board);
	}	

	// Read
	@Transactional(readOnly = true)
	public Board findById(Long boardId) {
		return boardRepository.findById(boardId).get();
	}

	// Read All
	@Transactional(readOnly = true)
	public List<Board> findAll() {
		return boardRepository.findAll();
	}
	
	// Delete
	@Transactional
	public void delete(Board board) {
		boardRepository.delete(board);
		return;
	}
	
	// DeleteById
	@Transactional
	public void deleteById(Long boardId) {
		boardRepository.deleteById(boardId);
		return;
	}

}
