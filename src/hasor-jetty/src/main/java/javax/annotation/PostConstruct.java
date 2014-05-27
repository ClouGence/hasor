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

package javax.annotation;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * The PostConstruct annotation is used on a method that needs to be executed 
 * after dependency injection is done to perform any initialization. This 
 * method MUST be invoked before the class is put into service. This 
 * annotation MUST be supported on all classes that support dependency 
 * injection. The method annotated with PostConstruct MUST be invoked even 
 * if the class does not request any resources to be injected. Only one 
 * method can be annotated with this annotation. The method on which the 
 * PostConstruct annotation is applied MUST fulfill all of the following 
 * criteria - 
- The method MUST NOT have any parameters except in the case of EJB 
 * interceptors   in which case it takes an InvocationC	ontext object as 
 * defined by the EJB   specification.
 * - The return type of the method MUST be void.
 * - The method MUST NOT throw a checked exception.
 * - The method on which PostConstruct is applied MAY be public, protected, 
 * package private or private.
 * - The method MUST NOT be static except for the application client.
 * - The method MAY be final.
 * - If the method throws an unchecked exception the class MUST NOT be put into   
 * service except in the case of EJBs where the EJB can handle exceptions and 
 * even   recover from them.
 * @since Common Annotations 1.0
 * @see javax.annotation.PreDestroy
 * @see javax.annotation.Resource
 */
@Documented
@Retention (RUNTIME)
@Target(METHOD)
public @interface PostConstruct {
}
