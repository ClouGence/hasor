package net.example.hasor;
import java.util.Date;
/**
 * UserDTO
 * @version : 2016年11月07日
 * @author 赵永春 (zyc@hasor.net)
 */
public class OptionDO {
    private String id          = null;
    private String key         = null;
    private String value       = null;
    private String desc        = null;
    private Date   create_time = null;
    private Date   modify_time = null;
    //
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Date getCreate_time() {
        return create_time;
    }
    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
    public Date getModify_time() {
        return modify_time;
    }
    public void setModify_time(Date modify_time) {
        this.modify_time = modify_time;
    }
}