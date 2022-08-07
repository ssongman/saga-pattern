# Saga Pattern 분석(진행중)

> 프로젝트에서 쉽게 적용 가능한 Saga Pattern Framwork 개발을 위한 분석



본 분석 문서는 개발자들의 최소한의 개발시간 투자로 Saga Pattern 을 적용한 Framwork 을 개발하기 위함이다..



# 1. 분석서 ( [상세 내용 보기](./SagaDesign/saga_pattern.md) )  

- Orchestration-Based Saga
- Saga Pattern 설계
  - 성공시나리오 (Sequence Diagram)
  - 보상(실패) 시나리오(Sequence Diagram)
- Saga Manager 설계
  - ER
  - Structure



# 2. Source

- boardManager -  saga-src/boardManager
- board Service1 - saga-src/board
- board Service2 - saga-src/board







# 8. KT Cloud Setup ( [상세 내용 보기](./ktcloud-setup/ktcloud-setup.md) )  

## 1) MariaDB on KT Cloud

- MariaDB Primary / Secondary

- phpmyadmin

  

  

## 2) Redis on KT Cloud

- helm install
- Redis Cluster / Redis Install
- P3X Redis UI
- ACL
- Java Sample





# 9. 진행경과



- [x] 분석서 작성

  - [ ] Design

- [ ] KTCloud

  - [x] MariaDB Setting

    - [x] MariaDB Setting

    - [x] PhpAdmin 

      http://phpmyadmin.song.ktcloud.211.254.212.105.nip.io/

  - [x] Redis Setting

    - [x] P3X Admin

      http://p3xredisui.redis-system.ktcloud.211.254.212.105.nip.io/
      
      

- [ ] 개발

  - [ ] board Service
    - [x] Local DB CRUD
      - [x] MariaDB CRUD
      - [x] H2 CRUD
    - [x] Controller
      - [x] GET/POST/DELETE
      - [ ] Saga Transaction
    - [x] build
      - [x] jib build
    - [ ] Saga Transaction
      - [ ] DTO - Saga ID 등
      - [ ] Rest Controller 
      - [ ] DB transaction
      - [ ] Redis Save
  - [ ] boardManager
    - [ ] board Service call
      - [x] OpenFeign
      - [ ] Kafka pub/sub
    - [ ] Saga Transaction
      - [ ] Saga Start
        - [ ] Redis Save
      - [ ] Saga Process
        - [ ] DTO - Saga ID 등
        - [ ] Rest Controller
      - [ ] Saga End
        - [ ] Redis Save



