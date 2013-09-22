/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package javax.annotation.sql;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to define a container <code>DataSource</code> and
 * be registered with JNDI. The <code>DataSource</code> may be configured by
 * setting the annotation elements for commonly used <code>DataSource</code>
 * properties.  Additional standard and vendor-specific properties may be
 * specified using the <code>properties</code> element.
 * <p>
 *
 * The data source will be registered under the name specified in the
 * <code>name</code> element. It may be defined to be in any valid
 * <code>Java EE</code> namespace, and will determine the accessibility of
 * the data source from other components.
 * <p>
 * A JDBC driver implementation class of the appropriate type, either
 * <code>DataSource</code>, <code>ConnectionPoolDataSource</code>, or
 * <code>XADataSource</code>, must be indicated by the <code>className</code>
 * element. The availability of the driver class will be assumed at runtime.
 *<p>
 * The <code>url</code> property should not be specified in conjunction with
 * other standard properties for defining the connectivity to the database.
 * If the <code>url</code> property is specified along with other standard
 * <code>DataSource</code> properties
 * such as <code>serverName</code> and <code>portNumber</code>, the more
 * specific properties will take precedence and <code>url</code> will be
 * ignored.
 * <p>
 * Vendors are not required to support properties that do not normally
 * apply to a specific data source type. For example, specifying the
 * <code>transactional</code> property to be <code>true</code> but supplying
 * a value for <code>className</code> that implements a data source class
 * other than <code>XADataSource</code> may not be supported.
 * <p>
 * Vendor-specific properties may be combined with or used to
 *  override standard data source properties defined using this annotation.
 * <p>
 * <code>DataSource</code> properties that are specified and are not supported
 * in a given configuration or cannot be mapped to a vendor specific
 * configuration property may be ignored.
 * <p>
 * Examples:
 * <br>
 *  <pre>
 *   &#064;DataSourceDefinition(name="java:global/MyApp/MyDataSource",
 *      className="com.foobar.MyDataSource",
 *      portNumber=6689,
 *      serverName="myserver.com",
 *      user="lance",
 *      password="secret"
 *   )
 * 
 * </pre>
 * <p>
 * Using a <code>URL</code>:
 * <br>
 * <pre>
 *  &#064;DataSourceDefinition(name="java:global/MyApp/MyDataSource",
 *    className="org.apache.derby.jdbc.ClientDataSource",
 *    url="jdbc:derby://localhost:1527/myDB",
 *    user="lance",
 *    password="secret"
 * )
 * </pre>
 * <p>
 * An example lookup of the {@link DataSource} from an EJB:
 * <pre>
 * &#064;Stateless
 * public class MyStatelessEJB {
 *   &#064;Resource(lookup="java:global/MyApp/myDataSource")
 *    DataSource myDB;
 *      ...
 * }
 * </pre>
 * <p>
 * @see javax.sql.DataSource
 * @see javax.sql.XADataSource
 * @see javax.sql.ConnectionPoolDataSource
 * @since Common Annotations 1.1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceDefinition {

    /**
     * JNDI name by which the data source will be registered.
     * @since 1.1
     */
     String name();

    /**
     * DataSource implementation class name which implements:
     *  <code>javax.sql.DataSource</code> or <code>javax.sql.XADataSource</code>
     * or <code>javax.sql.ConnectionPoolDataSource</code>.
     * @since 1.1
     */
    String className();

    /**
     * Description of this data source
     * @since 1.1
     */
    String description() default "";

    /**
     * A JDBC URL.  If the <code>url</code> property is specified along with
     * other standard <code>DataSource</code> properties
     * such as <code>serverName</code> and <code>portNumber</code>, the more
     * specific properties will take precedence and <code>url</code> will be
     * ignored.
     * @since 1.1
     */
    String url() default "";

    /**
     * User name to use for connection authentication.
     * @since 1.1
     */
    String user() default "";

    /**
     * Password to use for connection authentication.
     * @since 1.1
     */
    String password() default "";

    /**
     * Name of a database on a server.
     * @since 1.1
     */
    String databaseName() default "";

    /**
     * Port number where a server is listening for requests.
     * @since 1.1
     */
    int portNumber() default -1;

    /**
     * Database server name.
     * @since 1.1
     */
    String serverName() default "localhost";

    /**
     * Isolation level for connections. The Isolation level 
     * must be one of the following:
     * <p>
     * <ul>
     * <li>Connection.TRANSACTION_NONE,
     * <li>Connection.TRANSACTION_READ_ UNCOMMITTED,
     * <li>Connection.TRANSACTION_READ_COMMITTED,
     * <li>Connection.TRANSACTION_REPEATABLE_READ,
     * <li>Connection.TRANSACTION_SERIALIZABLE
     *</ul>
     * <p>
     * Default is vendor-specific.
     * @since 1.1
     */
    int isolationLevel() default -1;

    /**
     * Set to <code>false</code> if connections should not participate
     * in transactions.
     * <p>
     * Default is to enlist in a transaction when one is active or becomes
     * active.
     * @since 1.1
     */
    boolean transactional() default true;

    /**
     * Number of connections that should be created when a connection pool
     * is initialized.
     * <p>
     * Default is vendor-specific
     * @since 1.1
     */
    int initialPoolSize() default -1;

    /**
     * Maximum number of connections that should be concurrently allocated for a
     * connection pool.
     * <p>
     * Default is vendor-specific.
     * @since 1.1
     */
    int maxPoolSize() default -1;

    /**
     * Minimum number of connections that should be allocated for a
     * connection pool.
     * <p>
     * Default is vendor-specific.
     * @since 1.1
     */
    int minPoolSize() default -1;

    /**
     * The number of seconds that a physical connection
     * should remain unused in the pool before the
     * connection is closed for a connection pool.
     * <p>
     * Default is vendor-specific
     * @since 1.1
     */
    int maxIdleTime() default -1;

    /**
     * The total number of statements that a connection pool should keep open.
     * A value of 0 indicates that the caching of statements is disabled for
     * a connection pool.
     * <p>
     * Default is vendor-specific
     * @since 1.1
     */
    int maxStatements() default -1;
    /**
     *  Used to specify  Vendor specific properties and less commonly
     * used <code>DataSource</code> properties such as:
     * <p>
     * <ul>
     * <li>dataSourceName
     * <li>networkProtocol
     * <li>propertyCycle
     * <li>roleName
     * </ul>
     * <p>
     *  Properties are specified using the format:
     *  <i>propertyName=propertyValue</i>  with one property per array element.
     * @since 1.1
     */
    String[] properties() default {};


    /**
     * Sets the maximum time in seconds that this data source will wait while
     * attempting to connect to a database. A value of zero specifies that
     * the timeout is the default system timeout if there is one; otherwise,
     * it specifies that there is no timeout.
     * <p>
     * Default is vendor-specific.
     * @since 1.1
     */
    int loginTimeout() default 0;
}
