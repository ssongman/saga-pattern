

# 1. jib build

```sh
$ mvn compile jib:build

# docker daemon 으로 build
$ mvn compile jib:dockerbuild


```




# 2. docker 로 실행

## 2.1 docker pull

```sh
$ docker rmi -f docker.io/ssongman/board
$ docker pull docker.io/ssongman/board


```


## 2.2 board1 실행

```sh
$ docker rm -f board
$ docker run -d --name board -p 8081:8080 docker.io/ssongman/board

# 1) 조회
curl -i localhost:8081/board/list

# 2) 조회: 특정 index 조회
curl -i localhost:8081/board/1



```


## 2.3 board2 실행

```sh
$ docker rm -f board2
$ docker run -d --name board2 -p 8082:8080 docker.io/ssongman/board

# 1) 조회
curl -i localhost:8082/board/list

# 2) 조회: 특정 index 조회
curl -i localhost:8082/board/2



```


## 2.4 [참고]podman으로 실행

```sh

$ podman run -d --name board -p 8081:8080 docker.io/ssongman/board


```





# 3. kubernetes 로 실행

```sh
$ kubectl -n song create board --image=ssongman/board


```







# 4. API 사용법

```sh

# 1) 조회
curl -i localhost:8081/board/list

# 2) 조회: 특정 index 조회
curl -i localhost:8081/board/2

# 3) 생성
curl -X POST -i localhost:8081/board/create \
   --header 'Content-Type: application/json' \
   -d '{
        "title": "title1",
        "content": "content1",
        "writer": "honggildong",
        "hits": 0,
        "deleteYn": "N",
        "createdDate": "2022-08-07T23:26:00",
        "modifiedDate": null
    }'

# 3-2) 생성 - createdDate 없이
curl -X POST -i localhost:8081/board/create \
   --header 'Content-Type: application/json' \
   -d '{
        "title": "title1",
        "content": "content1",
        "writer": "honggildong",
        "hits": 0,
        "deleteYn": "N"
    }'

# 4) 삭제
curl -X DELETE -i localhost:8081/board/1


```



# 5. h2 db console 확인
```

$ localhost:8081/h2-console
jdbc:h2:mem:testdb
root
rootpass!

```    
    