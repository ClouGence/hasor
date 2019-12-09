/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.core;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 环境支持
 *
 * 环境变量来源以及加载顺序，靠后顺位会覆盖前一顺位的重复配置。
 * <li>1st，配置文件"hasor.environmentVar"</li>
 * <li>2st，"Hasor.put*"的配置</li>
 * <li>3st，属性文件"env.config"</li>
 * <li>4st，System.getProperties()</li>
 * <li>5st，System.getenv()</li>
 *
 * 属性文件可以存在下面两个位置：1.%WORK_HOME% 下、2.classpath路径。 WORK_HOME优先classpath。
 * @version : 2013-6-19
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Environment {
    /** @return 获取扫描路径*/
    public String[] getSpanPackage();

    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> findClass(Class<?> featureType);

    /** @return 返回启动Hasor 的方式*/
    public Hasor.Level runMode();

    /**
     * 在框架扫描包的范围内查找具有特征类集合（特征可以是继承的类、标记的注解）。<br>
     * -- 该方法会放弃在匹配的过程中如果类无法被ClassLoader所加载的类。
     *
     * @param featureType 特征类型
     * @param loadPackages 扫面范围，单个包
     * @return 返回匹配的类集合。
     */
    public Set<Class<?>> findClass(Class<?> featureType, String loadPackages);

    /**
     * 在框架扫描包的范围内查找具有特征类集合（特征可以是继承的类、标记的注解）。<br>
     * -- 该方法会放弃在匹配的过程中如果类无法被ClassLoader所加载的类。
     *
     * @param featureType 特征类型
     * @param loadPackages 扫面范围，多个包
     * @return 返回匹配的类集合。
     */
    public Set<Class<?>> findClass(Class<?> featureType, String[] loadPackages);

    /** @return 获取上下文*/
    public Object getContext();

    /**获取当创建Bean时使用的{@link ClassLoader}*/
    public ClassLoader getClassLoader();

    /** @return 事件上下文*/
    public EventContext getEventContext();

    /** @return 获取应用程序配置。*/
    public Settings getSettings();
    //
    /*----------------------------------------------------------------------------------------Env*/

    public String[] getVariableNames();

    public String getVariable(String varName);

    /**
     * 计算字符串，将字符串中定义的环境变量替换为环境变量值。环境变量名不区分大小写。<br>
     * <font color="ff0000"><b>注意</b></font>：只有被百分号包裹起来的部分才被解析成为环境变量名，
     * 如果无法解析某个环境变量平台会抛出一条警告，并且将环境变量名连同百分号作为环境变量值一起返回。<br>
     * <font color="00aa00"><b>例如</b></font>：环境中定义了变量Hasor_Home=C:/hasor、Java_Home=c:/app/java，下面的解析结果为
     * <div>%hasor_home%/tempDir/uploadfile.tmp&nbsp;&nbsp;--&gt;&nbsp;&nbsp;C:/hasor/tempDir/uploadfile.tmp</div>
     * <div>%JAVA_HOME%/bin/javac.exe&nbsp;&nbsp;--&gt;&nbsp;&nbsp;c:/app/java/bin/javac.exe</div>
     * <div>%work_home%/data/range.png&nbsp;&nbsp;--&gt;&nbsp;&nbsp;%work_home%/data/range.png；并伴随一条警告</div>
     * @param eval 环境变量表达式。
     * @return 返回环境变量表达式计算结果。
     */
    public String evalString(String eval);

    /**
     * 添加环境变量，添加的环境变量并不会影响到系统环境变量，它会使用内部Map保存环境变量从而避免影响JVM正常运行。
     * @param varName 环境变量名。
     * @param value 环境变量值或环境变量表达式。
     */
    public void addVariable(String varName, String value);

    /**
     * 删除环境变量，该方法从内部Map删除所保存的环境变量，这样做的目的是为了避免影响JVM正常运行。
     * @param varName 环境变量名。
     */
    public void removeVariable(String varName);

    /**刷新加载的环境变量*/
    public void refreshVariables();

    /**获取系统属性。*/
    public String getSystemProperty(String property);
    /*----------------------------------------------------------------------------------------Env*/

    /**
     * Performs the given action for each entry in this map until all entries
     * have been processed or the action throws an exception.   Unless
     * otherwise specified by the implementing class, actions are performed in
     * the order of entry set iteration (if an iteration order is specified.)
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each entry
     * @throws NullPointerException if the specified action is null
     * @since 1.8
     */
    public default void forEach(BiConsumer<String, String> action) {
        Objects.requireNonNull(action);
        for (String varName : getVariableNames()) {
            String varValue = getVariable(varName);
            action.accept(varName, varValue);
        }
    }

    /**
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}) associates it with the given value and returns
     * {@code null}, else returns the current value.
     *
     * @param varName key with which the specified value is to be associated
     * @param varValue value to be associated with the specified key
     * @since 1.8
     */
    public default void putIfAbsent(String varName, String varValue) {
        if (getVariable(varName) == null) {
            addVariable(varName, varValue);
        }
    }

    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     *
     * @implSpec
     * The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     *
     * @param varName the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or {@code defaultValue} if this map contains no mapping for the key
     * @since 1.8
     */
    public default String getOrDefault(String varName, String defaultValue) {
        String v = null;
        return (((v = getVariable(varName)) != null)) ? v : defaultValue;
    }

    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     *
     * @implSpec
     * The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     *
     * @param varName the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or {@code defaultValue} if this map contains no mapping for the key
     * @since 1.8
     */
    public default <V> V getOrMap(String varName, Function<String, V> defaultValue) {
        return defaultValue.apply(getVariable(varName));
    }

    /**
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}), attempts to compute its value using the given mapping
     * function and enters it into this map unless {@code null}.
     *
     * <p>If the function returns {@code null} no mapping is recorded. If
     * the function itself throws an (unchecked) exception, the
     * exception is rethrown, and no mapping is recorded.  The most
     * common usage is to construct a new object serving as an initial
     * mapped value or memoized result.
     *
     * @param varName key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @throws UnsupportedOperationException if the {@code put} operation is not supported by this map
     * @since 1.8
     */
    public default void computeIfAbsent(String varName, Function<String, String> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        if (getVariable(varName) == null) {
            String newValue;
            if ((newValue = mappingFunction.apply(varName)) != null) {
                addVariable(varName, newValue);
            }
        }
    }
}