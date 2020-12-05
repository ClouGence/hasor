package net.hasor.dataql.fx.db.likemybatis;

import java.util.List;
import java.util.Map;

import net.hasor.utils.StringUtils;

/**
 * 对应XML中 <trim>，注意prefixOverrides和suffixOverrides大小写敏感
 * @author zhangxu
 * @version : 2020-12-05
 */
public class TrimSqlNode extends SqlNode {
    /** 前缀  prefix*/
    protected String prefix;

    /** 后缀  suffix*/
    protected String suffix;

    /** 前缀 prefixOverrides */
    protected String prefixOverrides;

    /** 后缀 suffixOverrides */
    protected String suffixOverrides;

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setSuffixOverrides(String suffixOverrides) {
        this.suffixOverrides = suffixOverrides;
    }

    public void setPrefixOverrides(String prefixOverrides) {
        this.prefixOverrides = prefixOverrides;
    }

    @Override
    public String getSql(Map<String, Object> paramMap, List<Object> parameters) {
        StringBuilder sqlBuffer = new StringBuilder();

        String childrenSql = executeChildren(paramMap, parameters);

        // 如果子节点不为null，则转成数组
        if (StringUtils.isNotEmpty(childrenSql)) {
            // 开始拼接SQL,
            sqlBuffer.append(StringUtils.defaultString(this.prefix)).append(" ");

            //去掉prefixOverrides
            if (StringUtils.isNotEmpty(this.prefixOverrides)) {
                String[] overrideArray = this.prefixOverrides.split("\\|");
                for (String override : overrideArray) {
                    if (childrenSql.trim().startsWith(override)) {
                        childrenSql = childrenSql.substring(childrenSql.indexOf(override) + override.length());
                        break;
                    }
                }

            }

            //去掉suffixOverrides
            if (StringUtils.isNotEmpty(this.suffixOverrides)) {
                String[] overrideArray = this.suffixOverrides.split("\\|");
                for (String override : overrideArray) {
                    if (childrenSql.trim().endsWith(override)) {
                        childrenSql = childrenSql.substring(0, childrenSql.lastIndexOf(override));
                        break;
                    }
                }
            }

            sqlBuffer.append(childrenSql);
            // 拼接结束SQL
            sqlBuffer.append(" ").append(StringUtils.defaultString(this.suffix));
        }

        return sqlBuffer.toString();
    }

}
