package com.ssongman.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class SagaService {

    @Autowired
    private SagaRedisRepository repo;

	public SagaDTO setSagaDto(SagaDTO sagaDto) {
    	log.info("[setSagaDto]-------------");
		return  repo.save(sagaDto);
	}

	public SagaDTO getSagaDto(String id) {
    	log.info("[getSagaDto]-------------");
    	return repo.findById(id).get();
    }

	public void deleteSagaDto(String id) {
    	log.info("[deleteSagaDto]-------------");
    	repo.deleteById(id);
    	return;
    }
    
    

}
