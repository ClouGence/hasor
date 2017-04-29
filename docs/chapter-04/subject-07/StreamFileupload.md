&emsp;&emsp;流式文件上传，流式上传最大的好处就是不需要磁盘缓存。您必须通过 WebController 类进行操作，我们先看一下简单的文件上传例子，首先新建一个请求处理器。然后迭代所有表单项：
```java
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
```

&emsp;&emsp;更多的流式文件上传处理请参考 WebController 中其它的重载方法。您可以根据自己的需要传入一些参数。