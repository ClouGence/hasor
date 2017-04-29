&emsp;&emsp;文件上传是 web 框架必备的核心技能，Hasor 内置了 apache 的 fileuplaod 组件，并且对该组件做了大量精简优化。因此您在使用 Hasor 的文件上传时不需要引入任何第三方 jar 即可使用。

使用文件上传，您必须通过 WebController 类进行操作，我们先看一下简单的文件上传例子，首先新建一个请求处理器：
```java
@MappingTo("/fileupload.do")
public class FileupLoad extends WebController {
    public void execute() throws IOException {
        FileItem multipart = this.getOneMultipart("upfile");
        multipart.writeTo(...);
        multipart.deleteOrSkip();
    }
}
```

&emsp;&emsp;而对应的 html 页面就是一个普通的 文件表单上传。
```html
<form action="/fileupload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upfile"/>
    <input type="submit" value="上传"/>
</form>
```

&emsp;&emsp;大一点的文件，在上传时服务器需要一个临时存储。使用 Hasor 上传文件你可以在解析上传时指定临时存储目录，也可以使用 Hasor 环境变量中指定的默认上传地址。默认情况下 Hasor 使用默认位置进行缓存上传数据。

&emsp;&emsp;Hasor 中默认缓存路径是“%WORK_HOME%/temp/fragment”，这是解析 "hasor.fileupload.cacheDirectory" 配置项得到的结果。您可以在 Hasor 内置静态文件 “static-config.xml” 中找到它们。

&emsp;&emsp;上传配置下面是两种上传方式，第一种是用了 Hasor 默认自带的缓存目录配置。而第二种方式是用户自定义上传的缓存目录和配置信息。
```java
@MappingTo("/fileupload.do")
public class FileupLoad extends WebController {
    public void execute() throws IOException {
        //
        // 方式1: - 使用默认缓存目录
        FileItem multipart = this.getOneMultipart("upfile");
        multipart.writeTo(new File(""));
        multipart.deleteOrSkip();
        //
        // 方式2: - 使用自定义缓存目录
        String cacheDirectory = "...";
        Integer maxPostSize = 1024 * 1024;
        FileItem multipart1 = this.getOneMultipart("upfile", cacheDirectory, maxPostSize);
    }
}
```

&emsp;&emsp;而在接下来将会介绍一下 Hasor 默认的上传缓存配置。临时上传缓存的配置信息保存在：“hasor.fileupload.cacheDirectory”配置项下。这个配置位于 hasor jar 包中的“static-config.xml”配置文件中。具体内容如下：
```xml
...
<hasor>
    ...
    <!-- 文件上传 -->
    <fileupload>
        <!-- 上传文件缓存目录 -->
        <cacheDirectory>${HASOR_TEMP_PATH}/fragment</cacheDirectory>
        <!-- 允许的请求大小 ( -1 表示不限制)-->
        <maxRequestSize>-1</maxRequestSize>
        <!-- 允许上传的单个文件大小( -1 表示不限制) -->
        <maxFileSize>-1</maxFileSize>
    </fileupload>
    ...
</hasor>
...
```

&emsp;&emsp;在这段配置文件中 “HASOR_TEMP_PATH” 表示的是一个环境变量，在 static-config.xml 配置文件的 “environmentVar” 节点下可以找到这个环境变量的内容。
```xml
<environmentVar>
    ...
    <!-- 工作目录 -->
    <WORK_HOME>%USER.HOME%/hasor-work</WORK_HOME>
    <!-- 临时文件位置 -->
    <HASOR_TEMP_PATH>%WORK_HOME%/temp</HASOR_TEMP_PATH>
    ...
</environmentVar>
```

&emsp;&emsp;默认情况下 “HASOR_TEMP_PATH” 的配置信息是 “%WORK_HOME%/temp”，我们看到这又是一组环境变量。“WORK_HOME” 默认情况下表示的是 “USER.HOME”。而这是用户登录系统之后的用户主目录。

&emsp;&emsp;如果您是 linux 系统那么这个目录通常在这里：“/home/xxx”，如果是 window 用户用户住目录会在“c:/users”下面。
