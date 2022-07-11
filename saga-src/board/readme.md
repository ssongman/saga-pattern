



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



```

    
    