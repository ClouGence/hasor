package net.hasor.db.dal.dynamic;
/**
 * 对应XML中 <where>
 * @author zhangxu
 * @author 赵永春 (zyc@byshell.org)
 * @version : 2021-05-24
 */
public class WhereSqlNode extends TermSqlNode {
    public WhereSqlNode() {
        super("where", "", "and | or", "", true);
    }
}