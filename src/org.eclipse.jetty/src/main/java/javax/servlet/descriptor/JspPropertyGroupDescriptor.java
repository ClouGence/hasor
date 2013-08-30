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

package javax.servlet.descriptor;

import java.util.Collection;

/**
 * This interface provides access to the
 * <code>&lt;jsp-property-group&gt;</code>
 * related configuration of a web application.
 *
 * <p>The configuration is aggregated from the <code>web.xml</code> and
 * <code>web-fragment.xml</code> descriptor files of the web application.
 *
 * @since Servlet 3.0
 */
public interface JspPropertyGroupDescriptor {
    
    /**
     * Gets the URL patterns of the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>.
     *
     * <p>Any changes to the returned <code>Collection</code> must not 
     * affect this <code>JspPropertyGroupDescriptor</code>.
     *
     * @return a (possibly empty) <code>Collection</code> of the URL
     * patterns of the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>
     */
    public Collection<String> getUrlPatterns();

    /**
     * Gets the value of the <code>el-ignored</code> configuration, which
     * specifies whether Expression Language (EL) evaluation is enabled for
     * any JSP pages mapped to the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>.
     *
     * @return the value of the <code>el-ignored</code> configuration, or
     * null if unspecified
     */
    public String getElIgnored();

    /**
     * Gets the value of the <code>page-encoding</code> configuration,
     * which specifies the default page encoding for any JSP pages mapped
     * to the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>.
     *
     * @return the value of the <code>page-encoding</code> configuration, or
     * null if unspecified
     */
    public String getPageEncoding();

    /**
     * Gets the value of the <code>scripting-invalid</code> configuration,
     * which specifies whether scripting is enabled for any JSP pages mapped
     * to the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>.
     *
     * @return the value of the <code>scripting-invalid</code> configuration,
     * or null if unspecified
     */
    public String getScriptingInvalid();

    /**
     * Gets the value of the <code>is-xml</code> configuration, which 
     * specifies whether any JSP pages mapped to the JSP property group
     * represented by this <code>JspPropertyGroupDescriptor</code> will
     * be treated as JSP documents (XML syntax).
     *
     * @return the value of the <code>is-xml</code> configuration, or
     * null if unspecified
     */
    public String getIsXml();

    /**
     * Gets the <code>include-prelude</code> configuration
     * of the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>.
     *
     * <p>Any changes to the returned <code>Collection</code> must not 
     * affect this <code>JspPropertyGroupDescriptor</code>.
     *
     * @return a (possibly empty) <code>Collection</code> of the
     * <code>include-prelude</code> configuration of
     * the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>
     */
    public Collection<String> getIncludePreludes();

    /**
     * Gets the <code>include-coda</code> configuration
     * of the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>.
     *
     * <p>Any changes to the returned <code>Collection</code> must not 
     * affect this <code>JspPropertyGroupDescriptor</code>.
     *
     * @return a (possibly empty) <code>Collection</code> of the
     * <code>include-coda</code> configuration of
     * the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>
     */
    public Collection<String> getIncludeCodas();

    /**
     * Gets the value of the
     * <code>deferred-syntax-allowed-as-literal</code> configuration, which
     * specifies whether the character sequence <code>&quot;#{&quot;</code>,
     * which is normally reserved for Expression Language (EL) expressions,
     * will cause a translation error if it appears as a String literal
     * in any JSP pages mapped to the JSP property group represented by
     * this <code>JspPropertyGroupDescriptor</code>.
     *
     * @return the value of the
     * <code>deferred-syntax-allowed-as-literal</code> configuration, or
     * null if unspecified
     */
    public String getDeferredSyntaxAllowedAsLiteral();

    /**
     * Gets the value of the <code>trim-directive-whitespaces</code>
     * configuration, which specifies whether template text containing only
     * whitespaces must be removed from the response output of any JSP
     * pages mapped to the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>.
     *
     * @return the value of the <code>trim-directive-whitespaces</code>
     * configuration, or null if unspecified
     */
    public String getTrimDirectiveWhitespaces();

    /**
     * Gets the value of the <code>default-content-type</code> configuration,
     * which specifies the default response content type for any JSP pages
     * mapped to the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>.
     *
     * @return the value of the <code>default-content-type</code>
     * configuration, or null if unspecified
     */
    public String getDefaultContentType();

    /**
     * Gets the value of the <code>buffer</code> configuration, which
     * specifies the default size of the response buffer for any JSP pages
     * mapped to the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>.
     *
     * @return the value of the <code>buffer</code> configuration, or
     * null if unspecified
     */
    public String getBuffer();

    /**
     * Gets the value of the <code>error-on-undeclared-namespace</code>
     * configuration, which specifies whether an error will be raised at
     * translation time if tag with an undeclared namespace is used in
     * any JSP pages mapped to the JSP property group represented by this
     * <code>JspPropertyGroupDescriptor</code>.
     *
     * @return the value of the <code>error-on-undeclared-namespace</code>
     * configuration, or null if unspecified
     */
    public String getErrorOnUndeclaredNamespace();
}
