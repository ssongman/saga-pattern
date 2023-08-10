package com.ssongman.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssongman.dto.Board;
import com.ssongman.feign.Board2Feign;
import com.ssongman.feign.BoardFeign;
import com.ssongman.saga.SagaManagerETT;
import com.ssongman.saga.SagaRedisRepository;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service
public class BoardService {
	
	@Autowired
	private BoardFeign boardFeign;
	
	@Autowired
	private Board2Feign board2Feign;	

    @Autowired
    private SagaRedisRepository sagaManagerRepo;    
    
	
	
	// [BoardManager]-------------------------------------------------
	
	public List<Board> getBoardAll() {
		List<Board> boards = new ArrayList<>();
		
		List<Board> board1s = callGetBoard1All();
		List<Board> board2s = callGetBoard2All();

		boards.addAll(board1s);
		boards.addAll(board2s);
		
		return boards;
	}
	
	public Board createBoard(Long boardId) {
		log.info("[createBoard] createBoard job start");		

		// 1. [Saga Instance] start
		SagaManagerETT sagaManagerETT = sagaManagerRepo.save(SagaManagerETT.builder().build());
		List<String> sagaTransactionIDs = new ArrayList<String>();

		// 2. Service1 - Board1
		Board board = Board.builder()
				.title("title" + boardId.toString())
				.content("content" + boardId.toString())
				.writer("Song")
				.hits(5)
				.deleteYn('N')
			    .build();

		String sagaTransactionID1 = callSaveBoard1(board);
		// 성공시
		sagaTransactionIDs.add(sagaTransactionID1);
		sagaManagerETT.setSagaTransactionIDs(sagaTransactionIDs);		
		sagaManagerRepo.save(sagaManagerETT);
		// 실패시 - 보상처리시작
//		callConpenBoard1(board);

		
		// 3. Service2 - Board2
		String sagaTransactionID2 = callSaveBoard2(board);
		// 성공시
		sagaTransactionIDs.add(sagaTransactionID2);
		sagaManagerETT.setSagaTransactionIDs(sagaTransactionIDs);
		sagaManagerRepo.save(sagaManagerETT);
		// 실패시 - 보상처리시작
//		callConpenBoard2(board);
//		callConpenBoard1(board);
		
		
		// 9. Save sagaManager
		sagaManagerRepo.save(sagaManagerETT);
		
		log.info("[createBoard] createBoard job completed!!");
		
		return board;
	}
	
	public String deleteAll() {
		callDeleteBoard1All();
		callDeleteBoard2All();
		return "OK";		
	}
	
	

	
	// [OpenFeign Access]-------------------------------------------------
	
	
	public List<Board> callGetBoard1All() {
		return boardFeign.getBoardList();
	}
	
	public Board callGetBoard1(Long boardId) {
		return boardFeign.findByBoardID(boardId);
	}
	
	public String callSaveBoard1(Board board) {
		return boardFeign.saveBoard(board);
	}
	
	public void callConpenBoard1(Board board) {
		return;
	}
	
	public String callDeleteBoard1All() {
		List<Board> boards = boardFeign.getBoardList();		
		boards.stream().forEach(x -> boardFeign.deleteBoardById(x.getId()));		
		return "OK";
	}
	
	
	
	
	public List<Board> callGetBoard2All() {
		return board2Feign.getBoardList();
	}
	
	public Board callGetBoard2(Long boardId) {
		return board2Feign.findByBoardID(boardId);
	}
	
	public String callSaveBoard2(Board board) {
		return board2Feign.saveBoard(board);
	}
	
	public void callConpenBoard2(Board board) {
		return;
	}
	
	public String callDeleteBoard2All() {
		List<Board> boards = board2Feign.getBoardList();		
		boards.stream().forEach(x -> board2Feign.deleteBoardById(x.getId()));		
		return "OK";
	}
	

}
