package net.demo.hasor.daos;
import net.demo.hasor.domain.UserInfo;
import org.more.util.StringUtils;
/**
 *
 */
public class UserDao {
    public UserInfo queryUserInfoByAccount(String account) {
        if (StringUtils.equalsIgnoreCase(account, "zyc")) {
            return new UserInfo();
        }
        return null;
    }
}
