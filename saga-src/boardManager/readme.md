



# 1. board1,2 확인 
board Manager test를 위한 board1,2 이 사전에 실행되어 있어야 함

## 1.1 board service 들 확인 

```sh

# board1
curl -i localhost:8081/board/list


# board2
curl -i localhost:8082/board/list



```



# 2. boardManager 

boardManager 확인

```sh

# 조회 - 모두
$ curl localhost:8080/boardManager/getAll


# 조회 - 한건
$ curl localhost:8080/boardManager/get/2

# Board 한건 생성
$ curl localhost:8080/boardManager/create/2

# 모두삭제
$ curl localhost:8080/boardManager/deleteAll

```
