#rocketmq_learning<br>
##RocketMQ消息中间件学习总结<br>
本次使用的RocketMQ消息中间件的版本是4.1.0-incubating版本其他版本与该版本类似<br>
首先是环境的部署与搭建(双机Master服务器环境)<br>
环境：(以下是本人安装环境与步骤,可参考)<br>

Linux  centos 7<br>
RocketMQ 4.1.0<br>
java 1.8<br>
maven 3.3.9 <br>

###单机环境搭建请看最后面<br>

1.[下载rocketmq](http://rocketmq.apache.org/release_notes/release-notes-4.1.0-incubating/)
推荐下载Binary（不需进行打包）<br>

####注意：因为是双主机模式,所以下面的步骤需要在两台Liunx服务器都完成<br>
2.将下载文件传输到liunx服务器(这里本人使用的是Linux服务器root账户)<br>

MAC版:scp /Users/***/Downloads/rocketmq-all-4.1.0-incubating-bin-release.zip  root@Linux服务器IP地址:/usr/local/<br>

Win版:pscp windows本地路径 root@Linux服务器IP地址:/usr/local<br>

注意：windows 需要下载pscp.exe文件(具体下载地址可google或百度)，并将其存放入C:\Windows\System32文件夹下，然后可在windows命令框中直接输入上面命令。<br>


3.解压文件(root账户登录服务器linux)<br>

进入安装包目录:cd /usr/local/<br>

解压安装包:unzip rocketmq-all-4.1.0-incubating-bin-release.zip<br>

重命名解压文件(方便操作,根据个人喜好,可以改名也可不改):mv rocketmq-all-4.1.0-incubating-bin-release rocketmq<br>


4.修改两台主机hosts信息<br>
vim /etc/hosts<br>
添加如下信息：(ip地址改为自己的服务器地址)<br>

192.168.1.103 mqnameserver1<br>

192.168.1.105 mqnameserver2<br>

5.创建存储路径【两台机器】<br>
  cd /usr/local<br>
  mkdir /usr/local/rocketmq/store<br>
  
  mkdir /usr/local/rocketmq/store/commitlog<br>
  
  mkdir /usr/local/rocketmq/store/consumequeue<br>
  
  mkdir /usr/local/rocketmq/store/index<br>
 
6. RocketMQ配置文件【两台机器】<br>

  vim /usr/local/rocketmq/conf/2m-noslave/broker-a.properties<br>

  vim /usr/local/rocketmq/conf/2m-noslave/broker-b.properties<br>

\#所属集群名字<br>
brokerClusterName=rocketmq-cluster<br>
\#broker名字，注意此处不同的配置文件填写的不一样<br>
brokerName=broker-a[或者](http://#)broker-b[根据进入的文件选填](http://#)<br>
\#0 表示 Master， >0 表示 Slave<br>
brokerId=0<br>
\#nameServer地址，分号分割<br>
namesrvAddr=mqnameserver1:9876;mqnameserver1:9876<br>
\#在发送消息时，自动创建服务器不存在的topic，默认创建的队列数<br>
defaultTopicQueueNums=4<br>
\#是否允许 Broker 自动创建Topic，建议线下开启，线上关闭<br>
autoCreateTopicEnable=true<br>
\#是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭<br>
autoCreateSubscriptionGroup=true<br>
\#Broker 对外服务的监听端口<br>
listenPort=10911<br>
\#删除文件时间点，默认凌晨 4点<br>
deleteWhen=04<br>
\#文件保留时间，默认 48 小时<br>
fileReservedTime=120<br>
\#commitLog每个文件的大小默认1G<br>
mapedFileSizeCommitLog=1073741824<br>
\#ConsumeQueue每个文件默认存30W条，根据业务情况调整<br>
mapedFileSizeConsumeQueue=300000<br>
\#destroyMapedFileIntervalForcibly=120000<br>
\#redeleteHangedFileInterval=120000<br>
\#检测物理文件磁盘空间<br>
diskMaxUsedSpaceRatio=88<br>
\#存储路径<br>
storePathRootDir=/usr/local/rocketmq/store<br>
\#commitLog 存储路径<br>
storePathCommitLog=/usr/local/rocketmq/store/commitlog<br>
\#消费队列存储路径存储路径<br>
storePathConsumeQueue=/usr/local/rocketmq/store/consumequeue<br>
\#消息索引存储路径<br>
storePathIndex=/usr/local/rocketmq/store/index<br>
\#checkpoint 文件存储路径<br>
storeCheckpoint=/usr/local/rocketmq/store/checkpoint<br>
\#abort 文件存储路径<br>
abortFile=/usr/local/rocketmq/store/abort<br>
\#限制的消息大小<br>
maxMessageSize=65536<br>
\#flushCommitLogLeastPages=4<br>
\#flushConsumeQueueLeastPages=2<br>
\#flushCommitLogThoroughInterval=10000<br>
\#flushConsumeQueueThoroughInterval=60000<br>
\#Broker 的角色<br>
\#- ASYNC_MASTER 异步复制Master<br>
\#- SYNC_MASTER 同步双写Master<br>
\#- SLAVE<br>
brokerRole=ASYNC_MASTER<br>
\#刷盘方式<br>
\#- ASYNC_FLUSH 异步刷盘<br>
\#- SYNC_FLUSH 同步刷盘<br>
flushDiskType=ASYNC_FLUSH<br>
\#checkTransactionMessageEnable=false<br>
\#发消息线程池数量<br>
\#sendMessageThreadPoolNums=128<br>
\#拉消息线程池数量<br>
\#pullMessageThreadPoolNums=128<br>

7.修改启动脚本参数【两台机器】<br>
vim /usr/local/rocketmq/bin/runbroker.sh<br>
JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512m -XX:PermSize=128m -XX:MaxPermSize=320m"<br>
vim /usr/local/rocketmq/bin/runserver.sh<br>
JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512m -XX:PermSize=128m -XX:MaxPermSize=320m"<br>

注意根据自己的配置进行修改大小  本人为30块钱6个月的百度云服务器内存仅有1g,修改为下<br>
vim /usr/local/rocketmq/bin/runbroker.sh<br>
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m -XX:PermSize=128m -XX:MaxPermSize=128m"<br>
vim /usr/local/rocketmq/bin/runserver.sh<br>
JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512m -XX:PermSize=128m -XX:MaxPermSize=320m"<br>

8.启动 NameServer 【两台机器】<br>

cd /usr/local/rocketmq/bin<br>
nohup sh mqnamesrv &<br>

查看启动日志:<br>
tail -f -n 500 /usr/local/rocketmq/logs/rocketmqlogs/namesrv.log

9.启动 BrokerServer  A【192.168.1.103】<br>

cd /usr/local/rocketmq/bin<br>
nohup sh mqbroker -c /usr/local/rocketmq/conf/2m-noslave/broker-a.properties >/dev/null 2>&1 &<br>

查看启动日志:tail -f -n 500 /usr/local/rocketmq/logs/rocketmqlogs/broker.log<br>

10.启动BrokerServer B【192.168.1.105】<br>

cd /usr/local/rocketmq/bin<br>
nohup sh mqbroker -c /usr/local/rocketmq/conf/2m-noslave/broker-b.properties >/dev/null 2>&1 &<br>

11.查看启动情况<br>
输入jps命令看到以下情况表明成功<br>
103891 BrokerStartup<br>
103908 Jps<br>
117958 NamesrvStartup<br>

12.关闭rocketmq<br>
cd /usr/local/rocketmq/bin<br>
sh mqshutdown broker<br>
sh mqshutdown namesrv<br>

####rocketMq监控平台rocketmq-console搭建<br>
必备软件(jdk、maven等)不再敖述，而且此步骤十分简单，具体如下：<br>

[rocketmq下载地址:](https://github.com/apache/incubator-rocketmq-externals/tree/master/rocketmq-console)<br>

下载源码后，找到配置文件application.properties，并按照自己需求进行配置。<br>

例如：<br>

rocketmq.config.namesrvAddr=namesrv服务地址（ip1：port;ip2:port）<br>

在文件根目录执行命令：<br>

mvn clean package -Dmaven.test.skip=true<br>

然后在target目录下找到文件rocketmq-console-ng-1.0.0.jar<br>

启动rocketmq-console，执行命令:<br>

java -jar rocketmq-console-ng-1.0.0.jar<br>

访问页面：ip地址:8080<br>


###单机环境搭建（偷懒版）

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


####如果以上任何步骤有问题可以发邮件franky8805@gmail.com或者new issue提问本人
#共同学习 共同进步     [peace&love](http://#)

  

 







