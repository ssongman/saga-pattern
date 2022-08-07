package com.ssongman.saga;


import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RedisHash(value = "SagaManager", timeToLive = 3600)
public class SagaManagerETT {
	
	@Id
	private String sagaId;
	private List<String> sagaTransactionIDs;
	
	@Builder
	public SagaManagerETT(String sagaId, List<String> sagaTransactionIDs) {
//		super();
		this.sagaId = sagaId;
		this.sagaTransactionIDs = sagaTransactionIDs;
	}
	
		
}
