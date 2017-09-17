package net.example.hasor.daos;
import net.example.hasor.domain.UserDTO;
import net.hasor.core.Inject;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.paramer.BeanSqlParameterSource;

import java.sql.SQLException;
import java.util.List;
/**
 *
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserDao {
    @Inject
    private JdbcTemplate jdbcTemplate;
    //
    //
    /** 查询所有用户 */
    public List<UserDTO> queryList() throws SQLException {
        return this.jdbcTemplate.queryForList("select * from TEST_USER_INFO", UserDTO.class);
    }
    /** 新增用户 */
    public boolean insertUser(UserDTO userDO) throws Exception {
        //
        int execute = this.jdbcTemplate.update("insert into TEST_USER_INFO ("//
                + "    `account`,`email`,`password`,`nick`,`create_time`,`modify_time`"//
                + ") values ("//
                + "    :account,:email,:password,:nick,:create_time,:modify_time" + //
                ");", new BeanSqlParameterSource(userDO));
        //
        return execute == 1;
    }
    /** 根据ID查询用户 */
    public UserDTO queryUserInfoByUserID(long userID) throws SQLException {
        return this.jdbcTemplate.queryForObject(//
                "select * from TEST_USER_INFO where id = ?", //
                UserDTO.class, userID);
    }
    /** 根据帐号查询用户 */
    public UserDTO queryUserInfoByAccount(String account) throws SQLException {
        return this.jdbcTemplate.queryForObject(//
                "select * from TEST_USER_INFO where account = ?", //
                UserDTO.class, account);
    }
}