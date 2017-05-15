package net.hasor.core.container;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
/**
 * Bean构建接口，负责创建和定义Bean对象。
 * @version : 2014-3-17
 * @author 赵永春(zyc@hasor.net)
 */
public interface ScopManager {
    public static final String SINGLETON_SCOPE = "singleton";

    /**
     * 注册作用域。
     * @param scopeName 作用域名称
     * @param scope 作用域
     * @return 成功注册之后返回它自身, 如果存在同名的scope那么会返回第一次注册那个 scope。
     */
    public Provider<Scope> registerScope(String scopeName, Provider<Scope> scope);

    /**
     * 查找某个作用域。
     * @param scopeName 作用域名称
     */
    public Provider<Scope> findScope(String scopeName);
}