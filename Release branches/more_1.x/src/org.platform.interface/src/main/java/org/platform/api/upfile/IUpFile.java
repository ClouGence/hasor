package org.platform.api.upfile;
import java.io.IOException;
import java.util.List;
/**
 * 处理文件上传的接口
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IUpFile {
    /***
     * 处理文件上传。
     * @param upData 上传过程携带的所有信息。
     * @param list 发现的文件或者字段条目。
     */
    public void doUpFile(IUpInfo upData, List<IFileItem> list) throws IOException;
}