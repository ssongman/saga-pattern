package com.ssongman.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ssongman.dto.Board;
import com.ssongman.saga.SagaManagerETT;


@FeignClient(name = "board", url = "http://localhost:8081")
public interface BoardFeign {
	
	@GetMapping("/board/{id}")
	Board findByBoardID(@PathVariable("id") Long boardId);
	
	@PostMapping("/board/create")
	String saveBoard(@RequestBody Board board);
	
	@DeleteMapping("/board/{id}")
	void deleteBoardById(@PathVariable("id") Long boardId);
	
	@GetMapping("/board/list")
	List<Board> getBoardList();
	
}
