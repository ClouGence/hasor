package net.example.jfinal.daos;
import net.example.jfinal.domain.UserDTO;

import java.sql.SQLException;
import java.util.List;
/**
 *
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserDao {
    //
    /** 查询所有用户 */
    public List<UserDTO> queryList() throws SQLException {
        return new UserDTO().find("select * from TEST_USER_INFO");
    }
    /** 新增用户 */
    public boolean insertUser(UserDTO userDO) throws Exception {
        return userDO.save();
    }
    /** 根据ID查询用户 */
    public UserDTO queryUserInfoByUserID(long userID) throws SQLException {
        return new UserDTO().findFirst("select * from TEST_USER_INFO where id = ?", userID);
    }
    /** 根据帐号查询用户 */
    public UserDTO queryUserInfoByAccount(String account) throws SQLException {
        return new UserDTO().findFirst("select * from TEST_USER_INFO where account = ?", account);
    }
}