本次使用的RocketMQ消息中间件的版本是4.1.0-incubating版本其他版本与该版本类似<br>
首先是环境的部署与搭建(双机Master服务器环境)

环境：(以下是本人安装环境与步骤,可参考)

Linux  centos 7
RocketMQ 4.1.0
java 1.8
maven 3.3.9

### 单机环境搭建请看最后面

1.[下载rocketmq](http://rocketmq.apache.org/release_notes/release-notes-4.1.0-incubating/)
推荐下载Binary（不需进行打包）<br>

#### 注意：因为是双主机模式,所以下面的步骤需要在两台Liunx服务器都完成<br>
2.将下载文件传输到liunx服务器(这里本人使用的是Linux服务器root账户)<br>

MAC版:
```bash
scp /Users/***/Downloads/rocketmq-all-4.1.0-incubating-bin-release.zip  root@Linux服务器IP地址:/usr/local/
```

Win版:
```bash
pscp windows本地路径 root@Linux服务器IP地址:/usr/local<br>
```
注意：windows 需要下载pscp.exe文件(具体下载地址可google或百度)，并将其存放入C:\Windows\System32文件夹下，然后可在windows命令框中直接输入上面命令。<br>


3.解压文件(root账户登录服务器linux)<br>

进入安装包目录:
```bash
cd /usr/local/
```

解压安装包:
```bash
unzip rocketmq-all-4.1.0-incubating-bin-release.zip
```

重命名解压文件(方便操作,根据个人喜好,可以改名也可不改):
```bash
mv rocketmq-all-4.1.0-incubating-bin-release rocketmq<br>
```

4.修改两台主机hosts信息<br>
```bash
vim /etc/hosts
```
添加如下信息：(ip地址改为自己的服务器地址)<br>
```bash
192.168.1.103 mqnameserver1

192.168.1.105 mqnameserver2
```
5.创建存储路径【两台机器】<br>
```bash
  cd /usr/local
  
  mkdir /usr/local/rocketmq/store
  
  mkdir /usr/local/rocketmq/store/commitlog
  
  mkdir /usr/local/rocketmq/store/consumequeue
  
  mkdir /usr/local/rocketmq/store/index
```
6. RocketMQ配置文件【两台机器】

```bash
vim /usr/local/rocketmq/conf/2m-noslave/broker-a.properties

vim /usr/local/rocketmq/conf/2m-noslave/broker-b.properties
```
复制并修改一下代码即可
```bash
#所属集群名字
brokerClusterName=rocketmq-cluster
#broker名字，注意此处不同的配置文件填写的不一样
brokerName=broker-a[或者](http://#)broker-b[根据进入的文件选填(http://#)
#0 表示 Master， >0 表示 Slave
brokerId=0
#nameServer地址，分号分割
namesrvAddr=mqnameserver1:9876;mqnameserver1:9876
#在发送消息时，自动创建服务器不存在的topic，默认创建的队列数
defaultTopicQueueNums=4
#是否允许 Broker 自动创建Topic，建议线下开启，线上关闭
autoCreateTopicEnable=true
#是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=true
#Broker 对外服务的监听端口
listenPort=10911
#删除文件时间点，默认凌晨 4点
deleteWhen=04
#文件保留时间，默认 48 小时
fileReservedTime=120
#commitLog每个文件的大小默认1G
mapedFileSizeCommitLog=1073741824
#ConsumeQueue每个文件默认存30W条，根据业务情况调整
mapedFileSizeConsumeQueue=300000
#destroyMapedFileIntervalForcibly=120000
#redeleteHangedFileInterval=120000
#检测物理文件磁盘空间
diskMaxUsedSpaceRatio=88
#存储路径
storePathRootDir=/usr/local/rocketmq/store
#commitLog 存储路径
storePathCommitLog=/usr/local/rocketmq/store/commitlog
#消费队列存储路径存储路径
storePathConsumeQueue=/usr/local/rocketmq/store/consumequeue
#消息索引存储路径
storePathIndex=/usr/local/rocketmq/store/index
#checkpoint 文件存储路径
storeCheckpoint=/usr/local/rocketmq/store/checkpoint
#abort 文件存储路径
abortFile=/usr/local/rocketmq/store/abort
#限制的消息大小
maxMessageSize=65536
#flushCommitLogLeastPages=4
#flushConsumeQueueLeastPages=2
#flushCommitLogThoroughInterval=10000
#flushConsumeQueueThoroughInterval=60000
#Broker 的角色
#- ASYNC_MASTER 异步复制Master
#- SYNC_MASTER 同步双写Master
#- SLAVE
brokerRole=ASYNC_MASTER
#刷盘方式
#- ASYNC_FLUSH 异步刷盘
#- SYNC_FLUSH 同步刷盘
flushDiskType=ASYNC_FLUSH
#checkTransactionMessageEnable=false
#发消息线程池数量
#sendMessageThreadPoolNums=128
#拉消息线程池数量
#pullMessageThreadPoolNums=128
```
7.修改启动脚本参数【两台机器】

```bash
vim /usr/local/rocketmq/bin/runbroker.sh
JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512m -XX:PermSize=128m -XX:MaxPermSize=320m"
vim /usr/local/rocketmq/bin/runserver.sh
JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512m -XX:PermSize=128m -XX:MaxPermSize=320m"
```

注意根据自己的配置进行修改大小  本人为30块钱6个月的百度云服务器内存仅有1g,修改为下:
```bash
vim /usr/local/rocketmq/bin/runbroker.sh
```
```bash
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m -XX:PermSize=128m -XX:MaxPermSize=128m"
```
```bash
vim /usr/local/rocketmq/bin/runserver.sh
```
```bash
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m -XX:PermSize=128m -XX:MaxPermSize=320m"
```

8.启动 NameServer 【两台机器】

```bash
cd /usr/local/rocketmq/bin
nohup sh mqnamesrv &
```

查看启动日志:
```bash
tail -f -n 500 /usr/local/rocketmq/logs/rocketmqlogs/namesrv.log
```
9.启动 BrokerServer  A【192.168.1.103】<br>

```bash
cd /usr/local/rocketmq/bin<br>
nohup sh mqbroker -c /usr/local/rocketmq/conf/2m-noslave/broker-a.properties >/dev/null 2>&1 &
```
查看启动日志:
```bash
tail -f -n 500 /usr/local/rocketmq/logs/rocketmqlogs/broker.log<br>
```
10.启动BrokerServer B【192.168.1.105】<br>
```bash
cd /usr/local/rocketmq/bin
nohup sh mqbroker -c /usr/local/rocketmq/conf/2m-noslave/broker-b.properties >/dev/null 2>&1 &
```

11.查看启动情况<br>
输入jps命令看到以下情况表明成功
```bash
103891 BrokerStartup
103908 Jps
117958 NamesrvStartup
```

12.关闭rocketmq<br>
```bash
cd /usr/local/rocketmq/bin
sh mqshutdown broker
sh mqshutdown namesrv
```
#### rocketMq监控平台rocketmq-console搭建<br>
必备软件(jdk、maven等)不再敖述，而且此步骤十分简单，具体如下：<br>

[rocketmq下载地址:](https://github.com/apache/incubator-rocketmq-externals/tree/master/rocketmq-console)<br>

下载源码后，找到配置文件application.properties，并按照自己需求进行配置。<br>

例如：<br>
```bash
rocketmq.config.namesrvAddr=namesrv服务地址（ip1：port;ip2:port）
```
在文件根目录执行命令：<br>
```bash
mvn clean package -Dmaven.test.skip=true
```
然后在target目录下找到文件rocketmq-console-ng-1.0.0.jar<br>

启动rocketmq-console，执行命令:<br>
```bash
java -jar rocketmq-console-ng-1.0.0.jar
```
访问页面：ip地址:8080<br>


### 单机环境搭建（偷懒版）

上述1，2，3步骤不变<br>
4.添加1个地址既可<br>
5.不变<br>
6.配置broker-a.properties 或 broker-b.properties其中一个既可<br>
\#nameServer地址，中添加添加一个既可<br>
namesrvAddr=mqnameserver1:9876<br>
7,8步骤同上不变<br>
9,10步骤根据第6步骤的配置选一个进行既可<br>
11,12同上<br>
rocketMq监控平台rocketmq-console搭建过程中<br>
在resource文件夹下:application.properties文件中修改<br>
ocketmq.config.namesrvAddr=namesrv服务地址：9876<br>
其余过程不变<br>


#### 如果以上任何步骤有问题可以发邮件franky8805@gmail.com或者new issue提问本人<br>
共同学习      共同进步
