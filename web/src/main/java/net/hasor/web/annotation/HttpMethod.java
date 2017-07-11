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
package net.hasor.web.annotation;
import java.lang.annotation.*;
/**
 * Associates the name of a HTTP method with an annotation. A Java method annotated
 * with a runtime annotation that is itself annotated with this annotation will
 * be used to handle HTTP requests of the indicated HTTP method. It is an error
 * for a method to be annotated with more than one annotation that is annotated
 * with {@code HttpMethod}.
 *
 * @see HttpMethod#ANY
 * @see HttpMethod#GET
 * @see HttpMethod#POST
 * @see HttpMethod#PUT
 * @see HttpMethod#DELETE
 * @see HttpMethod#HEAD
 * @see HttpMethod#OPTIONS
 */
@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpMethod {
    /** HTTP ANY method */
    public static final String ANY     = "ANY";
    /** HTTP GET method */
    public static final String GET     = "GET";
    /** HTTP POST method */
    public static final String POST    = "POST";
    /** HTTP PUT method */
    public static final String PUT     = "PUT";
    /** HTTP DELETE method */
    public static final String DELETE  = "DELETE";
    /** HTTP HEAD method */
    public static final String HEAD    = "HEAD";
    /** HTTP OPTIONS method */
    public static final String OPTIONS = "OPTIONS";

    /** Specifies the name of a HTTP method. E.g. "GET". */
    public String value();
}