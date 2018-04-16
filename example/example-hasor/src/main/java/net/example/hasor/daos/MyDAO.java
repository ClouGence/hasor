package net.example.hasor.daos;
import net.example.hasor.domain.UserDTO;
import net.hasor.core.Inject;
import net.hasor.db.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
/**
 *
 * @version : 2016年11月07日
 * @author 赵永春 (zyc@hasor.net)
 */
public class MyDAO {
    // 依赖注入 JdbcTemplate 到 MyDAO 中
    @Inject
    private JdbcTemplate jdbcTemplate;
    //
    // 根据用户名获取用户
    public UserDTO getUserByAccount(String account) throws SQLException {
        String querySQL = "select * from UserInfo where account = ?";
        return jdbcTemplate.queryForObject(querySQL, UserDTO.class, account);
    }
}