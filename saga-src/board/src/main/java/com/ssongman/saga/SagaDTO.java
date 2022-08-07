package com.ssongman.saga;


import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.ssongman.board.entity.Board;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
@RedisHash(value = "sagaTransaction", timeToLive = 3600)
public class SagaDTO {
	
	@Id
	private String sagaId;
	private Board data;
	
	@Builder
	public SagaDTO(String sagaId, Board data) {
		this.sagaId = sagaId;
		this.data = data;
	}
	
	
	
	
		
}
