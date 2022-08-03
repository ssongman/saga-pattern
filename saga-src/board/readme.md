

# 1. jib build

```sh
$ mvn compile job:build

```




# 2. docker 로 실행

```sh
$ docker run -d --name board -p 8080:8080 ssongman/board


```



# 3. kubernetes 로 실행

```sh
$ kubectl -n song create board --image=ssongman/board


```







# 4. API 사용법

```sh

# 1) 조회
curl -i localhost:8080/board/list

# 2) 조회: 특정 index 조회
curl -i localhost:8080/board/2

# 3) 생성
curl -X POST -i localhost:8080/board/create \
   --header 'Content-Type: application/json' \
   -d '{
        "title": "1title",
        "content": "1content",
        "writer": "honggildong",
        "hits": 0,
        "deleteYn": "N",
        "createdDate": "2022-07-11T23:26:00",
        "modifiedDate": null
    }'

# 3-2) 생성 - createdDate 없이
curl -X POST -i localhost:8080/board/create \
   --header 'Content-Type: application/json' \
   -d '{
        "title": "1title",
        "content": "1content",
        "writer": "honggildong",
        "hits": 0,
        "deleteYn": "N"
    }'

# 4) 삭제
curl -X DELETE -i localhost:8080/board/3


```



# 5. db 확인
```

$ localhost:8080/h2-console
jdbc:h2:mem:testdb
root
rootpass!

```    
    