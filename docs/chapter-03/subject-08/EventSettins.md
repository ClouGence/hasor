&emsp;&emsp;默认配置下，Hasor 执行事件的线程池是 20 您可以通过下面两种方式修改这个设定：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <hasor.environmentVar>
        <!-- 执行事件的线程池大小 -->
        <HASOR_LOAD_EVENT_POOL>20</HASOR_LOAD_EVENT_POOL>
    </hasor.environmentVar>
</config>
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://project.hasor.net/hasor/schema/main">
    <!-- 执行事件的线程池大小 -->
    <hasor.eventThreadPoolSize>20</hasor.eventThreadPoolSize>
</config>
```