package net.example.hasor.domain;
import java.util.Date;
/**
 * UserDTO
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserDO extends UserDTO {
    private Date createTime = null; // 创建时间
    private Date modifyTime = null; // 修噶改时间
    //
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}