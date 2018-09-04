package net.example.hasor;
import net.hasor.core.Inject;
import net.hasor.db.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
/**
 *
 * @version : 2016年11月07日
 * @author 赵永春 (zyc@hasor.net)
 */
public class OptionDAO {
    @Inject
    private JdbcTemplate jdbcTemplate;
    //
    //
    public List<OptionDO> queryList() throws SQLException {
        String querySQL = "select * from MyOption";
        return jdbcTemplate.queryForList(querySQL, OptionDO.class);
    }
    //
    public OptionDO queryOption(String optKey) throws SQLException {
        String querySQL = "select * from MyOption where key = ?";
        return jdbcTemplate.queryForObject(querySQL, OptionDO.class, optKey);
    }
    //
    public boolean insertOption(String key, String value) throws SQLException {
        int i = jdbcTemplate.executeUpdate("insert into MyOption (id,key,value,desc,create_time,modify_time) values (?,?,?,?,?,?)",//
                UUID.randomUUID().toString(),   // id
                key,                                   // key
                value,                                 // value
                key + "-desc",                         // desc
                new Date(), new Date());
        return i != 0;
    }
    //
    public boolean updateOption(String key, String value) throws SQLException {
        int i = jdbcTemplate.executeUpdate("update MyOption set value =? ,modify_time = ? where key =?",//
                value, new Date(), key);
        return i != 0;
    }
    //
    public boolean deleteOption(String key) throws SQLException {
        int i = jdbcTemplate.executeUpdate("delete from MyOption where key =?", key);
        return i != 0;
    }
}