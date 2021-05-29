package net.hasor.db.dal.dynamic;
/**
 * 对应XML中 <set>
 * @author zhangxu
 * @author 赵永春 (zyc@byshell.org)
 * @version : 2021-05-24
 */
public class SetSqlNode extends TermSqlNode {
    public SetSqlNode() {
        super("set", "", "", ",", true);
    }
}