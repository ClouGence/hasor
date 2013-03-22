package org.platform.api.upfile;
import java.util.List;
/**
 * 上传文件的策略检查。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IUpFilePolicy {
    /**
     * 进行策略检查。
     * @param userInfo 用户信息
     * @param upData 上传的数据
     * @param list 传上来的文件列表
     */
    public boolean test(IUpInfo upData, List<IFileItem> list);
}