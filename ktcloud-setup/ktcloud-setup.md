#  < KT Cloud Setup >



# 1. 서버생성



## 1) k3s Cluster 용도 vm 생성

ktcloud 에 k3s cluster 용도의 서버 생성

```
master01  ubuntu 2core, 4GB
master02  ubuntu 2core, 4GB
master03  ubuntu 2core, 4GB
worker01  ubuntu 2core, 4GB
worker02  ubuntu 2core, 4GB
worker03  ubuntu 2core, 4GB
```



## 2) port-forwarding set

### (1) ssh 접근용

master02 서버에 user 들 접근 가능하도록 port-forwarding한다.

```
211.254.212.105 : 10021  = master01 : 22
211.254.212.105 : 10022  = master02 : 22
211.254.212.105 : 10023  = master03 : 22

211.254.212.105 : 10031  = worker01 : 22
211.254.212.105 : 10032  = worker02 : 22
211.254.212.105 : 10033  = worker03 : 22
```





## 3) k3s 셋팅



### (1) node root pass 셋팅

passwd: \*****



### (2) master node - HA 구성

```sh
# master01에서
$ curl -sfL https://get.k3s.io | sh -s - --write-kubeconfig-mode 644 --cluster-init

# 확인
$ kubectl version
Client Version: version.Info{Major:"1", Minor:"23", GitVersion:"v1.23.6+k3s1", GitCommit:"418c3fa858b69b12b9cefbcff0526f666a6236b9", GitTreeState:"clean", BuildDate:"2022-04-28T22:16:18Z", GoVersion:"go1.17.5", Compiler:"gc", Platform:"linux/amd64"}
Server Version: version.Info{Major:"1", Minor:"23", GitVersion:"v1.23.6+k3s1", GitCommit:"418c3fa858b69b12b9cefbcff0526f666a6236b9", GitTreeState:"clean", BuildDate:"2022-04-28T22:16:18Z", GoVersion:"go1.17.5", Compiler:"gc", Platform:"linux/amd64"}



# IP/ token 확인
$ cat /var/lib/rancher/k3s/server/node-token
K1096832a7d37c319f56386fc6f604922569d288e858563a7daea451cc3ea783f63::server:43e60b80a724226ba7cc986f30b2b468


# master02, 03 에서
$ export MASTER_TOKEN="K1096832a7d37c319f56386fc6f604922569d288e858563a7daea451cc3ea783f63::server:43e60b80a724226ba7cc986f30b2b468"
  export MASTER_IP="172.27.0.186"

$ curl -sfL https://get.k3s.io | sh -s - --write-kubeconfig-mode 644 --server https://${MASTER_IP}:6443 --token ${MASTER_TOKEN}

…
[INFO]  systemd: Starting k3s-agent   ← 정상 로그




# master01 에서
$ kubectl get nodes
NAME       STATUS   ROLES                       AGE    VERSION
master01   Ready    control-plane,etcd,master   3m8s   v1.23.6+k3s1
master02   Ready    control-plane,etcd,master   46s    v1.23.6+k3s1
master03   Ready    control-plane,etcd,master   32s    v1.23.6+k3s1




# [참고]istio setup을 위한 k3s 설정시 아래 참고
## traefik 을 deploy 하지 않는다. 
## istio 에서 별도 traefic 을 설치하는데 이때 기설치된 controller 가 있으면 충돌 발생함
$ curl -sfL https://get.k3s.io |INSTALL_K3S_EXEC="--no-deploy traefik" sh -

```



- worker node

```sh
# worker node 01,02,03 에서 각각


$ export MASTER_TOKEN="K1096832a7d37c319f56386fc6f604922569d288e858563a7daea451cc3ea783f63::server:43e60b80a724226ba7cc986f30b2b468"
  export MASTER_IP="172.27.0.186"
  

$ curl -sfL https://get.k3s.io | K3S_URL=https://${MASTER_IP}:6443 K3S_TOKEN=${MASTER_TOKEN} sh -

…
[INFO]  systemd: Starting k3s-agent   ← 나오면 정상



# master01 에서
$ kubectl get nodes 
NAME       STATUS   ROLES                       AGE     VERSION
master01   Ready    control-plane,etcd,master   5m12s   v1.23.6+k3s1
master02   Ready    control-plane,etcd,master   2m50s   v1.23.6+k3s1
master03   Ready    control-plane,etcd,master   2m36s   v1.23.6+k3s1
worker01   Ready    <none>                      46s     v1.23.6+k3s1
worker02   Ready    <none>                      45s     v1.23.6+k3s1
worker03   Ready    <none>                      43s     v1.23.6+k3s1



# 참고 - 수동방식으로 시작
$ sudo k3s agent --server https://${MASTER_IP}:6443 --token ${NODE_TOKEN} &


## uninstall
$ sh /usr/local/bin/k3s-killall.sh
  sh /usr/local/bin/k3s-uninstall.sh

```





### (2) kubeconfig 설정

local 에서 직접 kubctl 명령 실행을 위해서는 ~/.kube/config 에 연결정보가 설정되어야 한다.

현재는 /etc/rancher/k3s/k3s.yaml 에 정보가 존재하므로 이를 복사한다. 또한 모든 사용자가 읽을 수 있도록 권한을 부여 한다.

```sh
## root 권한으로 실행
## kubeconfig
$ mkdir -p ~/.kube
$ sudo cp /etc/rancher/k3s/k3s.yaml ~/.kube/config


# 자신만 RW 권한 부여
$ sudo chmod 600 /etc/rancher/k3s/k3s.yaml ~/.kube/config

## 확인
$ kubectl version
Client Version: version.Info{Major:"1", Minor:"23", GitVersion:"v1.23.6+k3s1", GitCommit:"418c3fa858b69b12b9cefbcff0526f666a6236b9", GitTreeState:"clean", BuildDate:"2022-04-28T22:16:18Z", GoVersion:"go1.17.5", Compiler:"gc", Platform:"linux/amd64"}
Server Version: version.Info{Major:"1", Minor:"23", GitVersion:"v1.23.6+k3s1", GitCommit:"418c3fa858b69b12b9cefbcff0526f666a6236b9", GitTreeState:"clean", BuildDate:"2022-04-28T22:16:18Z", GoVersion:"go1.17.5", Compiler:"gc", Platform:"linux/amd64"}

```

root 권한자가 아닌 다른 사용자도 사용하려면 위와 동일하게 수행해야한다.





### (3) alias 정의

```sh
$ cat > ~/env
alias k='kubectl'
alias kk='kubectl -n kube-system'
alias ks='k -n song'
alias kkf='k -n kafka'
alias krs='k -n kafka'

## alias 를 적용하려면 source 명령 수행
$ source ~/env
```



### (5) ingress controller port-forwarding 

```sh
$ kubectl -n kube-system get svc
NAME             TYPE           CLUSTER-IP      EXTERNAL-IP                                                                  PORT(S)                      AGE
kube-dns         ClusterIP      10.43.0.10      <none>                                                                       53/UDP,53/TCP,9153/TCP       9m13s
metrics-server   ClusterIP      10.43.164.203   <none>                                                                       443/TCP                      9m12s
traefik          LoadBalancer   10.43.17.18     172.27.0.153,172.27.0.176,172.27.0.186,172.27.0.238,172.27.0.39,172.27.0.6   80:30176/TCP,443:30513/TCP   7m42s


```

80:30176 / 443:30513 node port 로 접근 가능한 것을 알수 있다.

master01 서버에 port-forwarding한다.

```
211.254.212.105 : 80   = master01 : 30176 
211.254.212.105 : 443  = master01 : 30513 
```

그러므로 우리는 211.254.212.105:80 으로 call 을 보내면 된다.  대신 Cluster 내 진입후 자신의 service 를 찾기 위한 host 를 같이 보내야 한다. 



### (6) Clean Up

```sh
# uninstall
$ sh /usr/local/bin/k3s-killall.sh
$ sh /usr/local/bin/k3s-uninstall.sh 

```





# < MariaDB on KTCloud >

# 1. Maria DB

>  Maria DB  install



## 1) namespace 생성

```sh
# namespace 생성
$ kubectl create ns song

# 확인
$ kubectl get ns

# alias 설정
$ alias ks='kubectl -n song'
```





## 2) Helm Install

```sh
$ helm repo add bitnami https://charts.bitnami.com/bitnami


$ helm search repo mariadb
NAME                    CHART VERSION   APP VERSION     DESCRIPTION
bitnami/mariadb         11.0.14         10.6.8          MariaDB is an open source, community-developed ...
bitnami/mariadb-galera  7.3.6           10.6.8          MariaDB Galera is a multi-primary database clus...
bitnami/phpmyadmin      10.1.11         5.2.0           phpMyAdmin is a free software tool written in P...



$ helm -n song ls


$ helm -n song install mariadb bitnami/mariadb -f values.yaml


or

# Set 명령 수행
$ helm -n song install mariadb bitnami/mariadb \
   --set architecture=replication \
   --set auth.rootPassword=rootpass! \
   --set auth.username=song \
   --set auth.password=songpass! \
   --set auth.replicationUser=replicator \
   --set auth.replicationPassword=replicatorpass! \
   --set primary.persistence.enabled=false \
   --set secondary.persistence.enabled=false


Tip:

  Watch the deployment status using the command: kubectl get pods -w --namespace song -l app.kubernetes.io/instance=mariadb

Services:

  echo Primary: mariadb-primary.song.svc.cluster.local:3306
  echo Secondary: mariadb-secondary.song.svc.cluster.local:3306

Administrator credentials:

  Username: root
  Password : $(kubectl get secret --namespace song mariadb -o jsonpath="{.data.mariadb-root-password}" | base64 -d)

To connect to your database:

  1. Run a pod that you can use as a client:

      kubectl run mariadb-client --rm --tty -i --restart='Never' --image  docker.io/bitnami/mariadb:10.6.8-debian-11-r9 --namespace song --command -- bash

  2. To connect to primary service (read/write):

      mysql -h mariadb-primary.song.svc.cluster.local -uroot -p my_database

  3. To connect to secondary service (read-only):

      mysql -h mariadb-secondary.song.svc.cluster.local -uroot -p my_database

To upgrade this helm chart:

  1. Obtain the password as described on the 'Administrator credentials' section and set the 'auth.rootPassword' parameter as shown below:

      ROOT_PASSWORD=$(kubectl get secret --namespace song mariadb -o jsonpath="{.data.mariadb-root-password}" | base64 -d)
      helm upgrade --namespace song mariadb bitnami/mariadb --set auth.rootPassword=$ROOT_PASSWORD



# 삭제
$ helm -n song delete mariadb 

# upgrade
$ helm -n song upgrade mariadb .


```







# 2. Accessing Kafka



## 1) Internal Access



### (1) Maria DB Service 확인



```sh
$ kubectl -n song get svc

NAME                TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
mariadb-primary     NodePort    10.43.119.81   <none>        3306:32300/TCP   2d1h
mariadb-secondary   ClusterIP   10.43.15.149   <none>        3306/TCP         2d1h
my-phpmyadmin       ClusterIP   10.43.35.51    <none>        80/TCP,443/TCP   2d


```







### (2) mariadb cli 로 확인



#### mariadb cli 설치

```sh
# mariadb cli 설치
$ kubectl -n song run mariadb-client --rm  -it --restart='Never' \
    --image=docker.io/bitnami/mariadb:10.3.22-debian-10-r27 \
    -- bash

$ mysql -h mariadb-primary.song.svc.cluster.local -uroot -p
rootpass!


```









## 2) External Access(Node Port)



### (1) Node Port

#### Node IP 확인

- KT Cloud 에서는 Virtual Router IP 를 사용한다.

```sh

$ kubectl -n song get svc

NAME                TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
mariadb-primary     NodePort    10.43.119.81   <none>        3306:32300/TCP   2d1h
mariadb-secondary   ClusterIP   10.43.15.149   <none>        3306/TCP         2d1h
my-phpmyadmin       ClusterIP   10.43.35.51    <none>        80/TCP,443/TCP   2d


# KT Cloud 에서는 Virtual Router IP
211.254.212.105

```







### (2) kafkacat 로 확인

Local PC(Cluster 외부) 에서  kafka 접근 가능여부를 확인하기 위해 kafkacat 을 docker 로 PC 에 설치하자.

#### docker run

kafkacat 을 docker 로 설치한다.

docker CLI terminal 에서 수행한다.

```sh

# 실행
$ docker run -name mariadb-client --rm  -it \
    docker.io/bitnami/mariadb:10.3.22-debian-10-r27 \
    -- bash

$ mysql -h 211.254.212.105 --port 32300 -uroot -prootpass!

```



#### select 

```sh
$ 

```





# 3. phpmyadmin

https://artifacthub.io/packages/helm/bitnami/phpmyadmin





## 1) helm install

```sg
$ helm -n song install my-phpmyadmin bitnami/phpmyadmin


** Please be patient while the chart is being deployed **

1. Get the application URL by running these commands:

  echo "phpMyAdmin URL: http://127.0.0.1:80"
  kubectl port-forward --namespace song svc/my-phpmyadmin 80:80

2. How to log in
phpMyAdmin has not been configure to point to a specific database. Please provide the db host,
username and password at log in or upgrade the release with a specific database:

$ helm upgrade my-phpmyadmin bitnami/phpmyadmin --set db.host=mydb


```



## 2) Ingress 

````sh
$ kubectl -n song apply -f - <<EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  annotations:
    kubernetes.io/ingress.class: traefik
  name: phpmyadmin-ingress
  namespace: song
spec:
  rules:
  - host: phpmyadmin.song.ktcloud.211.254.212.105.nip.io
    http:
      paths:
      - backend:
          service:
            name: my-phpmyadmin
            port:
              number: 80
        path: /
        pathType: Prefix
EOF

````







# < Redis on KT Cloud >





# 1. Install 준비



## 1) helm install



### (1) helm client download

helm client 를 local 에 설치해 보자.

```sh
# root 권한으로 수행
## 임시 디렉토리를 하나 만들자.
$ mkdir -p ~/helm/
$ cd ~/helm/

$ wget https://get.helm.sh/helm-v3.9.0-linux-amd64.tar.gz
$ tar -zxvf helm-v3.9.0-linux-amd64.tar.gz
$ mv linux-amd64/helm /usr/local/bin/helm

$ ll /usr/local/bin/helm*
-rwxr-xr-x 1 song song 46182400 May 19 01:45 /usr/local/bin/helm*


# 권한정리
$ sudo chmod 600  /home/song/.kube/config


# 확인
$ helm version
version.BuildInfo{Version:"v3.9.0", GitCommit:"7ceeda6c585217a19a1131663d8cd1f7d641b2a7", GitTreeState:"clean", GoVersion:"go1.17.5"}

$ helm -n user01 ls
NAME    NAMESPACE       REVISION        UPDATED STATUS  CHART   APP VERSION

```





## 2) namespace 생성

```sh
$ kubectl create ns redis-system

$ alias krs='kubectl -n redis-system'

```



# 2. Redis Install

External (Cluster 외부) 에서 access 하기 위해서 node port 를 이용해야 한다.

하지만 Redis Cluster 의 경우 접근해야 할 Master Node 가 두개 이상이며 해당 데이터가 저장된 위치를 찾아 redirect 된다.

이때 redirect 가 정확히 이루어지려면 Client 가 인식가능한 Node 주소를 알아야 한다.

하지만 Redis Cluster 는 원격지 Client 가 인식가능한 Node 들의 DNS를 지원하지 않는다.

결국 Redis Cluster 는 PRD환경과 같이 Kubernetes Cluster 내에서는 사용가능하지만 

개발자 PC에서 연결이 필요한 DEV환경에서는 적절치 않다.

그러므로 redis-cluster 가 아닌 redis 로 설치 하여 테스트를 진행한다.



## 1) Redis(Single Master) Install



### (1)  Redis Install



#### helm search

추가된 bitnami repo에서 redis-cluster 를 찾는다.

```sh
$ helm search repo redis
bitnami/redis                                   16.13.0         6.2.7           Redis(R) is an open source, advanced key-value ...
bitnami/redis-cluster                           7.6.3           6.2.7           Redis(R) is an open source, scalable, distribut...
prometheus-community/prometheus-redis-exporter  4.8.0           1.27.0          Prometheus exporter for Redis metrics

```

bitnami/redis



#### helm install

```sh
$ cd ~/song/helm/charts/redis

# helm install
# master 1, slave 3 실행
$ helm -n redis-system install my-release bitnami/redis \
    --set global.redis.password=new1234 \
    --set image.registry=docker.io \
    --set master.persistence.enabled=false \
    --set master.service.type=NodePort \
    --set master.service.nodePorts.redis=32200 \
    --set replica.replicaCount=3 \
    --set replica.persistence.enabled=false \
    --set replica.service.type=NodePort \
    --set replica.service.nodePorts.redis=32210

##
    my-release-redis-master.redis-system.svc.cluster.local for read/write operations (port 6379)
    my-release-redis-replicas.redis-system.svc.cluster.local for read-only operations (port 6379)



To get your password run:

    export REDIS_PASSWORD=$(kubectl get secret --namespace redis-system my-release-redis -o jsonpath="{.data.redis-password}" | base64 -d)

To connect to your Redis&reg; server:

1. Run a Redis&reg; pod that you can use as a client:

   kubectl run --namespace redis-system redis-client --restart='Never'  --env REDIS_PASSWORD=$REDIS_PASSWORD  --image docker.io/bitnami/redis:6.2.7-debian-11-r9 --command -- sleep infinity

   Use the following command to attach to the pod:

   kubectl exec --tty -i redis-client \
   --namespace redis-system -- bash

2. Connect using the Redis&reg; CLI:
   REDISCLI_AUTH="$REDIS_PASSWORD" redis-cli -h my-release-redis-master
   REDISCLI_AUTH="$REDIS_PASSWORD" redis-cli -h my-release-redis-replicas

To connect to your database from outside the cluster execute the following commands:

    export NODE_IP=$(kubectl get nodes --namespace redis-system -o jsonpath="{.items[0].status.addresses[0].address}")
    export NODE_PORT=$(kubectl get --namespace redis-system -o jsonpath="{.spec.ports[0].nodePort}" services my-release-redis-master)
    REDISCLI_AUTH="$REDIS_PASSWORD" redis-cli -h $NODE_IP -p $NODE_PORT


# 확인
$ helm -n redis-system ls
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART           APP VERSION
my-release      redis-system    1               2022-07-01 01:21:32.6958631 +0900 KST   deployed        redis-16.13.1   6.2.7


```

my-release-redis-master 는 read/write 용도로 사용되며 my-release-redis-replicas 는 read-only 용도로 사용된다.





### (2) Chart Fetch 이후 Install

설치 과정에서 chart 를 다운 받지 못한다면 Chart 를 fetch 받아서 설치하자.



#### Chart Fetch

helm chart 를 fetch 받는다.

```sh
# chart 를 저장할 적당한 위치로 이동
$ cd ~/song/helm/charts

$ helm fetch bitnami/redis

$ ls
redis-16.13.0.tgz

$ tar -xzvf redis-16.13.0.tgz
...

$ cd redis

$ ls -ltr
-rw-r--r-- 1 root root    220 Jun 24 17:57 Chart.lock
drwxr-xr-x 3 root root   4096 Jun 26 07:15 charts/
-rw-r--r-- 1 root root    773 Jun 24 17:57 Chart.yaml
-rw-r--r-- 1 root root    333 Jun 24 17:57 .helmignore
drwxr-xr-x 2 root root   4096 Jun 26 07:15 img/
-rw-r--r-- 1 root root 100896 Jun 24 17:57 README.md
drwxr-xr-x 5 root root   4096 Jun 26 07:15 templates/
-rw-r--r-- 1 root root   4483 Jun 24 17:57 values.schema.json
-rw-r--r-- 1 root root  68558 Jun 24 17:57 values.yaml

```



#### helm install

```sh
$ cd ~/song/helm/charts/redis   

# helm install
# node 2, replicas 1 이므로 Master / Slave 한개씩 사용됨
$ helm -n redis-system install my-release . \
    --set global.redis.password=new1234 \
    --set image.registry=docker.io \
    --set master.persistence.enabled=false \
    --set master.service.type=NodePort \
    --set master.service.nodePorts.redis=32200 \
    --set replica.replicaCount=3 \
    --set replica.persistence.enabled=false \
    --set replica.service.type=NodePort \
    --set replica.service.nodePorts.redis=32210

##
my-release-redis-master.redis-system.svc.cluster.local for read/write operations (port 6379)
my-release-redis-replicas.redis-system.svc.cluster.local for read-only operations (port 6379)


$ helm -n redis-system ls
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
my-release      redis-system    1               2022-06-26 06:59:30.08278938 +0000 UTC  deployed        redis-cluster-7.6.3     6.2.7


# 삭제시
$ helm -n redis-system delete my-release

```

my-release-redis-master 는 read/write 용도로 사용되며 my-release-redis-replicas 는 read-only 용도로 사용된다.



### (3) pod / svc 확인

```sh
$ krs get pod
NAME                          READY   STATUS    RESTARTS   AGE
my-release-redis-master-0     1/1     Running   0          6m30s
my-release-redis-replicas-0   1/1     Running   0          6m30s
my-release-redis-replicas-1   1/1     Running   0          5m34s
my-release-redis-replicas-2   1/1     Running   0          5m9s


$ krs get svc
NAME                        TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
my-release-redis-headless   ClusterIP   None            <none>        6379/TCP         6m40s
my-release-redis-master     NodePort    10.108.249.49   <none>        6379:32200/TCP   6m40s
my-release-redis-replicas   NodePort    10.96.105.76    <none>        6379:32210/TCP   6m40s

```



### (4) Catch up

```sh
# 삭제시
$ helm -n redis-system delete my-release

```





## 2) External Access

redis client를 cluster 외부에서 실행후 접근하는 방법을 알아보자.

### (1) Redis client 확인

#### docker redis client

local pc 에서 access 테스트를 위해 docker redis client 를 설치하자.

```sh
## redis-client 용도로 docker client 를 실행한다.
$ docker run --name redis-client -d --rm --user root docker.io/bitnami/redis-cluster:6.2.7-debian-11-r3 sleep 365d

## docker 내에 진입후
$ docker exec -it redis-client bash

## Local PC IP로 cluster mode 접근
$ redis-cli -h 211.254.212.105 -c -a new1234 -p 32200



# KT Cloud 에서는 Virtual Router IP




```



### (2) set/get 확인

```
192.168.31.1:32200>  set a 1
OK
192.168.31.1:32200> set b 2
OK
192.168.31.1:32200> set c 3
OK
192.168.31.1:32200> get a
"1"
192.168.31.1:32200> get b
"2"
192.168.31.1:32200> get c
"3"

```





# 4. P3X Redis UI

참고링크
https://www.electronjs.org/apps/p3x-redis-ui

https://github.com/patrikx3/redis-ui/blob/master/k8s/manifests/service.yaml

Redis DB 관리를 위한  편리한 데이터베이스 GUI app이며  WEB  UI 와 Desktop App 에서 작동한다.

P3X Web UI 를 kubernetes 에 설치해 보자.



## 1) redis-ui deploy

아래 yaml  manifest file을 활용하여 configmap, deployment, service, ingress 를 일괄 실행한다.

```sh
$ cd ~/song/del


$ cat ./redis/redisui/11.p3xredisui.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: p3x-redis-ui-settings
data:
  .p3xrs-conns.json: |
    {
      "list": [
        {
          "name": "cluster",
          "host": "my-release-redis-master",
          "port": 6379,
          "password": "new1234",
          "id": "unique"
        }
      ],
      "license": ""
    }
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: p3x-redis-ui
spec:
  replicas: 1
  selector:
    matchLabels:
      app.kubernetes.io/name: p3x-redis-ui
  template:
    metadata:
      labels:
        app.kubernetes.io/name: p3x-redis-ui
    spec:
      containers:
      - name: p3x-redis-ui
        image: patrikx3/p3x-redis-ui
        ports:
        - name: p3x-redis-ui
          containerPort: 7843
        volumeMounts:
        - name: p3x-redis-ui-settings
          mountPath: /settings/.p3xrs-conns.json
          subPath: .p3xrs-conns.json
      volumes:
      - name: p3x-redis-ui-settings
        configMap:
          name: p3x-redis-ui-settings
---
apiVersion: v1
kind: Service
metadata:
  name: p3x-redis-ui-service
  labels:
    app.kubernetes.io/name: p3x-redis-ui-service
spec:
  ports:
  - port: 7843
    targetPort: p3x-redis-ui
    name: p3x-redis-ui
  selector:
    app.kubernetes.io/name: p3x-redis-ui
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: p3x-redis-ui-ingress
  annotations:
    # kubernetes.io/ingress.class: nginx
    kubernetes.io/ingress.class: traefik
    # cert-manager support
    # cert-manager.io/cluster-issuer: letsencrypt
    # oauth2-proxy support
    # nginx.ingress.kubernetes.io/auth-url: "https://$host/oauth2/auth"
    # nginx.ingress.kubernetes.io/auth-signin: "https://$host/oauth2/start?rd=$escaped_request_uri"
spec:
  # tls:
  # - hosts: [p3x-redis-ui.example.com]
  #   secretName: p3x-redis-ui-tls
  rules:
  - host: p3xredisui.redis-system.ktcloud.211.254.212.105.nip.io
    http:
      paths:
      - backend:
          service:
            name: p3x-redis-ui-service
            port:
              number: 7843
        path: /
        pathType: Prefix
---

# install
$ kubectl -n redis-system apply -f ./redis/redisui/11.p3xredisui.yaml


# 삭제시
$ kubectl -n redis-system delete -f ./redis/redisui/11.p3xredisui.yaml



```



## 2) ui 확인

http://p3xredisui.redis-system.ktcloud.211.254.212.105.nip.io/main/key/people

![image-20220626181624749](ktcloud-setup.assets/image-20220626181624749.png)











# 5. ACL

Redis 6.0 이상부터는 계정별 access 수준을 정의할 수 있다.  

이러한 ACL 기능을 이용해서 아래와 같은 계정을 관리 할 수 있다.

- 읽기전용 계정생성도 가능

- 특정 프리픽스로 시작하는 Key 만 access 가능하도록 하는 계정 생성



## 1) ACL 기본명령



```sh
$ redis-cli -h 211.254.212.105 -c -a new1234 -p 32200

# 계정 목록
211.254.212.105:32200> acl list
1) "user default on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all"


# 계정 추가
211.254.212.105:32200> acl setuser supersong on >new1234 allcommands allkeys
OK
211.254.212.105:32200> acl setuser tempsong on >new1234 allcommands allkeys
OK


211.254.212.105:32200> acl list
1) "user default on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all"
2) "user supersong on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all"
3) "user tempsong on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all"


# 계정 전환
211.254.212.105:32200> acl whoami
"default"

211.254.212.105:32200> auth supersong new1234
OK
211.254.212.105:32200> acl whoami
"supersong"

211.254.212.105:32200> auth default new1234
OK

# 계정 삭제
211.254.212.105:32200> acl deluser tempsong
(integer) 1

```





## 2) 읽기전용 계정 생성

- 읽기전용 계정 테스트

```sh
# 계정생성
211.254.212.105:32200> acl setuser readonlysong on >new1234 allcommands allkeys -set +get
OK

211.254.212.105:32200> acl list
1) "user default on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all"
2) "user readonlysong on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all -set"
3) "user supersong on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all"


# 읽기는 가능
127.0.0.1:6379> get a
"1"

# 쓰기는 불가능
211.254.212.105:32200> set a 1
(error) NOPERM this user has no permissions to run the 'set' command or its subcommand

```





## 3) 특정 key만 접근 허용

- song으로 로그인 하면 song으로 시작하는 key 만 get/set 가능하도록 설정

```sh
# song 으로 시작하는 key 만 접근가능하도록 설정


211.254.212.105:32200> acl setuser song on >new1234 allcommands allkeys
OK
211.254.212.105:32200> acl list
1) "user default on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all"
2) "user readonlysong on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all -set"
3) "user song on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all"
4) "user supersong on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all"


211.254.212.105:32200> acl setuser song resetkeys ~song*
OK


211.254.212.105:32200> acl list
1) "user default on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all"
2) "user readonlysong on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all -set"
3) "user song on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~song* &* +@all"
4) "user supersong on #65fd3b5c243ea857f91daef8e3d5c203fa045f33e034861998b9d74cc42ceb24 ~* &* +@all"


211.254.212.105:32200> auth song new1234
OK

211.254.212.105:32200> acl whoami
"song"


# set 명령 테스트
211.254.212.105:32200> set a 1
(error) NOPERM this user has no permissions to access one of the keys used as arguments

211.254.212.105:32200> set song_a 1
OK

# get 명령 테스트
211.254.212.105:32200> get a
(error) NOPERM this user has no permissions to access one of the keys used as arguments


211.254.212.105:32200> get song_a
"1"


```







# 6. Java Sample



## 1) Jedis vs Lettuce

참고: https://jojoldu.tistory.com/418

- Java 의 Redis Client 는 크게 Jedis 와 Lettuce  가 있음.

- 초기에는 Jedis 를 많이 사용했으나 현재는 Lettuce 를 많이 사용하는 추세임.

- Jedis 의 단점
  -  멀티 쓰레드 불안정, Pool 한계 등
- Lettuce 의 장점
  - Netty 기반으로 비동기 지원 가능 등

- 결국 Spring Boot 2.0 부터 Jedis 가 기본 클라이언트에서 deprecated 되고 Lettuce 가 탑재되었음





## 2) Spring Boot Sample

sample source github link



- pom.xml

```xml
...
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
...
```



- application.yaml

```yaml
spring:
  redis:
    lettuce:
      pool:
        max-active: 10
        max-idle: 10
        min-idle: 2
    host: localhost
    port: 6379
    password: 'new1234'
```



- 참고 : 각 항목들에 대한 설명

| 변수                         | 기본값                             | 설명                                                         |
| ---------------------------- | ---------------------------------- | ------------------------------------------------------------ |
| spring.redis.database        | 0                                  | 커넥션  팩토리에 사용되는 데이터베이스 인덱스                |
| spring.redis.host            | localhost                          | 레디스  서버 호스트                                          |
| spring.redis.password        | 레디스  서버 로그인 패스워드       |                                                              |
| spring.redis.pool.max-active | 8                                  | pool에  할당될 수 있는 커넥션 최대수 (음수로 하면 무제한)    |
| spring.redis.pool.max-idle   | 8                                  | pool의  "idle" 커넥션 최대수 (음수로 하면 무제한)            |
| spring.redis.pool.max-wait   | -1                                 | pool이  바닥났을 때 예외발생 전에 커넥션 할당 차단의 최대 시간 (단위: 밀리세컨드, 음수는 무제한 차단) |
| spring.redis.pool.min-idle   | 0                                  | 풀에서  관리하는 idle 커넥션의 최소 수 대상 (양수일 때만 유효) |
| spring.redis.port            | 6379                               | 레디스  서버 포트                                            |
| spring.redis.sentinel.master | 레디스  서버 이름                  |                                                              |
| spring.redis.sentinel.nodes  | 호스트:포트  쌍 목록 (콤마로 구분) |                                                              |
| spring.redis.timeout         | 0                                  | 커넥션  타임아웃 (단위: 밀리세컨드)                          |



Redis에 Connection을 하기 위한 RedisConnectionFactory 생성

```java
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }
}

```

**RedisConnectionFactory 인터페이스를 통해 LettuceConnectionFactory를 생성하여 반환합니다.**



```java
@Getter
@RedisHash(value = "people", timeToLive = 30)
public class Person {

    @Id
    private String id;
    private String name;
    private Integer age;
    private LocalDateTime createdAt;

    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
        this.createdAt = LocalDateTime.now();
    }
}
```

- Redis 에 저장할 자료구조인 객체를 정의함

- 일반적인 객체 선언 후 @RedisHash 를 붙임
  - value  값이 Redis 의 key prefix 로 사용됨
  - timeToLive : 만료시간을 seconds 단위로 설정할 수 있음 
    - 기본값은 만료시간이 없는 -1L 임.
- @Id 어노테이션이 붙은 필드가 Redis Key 값이 되며 null 로 세팅하면 랜덤값이 설정됨
  - keyspace 와 합쳐져서 레디스에 저장된 최종 키 값은 keyspace:id 가 됨
    - key 생성형식: "people:{id}"





