ZK 目录数据
	/rsf-center --------------------------- RSF-Center
		/servers -------------------------- 注册中心集群列表
			/192.168.1.11:2180 ------------ 服务器IP:消息通信端口
				/info --------------------- 详细信息
				/auth --------------------- 认证秘钥，用来防止集群之外的机器利用RSF传输协议伪造消息
				/version ------------------ rsf-center版本
				/beat --------------------- 服务器状态（动态，如果不存在则标示服务器离线）
		/leader --------------------------- leader是谁
		/services ------------------------- 服务信息
			/group ------------------------ 服务所属分组
				/name --------------------- 服务名称
					/version -------------- 服务版本
						/info ------------- 服务信息
						/provider --------- 提供者列表
							/192.168.1.11:2180
							/192.168.1.11:2180
							/192.168.1.11:2180
						/consumer --------- 消费者列表
							/192.168.1.11:2180
							/192.168.1.11:2180
							/192.168.1.11:2180
						/flowcontrol ------ 流控规则
						/rule ------------- 路由规则
							/args-level --- 参数级路由规则
							/method-level - 方法级路由规则
							/service-level  服务级路由规则
		/config --------------------------- 默认规则设置
			/flowcontrol ------------------ 默认流控规则
			/rule ------------------------- 默认路由规则
				/args-level --------------- 参数级路由规则
				/method-level ------------- 方法级路由规则
				/service-level ------------ 服务级路由规则
								
------------------------------------------------------------------------------
- 节点上的数据结构

/rsf-center/servers/192.168.1.11:2180/info
	<info>
		<workMode>master</workMode>								<!-- 工作模式：Master、Slave、Alone -->
		<inetAddress>192.168.1.11</inetAddress>					<!-- 注册中心地址，RSF客户端连接到这个地址 -->
		<rsf.bindPort>2180</rsf.bindPort>						<!-- 注册中心端口，RSF客户端连接到这个端口 -->
		<zooKeeper.sid>0</zooKeeper.sid>						<!-- 集群中sid -->
		<zooKeeper.bindPort>2181</zooKeeper.bindPort>			<!-- 集群中用于zk信息传输的端口 -->
		<zooKeeper.electionPort>2182</zooKeeper.electionPort>	<!-- 集群中用于zk选举的端口 -->
	</info>

/rsf-center/servers/192.168.1.11:2180/auth
	xsfasjnfasldjhasdfiffaj

/rsf-center/servers/192.168.1.11:2180/version
	1.0.0

/rsf-center/servers/192.168.1.11:2180/beat
	20160202-222222

/rsf-center/leader
	192.168.1.11:2180

/rsf-center/services/group/name/version/info
	<info>
		<hashCode>xxxxxxxx</hashCode>										<!-- 服务签名 -->
		<bindID>[RSF]-org.hasor.test.HelloWord-1.0</bindID>					<!-- BindID -->
		<group>RSF</group>													<!-- Group -->
		<name>org.hasor.test.HelloWord</name>								<!-- Name -->
		<version>1.0</version>												<!-- Version -->
		<bindType>org.hasor.test.HelloWord</bindTypen>						<!-- Type -->
		<serviceList>														<!-- 接口信息 -->
			<method>org.hasor.test.HelloWord.hello(String,Long)</method>
			<method>org.hasor.test.HelloWord.hello(String,Long)</method>
			<method>org.hasor.test.HelloWord.hello(String,Long)</method>
		</serviceList>
	</info>

/rsf-center/services/group/name/version/provider
	1234567890000
	内容格式为：心跳时间戳Long类型，标示最后一次记录到provider变更的时间点

/rsf-center/services/group/name/version/provider/192.168.1.11:2180
	<info>
		<serializeType>java</serializeType>					<!-- 传输序列化协议 -->
		<client.timeout>6000</client.timeout>				<!-- rsf调用超时时间-->
		<queue.maxSize>4096</queue.maxSize>					<!-- 最大服务处理队列长度 -->
	</info>

/rsf-center/services/group/name/version/consumer/192.168.1.11:2180
	<info>
		<serializeType>java</serializeType>					<!-- 传输序列化协议 -->
		<client.timeout>6000</client.timeout>				<!-- rsf调用超时时间-->
		<client.maximumRequest>200</client.maximumRequest>	<!-- 最大并发请求数 -->
	</info>

/rsf-center/services/group/name/version/flowcontrol
	<controlSet>
		<!-- 单元化规则 -->
		<flowControl enable="true" type="unit">
			<threshold>0.3</threshold>                  <!-- 本地机房占比低于这个数时启用跨机房 -->
			<exclusions>172.23.*,172.19.*</exclusions>  <!-- 当本机IP属于下面这个网段时则不生效 -->
		</flowControl>
		<!-- 服务地址选取规则 -->
		<flowControl enable="true" type="random">
		</flowControl>
		<!-- QoS流量控制规则 -->
		<flowControl enable="true" type="Speed">
			<action>service</action>    				<!-- 速率控制方式：每服务、每方法、每地址 -->
			<rate>5</rate>             					<!-- 稳态速率 -->
			<peak>100</peak>            				<!-- 峰值速率 -->
			<timeWindow>10</timeWindow> 				<!-- 时间窗口 -->
		</flowControl>
	</controlSet>

/rsf-center/services/group/name/version/rule/args-level
	def Map<String,Map<String,List<String>>> evalAddress(String serviceID,List<String> allAddress)  {
	    return null
	}

/rsf-center/services/group/name/version/rule/method-level
	def Map<String,List<String>> evalAddress(String serviceID,List<String> allAddress)  {
	    return null
	}

/rsf-center/services/group/name/version/rule/service-level
	def List<String> evalAddress(String serviceID,List<String> allAddress)  {
	    return null
	}
