package com.ssongman.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SagaService {

    @Autowired
    private SagaRedisRepository repo;

	public SagaManagerETT setSagaDto(SagaManagerETT sagaDto) {
    	System.out.println("[setSagaDto]-----");
		return  repo.save(sagaDto);
	}

	public SagaManagerETT getSagaDto(String id) {
    	System.out.println("[getSagaDto]-----");    	
    	return repo.findById(id).get();
    }

	public void deleteSagaDto(String id) {
    	System.out.println("[deleteSagaDto]-----");
    	repo.deleteById(id);
    	return;
    }
    
    

}
