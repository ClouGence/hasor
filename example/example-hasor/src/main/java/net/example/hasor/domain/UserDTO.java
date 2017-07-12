package net.example.hasor.domain;
import java.util.Date;
/**
 * UserDTO
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserDTO {
    private long   id          = 0;    // UserID（PK，自增）
    private String account     = null; // 帐号（唯一）
    private String email       = null; // email
    private String password    = null; // 密码(非明文)
    private String nick        = null; // 昵称
    private Date   create_time = null; // 创建时间
    private Date   modify_time = null; // 修噶改时间
    //
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getNick() {
        return nick;
    }
    public void setNick(String nick) {
        this.nick = nick;
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