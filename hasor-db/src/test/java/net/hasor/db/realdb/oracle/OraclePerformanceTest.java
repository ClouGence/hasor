package net.hasor.db.performance;
import net.hasor.db.lambda.LambdaOperations.LambdaInsert;
import net.hasor.db.lambda.LambdaTemplate;
import net.hasor.test.db.dto.TB_User;
import net.hasor.test.db.dto.TbUser;
import net.hasor.test.db.utils.DsUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class OraclePerformanceTest {
    @Test
    public void oracleInsert_1() throws SQLException {
        long t = System.currentTimeMillis();
        try (Connection con = DsUtils.localOracle()) {
            LambdaTemplate lambdaTemplate = new LambdaTemplate(con);
            try {
                lambdaTemplate.getJdbcTemplate().execute("drop table tb_user");
            } catch (Exception e) {
                //
            }
            try {
                lambdaTemplate.getJdbcTemplate().loadSQL("/net_hasor_db/tb_user_for_oracle.sql");
            } catch (Exception e) {
                //
            }
            //
            LambdaInsert<TbUser> lambdaInsert = lambdaTemplate.lambdaInsert(TbUser.class);
            int count = 5000000;
            for (int i = 0; i < count; i++) {
                TbUser tbUser = new TbUser();
                tbUser.setUid("id_" + i);
                tbUser.setName(String.format("默认用户_%s", i));
                tbUser.setAccount(String.format("acc_%s", i));
                tbUser.setPassword(String.format("pwd_%s", i));
                tbUser.setIndex(i);
                tbUser.setMail(String.format("autoUser_%s@hasor.net", i));
                tbUser.setCreateTime(new Date());
                lambdaInsert.applyEntity(tbUser);
                //
                if (i % 500 == 0) {
                    lambdaInsert.executeSumResult();
                    System.out.println("write to db. " + i);
                }
            }
            lambdaInsert.executeSumResult();
            //
            int tbUsersCount = lambdaTemplate.lambdaQuery(TB_User.class).queryForCount();
            assert tbUsersCount == count;
            System.out.println("cost: " + (System.currentTimeMillis() - t));
        }
    }
}
