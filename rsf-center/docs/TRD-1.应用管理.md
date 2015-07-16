#RSF-Center

&emsp;&emsp;RSF注册中心，目的是维护一个RSF服务列表，并为所有连接到注册中心上的RSF客户端提供服务的发布和订阅支持。

&emsp;&emsp;本文是RSF注册中心“应用管理”模块的技术文档。

##一、模块设计

###应用注册
>&emsp;&emsp;提供一个页面让开发者可以创建一个RSF应用分组，并且可以设置应用（Code、应用名称、owner、接口人、应用分组）等信息以备将来查阅，同时还要提供相关的更新、删除操作功能。其中Code是应用的唯一识别码，为了安全起见只允许使用（数字、字母和下划线），应用分组数据是一个树形结构。

>&emsp;&emsp;在应用下面还包含了服务列表的管理，服务列表包含了（新增、删除、修改）三个功能，通过服务管理可以预先定义服务并且可以给服务配置路由策略。服务至少要包含（分组、名称、版本、owner、接口人）信息。

##### 应用添加页
1. 开发者可以在该页面输入如下信息，当输入完信息之后点击添加。保存到`RSF_APP`表。
2. 页面上要提供一个机制用于动态的对`appCode`做去重校验。如果出现重复要求出现错误提示。
3. 如果用户强制递交表单，绕过前端错误提示，后端要求对传入的数据做二次去重校验。
4. 添加完成后跳转到信息详情页，展示详情信息。
5. 下面是要输入的数据。

###### 需要用户输入
    String appCode;     //应用Code
    String appName;     //应用名称
    String accessKey;   //授权KEY（预授权KEY）
    String accessSecret;//授权密钥（预授权密码）
    String contactUsers;//接口人（多人）
    String description; //应用描述
###### 系统自动决定
    long   appID;       //应用ID（自增）
    String onwer;       //owner（当前用户）
    Date   createTime;  //创建时间
    Date   modifyTime;  //修改时间

##### 接口注册页
1. 在应用详情中点击添加接口进入。通过该页面可以为应用预添加接口。该数据保存到表`RSF_ServiceInfo`。
2. 唯一性校验：（`bindGroup`、`bindName`、`bindVersion`唯一）、（`hashCode`唯一）。
3. 添加完成后跳转到信息详情页，展示详情信息。
4. 下面是要输入的数据。

###### 需要用户输入
    String bindGroup;   //服务分组
    String bindName;    //服务名称
    String bindVersion; //服务版本
    String bindType;    //接口名称
    String contactUsers;//接口人（多人）
###### 系统自动决定
    long   serviceID;  //服务编号（自增）
    long   appID;      //所属应用（上下文决定）
    String onwer;      //owner（当前用户）
    String hashCode;   //一致性校验Code
    Date   createTime; //创建时间
    Date   modifyTime; //修改时间

###应用预授权
>&emsp;&emsp;应用授权是安全方面的考虑，当RSF客户端尝试在注册中心上登记时。如果配置的授权码不正确注册中心会拒绝这个客户端的连接，被拒绝的客户端不会得到注册中心任何信息反馈。这个功能可以防止一些恶意程序的连入和扫描，也可以在公司内部用以安全防范。

>&emsp;&emsp;也就是说RSF客户端在向中心注册的时候，至少需要提供应用的（Code码、授权码）两个数据。如果启用了匿名应用则可以什么都不传直接连接到中心，有关匿名应用详见下面专门的介绍。

应用预授权有两个设置入口：

1. 在添加应用的时候可以通过指定`accessKey`、`accessSecret`授权信息来完成设置。
2. 应用详情页可以修改授权信息以完成设置。

#
    String accessKey;   //授权KEY（预授权KEY）
    String accessSecret;//授权密钥（预授权密码）

###应用列表（包含：应用查询、应用详情）
>&emsp;&emsp;这个是应用管理中一个比较重量级的模块，它包含了 list 和 detail 两个页面，其中 list 页中还要提供基于（应用名、应用Code、应用组名）查询支持。点击list页上的信息可以进入 detail 在 detail 页 owner 可以修改应用信息。信息的详细程度可以具体在实现中逐步完善。

##### 应用搜索页
1. 列表页，带分页。
2. 默认搜索页展示
3. 可以根据`appName`、`appCode`、`onwer`、`contactUsers`几个纬度搜索应用。
4. 当搜索条件发生变化时，回到第一页显示数据。
5. 每一条结果表示一个应用，开发者点击某个应用可以进入应用详情页。
6. 排序依据无要求。
7. 每页最多显示25条记录，要求有（首页、尾页，上一页、下一页）的显示。

##### 我的应用
1. 相当于`应用搜索页`。
2. 我的应用中不包含搜索功能。
3. 列出的所有应用都是owner为当前用的的应用。

##### 应用详情页
1. 根据应用ID查询应用信息并显示到页面上。
2. 在详情页可以激活编辑模式进行修改。
3. 可以修改的应用字段如下，其中 owner 必须是由应用 owner 修改。

#
    String appName;     //应用名称
    String accessKey;   //授权KEY
    String accessSecret;//授权密钥（密码，显示时候需要显示为“***”）
    String contactUsers;//接口人
    String description; //应用描述
    String onwer;       //owner（当前用户）

##### 服务详情页
1. 根据应用ID查询应用信息并显示到页面上。
2. 在详情页可以激活编辑模式进行修改。
3. 可以修改的应用字段如下，其中 owner 必须是由应用 owner 修改。

#
    String bindGroup;   //服务分组
    String bindName;    //服务名称
    String bindVersion; //服务版本
    String bindType;    //接口名称
    String contactUsers;//接口人（多人）
    String onwer;       //owner（当前用户）
    String description; //接口描述

###匿名应用
>&emsp;&emsp;匿名应用是一个预留的默认选项，如果强制要求所有连接到注册中心 RSF客户端都要求有应用与其对应的话，开发和部署会比较麻烦。因此匿名应用就产生了。启用匿名应用之后，RSF客户端可以直接连接到RSF注册中心查询自己所需要的服务列表。

1. 配置项，目前先写入配置文件。
2. 目前不支持在线设置，要想修改配置只能修改配置文件并且重起注册中心（`二期考虑支持`）。

保存在rsf-config.xml中的全局配置信息，该配置数据要求封装成Bean成为系统全局唯一对象。包含如下三个数据：

#
    boolean anonymousApp;//是否启用匿名app，默认值：true
    String  accessKey;   //授权KEY
    String  accessSecret;//授权密钥（密码，显示时候需要显示为“***”）

##数据表
###HSQL
####表`RSF_APP`，实体`AppDO`
    appID        bigint;       //应用ID
    appName      nvarchar(200);//应用名称
    accessKey    nvarchar(200);//授权KEY
    accessSecret nvarchar(200);//授权密钥
    onwer        nvarchar(200);//责任人
    description  text;         //应用描述
    createTime   datetime;     //创建时间
    modifyTime   datetime;     //修改时间

####表`RSF_ServiceInfo`，实体`ServiceInfoDO`
    serviceID   bigint;        //服务编号（PK、自增）
    appID       bigint;        //所属应用
    bindGroup   nvarchar(200); //服务分组
    bindName    nvarchar(200); //服务名称
    bindVersion nvarchar(200); //服务版本
    bindType    nvarchar(200); //接口名称
    onwer       nvarchar(200); //接口人
    hashCode    nvarchar(200); //一致性校验Code
    createTime  datetime;      //创建时间
    modifyTime  datetime;      //修改时间

###MySQL
&emsp;&emsp;需要额外配置主键、索引。
####表`RSF_APP`
	PRIMARY KEY (`appID`)
	UNIQUE KEY `UK_RSF_APP_APP_CODE` (`appCode`)
####表`RSF_ServiceInfo`
	PRIMARY KEY (`serviceID`)
	UNIQUE KEY `UK_RSF_APP_APP_CODE` (`bindGroup`,`bindName`,`bindVersion`)
	UNIQUE KEY `UK_RSF_SERVICE_INFO_CODE` (`hashCode`)