/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.servlet.jsp.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspContext;

import javax.el.PropertyNotWritableException;
import javax.el.ELContext;
import javax.el.ELResolver;

/**
 * Defines variable resolution behavior for the EL implicit objects
 * defined in the JSP specification.
 * 
 * <p>The following variables are resolved by this <code>ELResolver</code>,
 * as per the JSP specification:
 * <ul>
 *   <li><code>pageContext</code> - the <code>PageContext</code> object.</li>
 *   <li><code>pageScope</code> - a <code>Map</code> that maps page-scoped 
 *       attribute names to their values.</li>
 *   <li><code>requestScope</code> - a <code>Map</code> that maps 
 *       request-scoped attribute names to their values.</li>
 *   <li><code>sessionScope</code> - a <code>Map</code> that maps 
 *       session-scoped attribute names to their values.</li>
 *   <li><code>applicationScope</code> - a <code>Map</code> that maps 
 *       application-scoped attribute names to their values.</li>
 *   <li><code>param</code> - a <code>Map</code> that maps parameter 
 *       names to a single String parameter value (obtained by calling 
 *       <code>ServletRequest.getParameter(String name)).</li>
 *   <li><code>paramValues</code> - a <code>Map</code> that maps parameter
 *       names to a <code>String[]</code> of all values for that 
 *       parameter (obtained by calling
 *       <code>ServletRequest.getParameterValues(String name))</code>.</li>
 *   <li><code>header</code> - a <code>Map</code> that maps header names 
 *       to a single String header value (obtained by calling 
 *       <code>HttpServletRequest.getHeader(String name))</code>.</li>
 *   <li><code>headerValues</code> - a <code>Map</code> that maps header 
 *       names to a <code>String[]</code> of all values for that header
 *       (obtained by calling 
 *       <code>HttpServletRequest.getHeaders(String))</code>.</li>
 *   <li><code>cookie</code> - a <code>Map</code> that maps cookie names 
 *       to a single <code>Cookie</code> object. Cookies are retrieved 
 *       according to the semantics of 
 *       <code>HttpServletRequest.getCookies()</code>. If the same name 
 *       is shared by multiple cookies, an implementation must use the 
 *       first one encountered in the array of <code>Cookie</code> objects
 *       returned by the <code>getCookies()</code> method. However, users
 *       of the cookie implicit object must be aware that the ordering 
 *       of cookies is currently unspecified in the servlet 
 *       specification.</li>
 *   <li><code>initParam</code> - a <code>Map</code> that maps context
 *       initialization parameter names to their String parameter 
 *       value (obtained by calling 
 *       <code>ServletContext.getInitParameter(String name))</code>.</li>
 * </ul></p>
 *
 * @see javax.el.ELResolver
 * @since JSP 2.1
 */
public class ImplicitObjectELResolver extends ELResolver {

    /**
     * If the base object is <code>null</code>, and the property matches
     * the name of a JSP implicit object, returns the implicit object.
     *
     * <p>The <code>propertyResolved</code> property of the 
     * <code>ELContext</code> object must be set to <code>true</code> by 
     * this resolver before returning if an implicit object is matched. If 
     * this property is not <code>true</code> after this method is called,
     * the caller should ignore the return value.</p>
     *
     * @param context The context of this evaluation.
     * @param base Only <code>null</code> is handled by this resolver.
     *     Other values will result in an immediate return.
     * @param property The name of the implicit object to resolve.
     * @return If the <code>propertyResolved</code> property of 
     *     <code>ELContext</code> was set to <code>true</code>, then
     *     the implicit object; otherwise undefined.
     * @throws NullPointerException if context is <code>null</code>
     * @throws ELException if an exception was thrown while performing
     *     the property or variable resolution. The thrown exception
     *     must be included as the cause property of this exception, if
     *     available.
     */
    public Object getValue(ELContext context,
                           Object base,
                           Object property) {

        if (context == null) {
            throw new NullPointerException();
        }

        if (base != null) {
            return null;
        }

        PageContext ctxt = (PageContext)context.getContext(JspContext.class);
                               
        if ("pageContext".equals(property)) {
            context.setPropertyResolved(true);
            return ctxt;
        }
        ImplicitObjects implicitObjects =
            ImplicitObjects.getImplicitObjects(ctxt);
        if ("pageScope".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getPageScopeMap();
        }
        if ("requestScope".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getRequestScopeMap();
        }
        if ("sessionScope".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getSessionScopeMap();
        }
        if ("applicationScope".equals (property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getApplicationScopeMap ();
        }
        if ("param".equals (property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getParamMap();
        }
        if ("paramValues".equals (property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getParamsMap();
        }
        if ("header".equals (property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getHeaderMap();
        }
        if ("headerValues".equals (property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getHeadersMap();
        }
        if ("initParam".equals (property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getInitParamMap ();
        }
        if ("cookie".equals (property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getCookieMap ();
        }
        return null;
    }

    /**
     * If the base object is <code>null</code>, and the property matches
     * the name of a JSP implicit object, returns <code>null</code> to
     * indicate that no types are ever accepted to <code>setValue()</code>.
     *
     * <p>The <code>propertyResolved</code> property of the 
     * <code>ELContext</code> object must be set to <code>true</code> by 
     * this resolver before returning if an implicit object is matched. If 
     * this property is not <code>true</code> after this method is called,
     * the caller should ignore the return value.</p>
     *
     * @param context The context of this evaluation.
     * @param base Only <code>null</code> is handled by this resolver.
     *     Other values will result in an immediate return.
     * @param property The name of the implicit object to resolve.
     * @return If the <code>propertyResolved</code> property of 
     *     <code>ELContext</code> was set to <code>true</code>, then
     *     <code>null</code>; otherwise undefined.
     * @throws NullPointerException if context is <code>null</code>
     * @throws ELException if an exception was thrown while performing
     *     the property or variable resolution. The thrown exception
     *     must be included as the cause property of this exception, if
     *     available.
     */
    public Class getType(ELContext context,
                         Object base,
                         Object property) {

        if (context == null) {
            throw new NullPointerException();
        }

        if ((base == null) && ("pageContext".equals(property) || 
                "pageScope".equals(property)) ||
                "requestScope".equals(property) ||
                "sessionScope".equals(property) ||
                "applicationScope".equals (property) ||
                "param".equals (property) ||
                "paramValues".equals (property) ||
                "header".equals (property) ||
                "headerValues".equals (property) ||
                "initParam".equals (property) ||
                "cookie".equals (property)) {
            context.setPropertyResolved(true);
        }
        return null;
    }

    /**
     * If the base object is <code>null</code>, and the property matches
     * the name of a JSP implicit object, throws
     * <code>PropertyNotWritableException</code> to indicate that implicit
     * objects cannot be overwritten.
     *
     * <p>The <code>propertyResolved</code> property of the 
     * <code>ELContext</code> object must be set to <code>true</code> by 
     * this resolver before returning if an implicit object is matched. If 
     * this property is not <code>true</code> after this method is called,
     * the caller should ignore the return value.</p>
     *
     * @param context The context of this evaluation.
     * @param base Only <code>null</code> is handled by this resolver.
     *     Other values will result in an immediate return.
     * @param property The name of the implicit object.
     * @param val The value to be associated with the implicit object.
     * @throws NullPointerException if context is <code>null</code>.
     * @throws PropertyNotWritableException always thrown, if the 
     *     implicit object name is recognized by this resolver.
     * @throws ELException if an exception was thrown while performing
     *     the property or variable resolution. The thrown exception
     *     must be included as the cause property of this exception, if
     *     available.
     */
    public void  setValue(ELContext context,
                          Object base,
                          Object property,
                          Object val) {

        if (context == null) {
            throw new NullPointerException();
        }

        if ((base == null) && ("pageContext".equals(property) || 
                "pageScope".equals(property)) ||
                "requestScope".equals(property) ||
                "sessionScope".equals(property) ||
                "applicationScope".equals (property) ||
                "param".equals (property) ||
                "paramValues".equals (property) ||
                "header".equals (property) ||
                "headerValues".equals (property) ||
                "initParam".equals (property) ||
                "cookie".equals (property)) {
            throw new PropertyNotWritableException();
        }
    }

    /**
     * If the base object is <code>null</code>, and the property matches
     * the name of a JSP implicit object, returns <code>true</code>
     * to indicate that implicit objects cannot be overwritten.
     *
     * <p>The <code>propertyResolved</code> property of the 
     * <code>ELContext</code> object must be set to <code>true</code> by 
     * this resolver before returning if an implicit object is matched. If 
     * this property is not <code>true</code> after this method is called,
     * the caller should ignore the return value.</p>
     *
     * @param context The context of this evaluation.
     * @param base Only <code>null</code> is handled by this resolver.
     *     Other values will result in an immediate return.
     * @param property The name of the implicit object.
     * @return If the <code>propertyResolved</code> property of 
     *     <code>ELContext</code> was set to <code>true</code>, then
     *     <code>true</code>; otherwise undefined.
     * @throws NullPointerException if context is <code>null</code>.
     * @throws ELException if an exception was thrown while performing
     *     the property or variable resolution. The thrown exception
     *     must be included as the cause property of this exception, if
     *     available.
     */
    public boolean isReadOnly(ELContext context,
                              Object base,
                              Object property) {
        if (context == null) {
            throw new NullPointerException();
        }

        if ((base == null) && ("pageContext".equals(property) || 
                "pageScope".equals(property)) ||
                "requestScope".equals(property) ||
                "sessionScope".equals(property) ||
                "applicationScope".equals (property) ||
                "param".equals (property) ||
                "paramValues".equals (property) ||
                "header".equals (property) ||
                "headerValues".equals (property) ||
                "initParam".equals (property) ||
                "cookie".equals (property)) {
            context.setPropertyResolved(true);
            return true;
        }
        return false;	// Doesn't matter
    }

    /**
     * If the base object is <code>null</code>, and the property matches
     * the name of a JSP implicit object, returns an <code>Iterator</code>
     * containing <code>FeatureDescriptor</code> objects with information
     * about each JSP implicit object resolved by this resolver. Otherwise,
     * returns <code>null</code>.
     *
     * <p>The <code>Iterator</code> returned must contain one instance of 
     * {@link java.beans.FeatureDescriptor} for each of the EL implicit objects
     * defined by the JSP spec. Each info object contains information about 
     * a single implicit object, and is initialized as follows:
     * <dl>
     *     <li>displayName - The name of the implicit object.</li>
     *     <li>name - Same as displayName property.</li>
     *     <li>shortDescription - A suitable description for the implicit
     *         object. Will vary by implementation.</li>
     *     <li>expert - <code>false</code></li>
     *     <li>hidden - <code>false</code></li>
     *     <li>preferred - <code>true</code></li>
     * </dl>
     * In addition, the following named attributes must be set in the
     * returned <code>FeatureDescriptor</code>s:
     * <dl>
     *     <li>{@link ELResolver#TYPE} - The runtime type of the implicit object.</li>
     *     <li>{@link ELResolver#RESOLVABLE_AT_DESIGN_TIME} - <code>true</code>.</li>
     * </dl></p>
     *
     * @param context The context of this evaluation.
     * @param base Only <code>null</code> is handled by this resolver.
     *     Other values will result in a <code>null</code> return value.
     * @return An <code>Iterator</code> containing one 
     *     <code>FeatureDescriptor</code> object for each implicit object, or
     *     <code>null</code> if <code>base</code> is not <code>null</code>.
     */
    public Iterator<FeatureDescriptor> getFeatureDescriptors(
                                            ELContext context,
                                            Object base) {
        ArrayList<FeatureDescriptor> list =
                                        new ArrayList<FeatureDescriptor>(11);

        // pageContext
        FeatureDescriptor descriptor = new FeatureDescriptor();
        descriptor.setName("pageContext");
        descriptor.setDisplayName("pageContext");
        //descriptor.setShortDescription("");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", javax.servlet.jsp.PageContext.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);

        // pageScope
        descriptor = new FeatureDescriptor();
        descriptor.setName("pageScope");
        descriptor.setDisplayName("pageScope");
        //descriptor.setShortDescription("");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);

        // requestScope
        descriptor = new FeatureDescriptor();
        descriptor.setName("requestScope");
        descriptor.setDisplayName("requestScope");
        //descriptor.setShortDescription("");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);

        // sessionScope
        descriptor = new FeatureDescriptor();
        descriptor.setName("sessionScope");
        descriptor.setDisplayName("sessionScope");
        //descriptor.setShortDescription("");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);

        // applicationScope
        descriptor = new FeatureDescriptor();
        descriptor.setName("applicationScope");
        descriptor.setDisplayName("applicationScope");
        //descriptor.setShortDescription("");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);

        // param
        descriptor = new FeatureDescriptor();
        descriptor.setName("param");
        descriptor.setDisplayName("param");
        //descriptor.setShortDescription("");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);

        // paramValues
        descriptor = new FeatureDescriptor();
        descriptor.setName("paramValues");
        descriptor.setDisplayName("paramValues");
        //descriptor.setShortDescription("");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);

        // header
        descriptor = new FeatureDescriptor();
        descriptor.setName("header");
        descriptor.setDisplayName("header");
        //descriptor.setShortDescription("");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);

        // headerValues
        descriptor = new FeatureDescriptor();
        descriptor.setName("headerValues");
        descriptor.setDisplayName("headerValues");
        //descriptor.setShortDescription("");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);

        // cookie
        descriptor = new FeatureDescriptor();
        descriptor.setName("cookie");
        descriptor.setDisplayName("cookie");
        //descriptor.setShortDescription("");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);

        // initParam
        descriptor = new FeatureDescriptor();
        descriptor.setName("initParam");
        descriptor.setDisplayName("initParam");
        //descriptor.setShortDescription("");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);

        return list.iterator();
    }

    /**
     * If the base object is <code>null</code>, returns 
     * <code>String.class</code>.  Otherwise, returns <code>null</code>.
     *
     * @param context The context of this evaluation.
     * @param base Only <code>null</code> is handled by this resolver.
     *     Other values will result in a <code>null</code> return value.
     * @return <code>null</code> if base is not <code>null</code>; otherwise
     *     <code>String.class</code>.
     */
    public Class<String> getCommonPropertyType(ELContext context,
                                       Object base) {
        if (base == null) {
            return String.class;
        }
        return null;
    }

    // XXX - I moved this class from commons-el to an inner class here
    // so that we do not have a dependency from the JSP APIs into commons-el.
    // There might be a better way to do this.
    /**
     * <p>This class is used to generate the implicit Map and List objects
     * that wrap various elements of the PageContext.  It also returns the
     * correct implicit object for a given implicit object name.
     * 
     * @author Nathan Abramson - Art Technology Group
     **/
    private static class ImplicitObjects
    {
      //-------------------------------------
      // Constants
      //-------------------------------------

        
      // XXX - This probably needs to change, now that this is in a 
      // standard pkg.
      static final String sAttributeName = 
        "org.apache.taglibs.standard.ImplicitObjects";

      //-------------------------------------
      // Member variables
      //-------------------------------------

      PageContext mContext;
      Map mPage;
      Map mRequest;
      Map mSession;
      Map mApplication;
      Map mParam;
      Map mParams;
      Map mHeader;
      Map mHeaders;
      Map mInitParam;
      Map mCookie;

      //-------------------------------------
      /**
       *
       * Constructor
       **/
      public ImplicitObjects (PageContext pContext)
      {
        mContext = pContext;
      }

      //-------------------------------------
      /**
       *
       * Finds the ImplicitObjects associated with the PageContext,
       * creating it if it doesn't yet exist.
       **/
      public static ImplicitObjects getImplicitObjects (PageContext pContext)
      {
        ImplicitObjects objs = 
          (ImplicitObjects)
          pContext.getAttribute (sAttributeName,
                                 PageContext.PAGE_SCOPE);
        if (objs == null) {
          objs = new ImplicitObjects (pContext);
          pContext.setAttribute (sAttributeName,
                                 objs,
                                 PageContext.PAGE_SCOPE);
        }
        return objs;
      }

      //-------------------------------------
      /**
       *
       * Returns the Map that "wraps" page-scoped attributes
       **/
      public Map getPageScopeMap ()
      {
        if (mPage == null) {
          mPage = createPageScopeMap (mContext);
        }
        return mPage;
      }

      //-------------------------------------
      /**
       *
       * Returns the Map that "wraps" request-scoped attributes
       **/
      public Map getRequestScopeMap ()
      {
        if (mRequest == null) {
          mRequest = createRequestScopeMap (mContext);
        }
        return mRequest;
      }

      //-------------------------------------
      /**
       *
       * Returns the Map that "wraps" session-scoped attributes
       **/
      public Map getSessionScopeMap ()
      {
        if (mSession == null) {
          mSession = createSessionScopeMap (mContext);
        }
        return mSession;
      }

      //-------------------------------------
      /**
       *
       * Returns the Map that "wraps" application-scoped attributes
       **/
      public Map getApplicationScopeMap ()
      {
        if (mApplication == null) {
          mApplication = createApplicationScopeMap (mContext);
        }
        return mApplication;
      }

      //-------------------------------------
      /**
       *
       * Returns the Map that maps parameter name to a single parameter
       * values.
       **/
      public Map getParamMap ()
      {
        if (mParam == null) {
          mParam = createParamMap (mContext);
        }
        return mParam;
      }

      //-------------------------------------
      /**
       *
       * Returns the Map that maps parameter name to an array of parameter
       * values.
       **/
      public Map getParamsMap ()
      {
        if (mParams == null) {
          mParams = createParamsMap (mContext);
        }
        return mParams;
      }

      //-------------------------------------
      /**
       *
       * Returns the Map that maps header name to a single header
       * values.
       **/
      public Map getHeaderMap ()
      {
        if (mHeader == null) {
          mHeader = createHeaderMap (mContext);
        }
        return mHeader;
      }

      //-------------------------------------
      /**
       *
       * Returns the Map that maps header name to an array of header
       * values.
       **/
      public Map getHeadersMap ()
      {
        if (mHeaders == null) {
          mHeaders = createHeadersMap (mContext);
        }
        return mHeaders;
      }

      //-------------------------------------
      /**
       *
       * Returns the Map that maps init parameter name to a single init
       * parameter values.
       **/
      public Map getInitParamMap ()
      {
        if (mInitParam == null) {
          mInitParam = createInitParamMap (mContext);
        }
        return mInitParam;
      }

      //-------------------------------------
      /**
       *
       * Returns the Map that maps cookie name to the first matching
       * Cookie in request.getCookies().
       **/
      public Map getCookieMap ()
      {
        if (mCookie == null) {
          mCookie = createCookieMap (mContext);
        }
        return mCookie;
      }

      //-------------------------------------
      // Methods for generating wrapper maps
      //-------------------------------------
      /**
       *
       * Creates the Map that "wraps" page-scoped attributes
       **/
      public static Map createPageScopeMap (PageContext pContext)
      {
        final PageContext context = pContext;
        return new EnumeratedMap ()
          {
            public Enumeration enumerateKeys () 
            {
              return context.getAttributeNamesInScope
                (PageContext.PAGE_SCOPE);
            }

            public Object getValue (Object pKey) 
            {
              if (pKey instanceof String) {
                return context.getAttribute
                  ((String) pKey, 
                   PageContext.PAGE_SCOPE);
              }
              else {
                return null;
              }
            }

            public boolean isMutable ()
            {
              return true;
            }
          };
      }

      //-------------------------------------
      /**
       *
       * Creates the Map that "wraps" request-scoped attributes
       **/
      public static Map createRequestScopeMap (PageContext pContext)
      {
        final PageContext context = pContext;
        return new EnumeratedMap ()
          {
            public Enumeration enumerateKeys () 
            {
              return context.getAttributeNamesInScope
                (PageContext.REQUEST_SCOPE);
            }

            public Object getValue (Object pKey) 
            {
              if (pKey instanceof String) {
                return context.getAttribute
                  ((String) pKey, 
                   PageContext.REQUEST_SCOPE);
              }
              else {
                return null;
              }
            }

            public boolean isMutable ()
            {
              return true;
            }
          };
      }

      //-------------------------------------
      /**
       *
       * Creates the Map that "wraps" session-scoped attributes
       **/
      public static Map createSessionScopeMap (PageContext pContext)
      {
        final PageContext context = pContext;
        return new EnumeratedMap ()
          {
            public Enumeration enumerateKeys () 
            {
              return context.getAttributeNamesInScope
                (PageContext.SESSION_SCOPE);
            }

            public Object getValue (Object pKey) 
            {
              if (pKey instanceof String) {
                return context.getAttribute
                  ((String) pKey, 
                   PageContext.SESSION_SCOPE);
              }
              else {
                return null;
              }
            }

            public boolean isMutable ()
            {
              return true;
            }
          };
      }

      //-------------------------------------
      /**
       *
       * Creates the Map that "wraps" application-scoped attributes
       **/
      public static Map createApplicationScopeMap (PageContext pContext)
      {
        final PageContext context = pContext;
        return new EnumeratedMap ()
          {
            public Enumeration enumerateKeys () 
            {
              return context.getAttributeNamesInScope
                (PageContext.APPLICATION_SCOPE);
            }

            public Object getValue (Object pKey) 
            {
              if (pKey instanceof String) {
                return context.getAttribute
                  ((String) pKey, 
                   PageContext.APPLICATION_SCOPE);
              }
              else {
                return null;
              }
            }

            public boolean isMutable ()
            {
              return true;
            }
          };
      }

      //-------------------------------------
      /**
       *
       * Creates the Map that maps parameter name to single parameter
       * value.
       **/
      public static Map createParamMap (PageContext pContext)
      {
        final HttpServletRequest request =
          (HttpServletRequest) pContext.getRequest ();
        return new EnumeratedMap ()
          {
            public Enumeration enumerateKeys () 
            {
              return request.getParameterNames ();
            }

            public Object getValue (Object pKey) 
            {
              if (pKey instanceof String) {
                return request.getParameter ((String) pKey);
              }
              else {
                return null;
              }
            }

            public boolean isMutable ()
            {
              return false;
            }
          };
      }

      //-------------------------------------
      /**
       *
       * Creates the Map that maps parameter name to an array of parameter
       * values.
       **/
      public static Map createParamsMap (PageContext pContext)
      {
        final HttpServletRequest request =
          (HttpServletRequest) pContext.getRequest ();
        return new EnumeratedMap ()
          {
            public Enumeration enumerateKeys () 
            {
              return request.getParameterNames ();
            }

            public Object getValue (Object pKey) 
            {
              if (pKey instanceof String) {
                return request.getParameterValues ((String) pKey);
              }
              else {
                return null;
              }
            }

            public boolean isMutable ()
            {
              return false;
            }
          };
      }

      //-------------------------------------
      /**
       *
       * Creates the Map that maps header name to single header
       * value.
       **/
      public static Map createHeaderMap (PageContext pContext)
      {
        final HttpServletRequest request =
          (HttpServletRequest) pContext.getRequest ();
        return new EnumeratedMap ()
          {
            public Enumeration enumerateKeys () 
            {
              return request.getHeaderNames ();
            }

            public Object getValue (Object pKey) 
            {
              if (pKey instanceof String) {
                return request.getHeader ((String) pKey);
              }
              else {
                return null;
              }
            }

            public boolean isMutable ()
            {
              return false;
            }
          };
      }

      //-------------------------------------
      /**
       *
       * Creates the Map that maps header name to an array of header
       * values.
       **/
      public static Map createHeadersMap (PageContext pContext)
      {
        final HttpServletRequest request =
          (HttpServletRequest) pContext.getRequest ();
        return new EnumeratedMap ()
          {
            public Enumeration enumerateKeys () 
            {
              return request.getHeaderNames ();
            }

            public Object getValue (Object pKey) 
            {
              if (pKey instanceof String) {
                // Drain the header enumeration
                List l = new ArrayList ();
                Enumeration e = request.getHeaders ((String) pKey);
                if (e != null) {
                  while (e.hasMoreElements ()) {
                    l.add (e.nextElement ());
                  }
                }
                String [] ret = (String []) l.toArray (new String [l.size ()]);
                return ret;
              }
              else {
                return null;
              }
            }

            public boolean isMutable ()
            {
              return false;
            }
          };
      }

      //-------------------------------------
      /**
       *
       * Creates the Map that maps init parameter name to single init
       * parameter value.
       **/
      public static Map createInitParamMap (PageContext pContext)
      {
        final ServletContext context = pContext.getServletContext ();
        return new EnumeratedMap ()
          {
            public Enumeration enumerateKeys () 
            {
              return context.getInitParameterNames ();
            }

            public Object getValue (Object pKey) 
            {
              if (pKey instanceof String) {
                return context.getInitParameter ((String) pKey);
              }
              else {
                return null;
              }
            }

            public boolean isMutable ()
            {
              return false;
            }
          };
      }

      //-------------------------------------
      /**
       *
       * Creates the Map that maps cookie name to the first matching
       * Cookie in request.getCookies().
       **/
      public static Map createCookieMap (PageContext pContext)
      {
        // Read all the cookies and construct the entire map
        HttpServletRequest request = (HttpServletRequest) pContext.getRequest ();
        Cookie [] cookies = request.getCookies ();
        Map ret = new HashMap ();
        for (int i = 0; cookies != null && i < cookies.length; i++) {
          Cookie cookie = cookies [i];
          if (cookie != null) {
            String name = cookie.getName ();
            if (!ret.containsKey (name)) {
              ret.put (name, cookie);
            }
          }
        }
        return ret;
      }

      //-------------------------------------
    }
    
    // XXX - I moved this class from commons-el to an inner class here
    // so that we do not have a dependency from the JSP APIs into commons-el.
    // There might be a better way to do this.
    /**
     * <p>This is a Map implementation driven by a data source that only
     * provides an enumeration of keys and a getValue(key) method.  This
     * class must be subclassed to implement those methods.
     *
     * <p>Some of the methods may incur a performance penalty that
     * involves enumerating the entire data source.  In these cases, the
     * Map will try to save the results of that enumeration, but only if
     * the underlying data source is immutable.
     * 
     * @author Nathan Abramson - Art Technology Group
     **/
    private static abstract class EnumeratedMap
      implements Map
    {
      //-------------------------------------
      // Member variables
      //-------------------------------------

      Map mMap;

      //-------------------------------------
      public void clear ()
      {
        throw new UnsupportedOperationException ();
      }

      //-------------------------------------
      public boolean containsKey (Object pKey)
      {
        return getValue (pKey) != null;
      }

      //-------------------------------------
      public boolean containsValue (Object pValue)
      {
        return getAsMap ().containsValue (pValue);
      }

      //-------------------------------------
      public Set entrySet ()
      {
        return getAsMap ().entrySet ();
      }

      //-------------------------------------
      public Object get (Object pKey)
      {
        return getValue (pKey);
      }

      //-------------------------------------
      public boolean isEmpty ()
      {
        return !enumerateKeys ().hasMoreElements ();
      }

      //-------------------------------------
      public Set keySet ()
      {
        return getAsMap ().keySet ();
      }

      //-------------------------------------
      public Object put (Object pKey, Object pValue)
      {
        throw new UnsupportedOperationException ();
      }

      //-------------------------------------
      public void putAll (Map pMap)
      {
        throw new UnsupportedOperationException ();
      }

      //-------------------------------------
      public Object remove (Object pKey)
      {
        throw new UnsupportedOperationException ();
      }

      //-------------------------------------
      public int size ()
      {
        return getAsMap ().size ();
      }

      //-------------------------------------
      public Collection values ()
      {
        return getAsMap ().values ();
      }

      //-------------------------------------
      // Abstract methods
      //-------------------------------------
      /**
       *
       * Returns an enumeration of the keys
       **/
      public abstract Enumeration enumerateKeys ();

      //-------------------------------------
      /**
       *
       * Returns true if it is possible for this data source to change
       **/
      public abstract boolean isMutable ();

      //-------------------------------------
      /**
       *
       * Returns the value associated with the given key, or null if not
       * found.
       **/
      public abstract Object getValue (Object pKey);

      //-------------------------------------
      /**
       *
       * Converts the MapSource to a Map.  If the map is not mutable, this
       * is cached
       **/
      public Map getAsMap ()
      {
        if (mMap != null) {
          return mMap;
        }
        else {
          Map m = convertToMap ();
          if (!isMutable ()) {
            mMap = m;
          }
          return m;
        }
      }

      //-------------------------------------
      /**
       *
       * Converts to a Map
       **/
      Map convertToMap ()
      {
        Map ret = new HashMap ();
        for (Enumeration e = enumerateKeys (); e.hasMoreElements (); ) {
          Object key = e.nextElement ();
          Object value = getValue (key);
          ret.put (key, value);
        }
        return ret;
      }

      //-------------------------------------
    }
}

