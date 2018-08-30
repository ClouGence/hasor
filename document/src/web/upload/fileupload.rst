文件上传
------------------------------------
文件上传是 web 框架必备的核心技能，Hasor 内置了 apache 的 fileuplaod 组件，并且对该组件做了大量精简优化。因此您在使用 Hasor 的文件上传时不需要引入任何第三方 jar 即可使用。

使用文件上传，您必须通过 WebController 类进行操作，我们先看一下简单的文件上传例子，首先新建一个请求处理器：

.. code-block:: java
    :linenos:

    @MappingTo("/fileupload.do")
    public class FileupLoad extends WebController {
        public void execute() throws IOException {
            FileItem multipart = this.getOneMultipart("upfile");
            multipart.writeTo(...);
            multipart.deleteOrSkip();
        }
    }


而对应的 html 页面就是一个普通的 文件表单上传。

.. code-block:: html
    :linenos:

    <form action="/fileupload.do" method="post" enctype="multipart/form-data">
        <input type="file" name="upfile"/>
        <input type="submit" value="上传"/>
    </form>


大一点的文件，在上传时服务器需要一个临时存储。使用 Hasor 上传文件你可以在解析上传时指定临时存储目录，也可以使用 Hasor 环境变量中指定的默认上传地址。默认情况下 Hasor 使用默认位置进行缓存上传数据。

Hasor 中默认缓存路径是“%WORK_HOME%/temp/fragment”，这是解析 "hasor.fileupload.cacheDirectory" 配置项得到的结果。您可以在 Hasor 内置静态文件 “static-config.xml” 中找到它们。

上传配置下面是两种上传方式，第一种是用了 Hasor 默认自带的缓存目录配置。而第二种方式是用户自定义上传的缓存目录和配置信息。

.. code-block:: java
    :linenos:

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


流式上传
------------------------------------
流式文件上传，流式上传最大的好处就是不需要磁盘缓存。您必须通过 WebController 类进行操作，我们先看一下简单的文件上传例子，首先新建一个请求处理器。然后迭代所有表单项：

.. code-block:: xml
    :linenos:

    @MappingTo("/fileupload.do")
    public class FileupLoad extends WebController {
        public void execute() throws IOException {
            Iterator<FileItemStream> multiStream = this.getMultipartIterator();
            while (multiStream.hasNext()){
                FileItemStream next = multiStream.next();
                if (!next.getName().equals("xxxxx")){
                    continue;
                }
                InputStream inputStream = next.openStream();
                try{
                    stream copy ...
                }finally {
                    inputStream.close();
                }
            }
        }
    }


更多的流式文件上传处理请参考 WebController 中其它的重载方法。您可以根据自己的需要传入一些参数。

缓存配置
------------------------------------
而在接下来将会介绍一下 Hasor 默认的上传缓存配置，具体内容如下：

.. code-block:: xml
    :linenos:

    <?xml version="1.0" encoding="UTF-8"?>
    <config xmlns="http://project.hasor.net/hasor/schema/hasor-web">
        <hasor>
            <environmentVar>
                <!-- fileupload 允许的请求大小 -->
                <HASOR_UPLOAD_MAX_REQUEST_SIZE>-1</HASOR_UPLOAD_MAX_REQUEST_SIZE>
                <!-- fileupload 允许上传的单个文件大小 -->
                <HASOR_UPLOAD_MAX_FILE_SIZE>-1</HASOR_UPLOAD_MAX_FILE_SIZE>
            </environmentVar>
        </hasor>
    </config>


默认缓存路径的位置为：“${HASOR_TEMP_PATH}/fragment”。其中 “HASOR_TEMP_PATH” 的环境变量值默认为 “%WORK_HOME%/temp”，“WORK_HOME” 默认情况下表示的是 “%USER.HOME%/hasor-work”，USER.HOME是用户登录系统之后的用户主目录。

- 如果是 linux 系统那么这个目录通常在这里：“/home/xxx/hasor-work/temp/fragment”
- 如果是 window 用户用户住目录会在：“c:/users/xxx/hasor-work/temp/fragment”

你可以通过指定，WORK_HOME 而切换位置。
