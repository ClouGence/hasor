/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.hasor.maven;


/**
 * @author <a href="mailto:jerome@coffeebreaks.org">Jerome Lacoste</a>
 */
public class FindClassInClasspath
{
    public static final String FOUND_ALL = "OK";

    /**
     * @param args the names of classes to search in the classpath prints 'OK' if all classes found
     **/
    public static void main( String... args )
    {
        for ( String arg : args )
        {
            if ( !isClassInClasspath( arg ) )
            {
                System.out.println( "ERROR: class + " + arg + " not found in classpath" );
                System.exit( 1 );
            }
        }
        System.out.println( FOUND_ALL );
    }

    private static boolean isClassInClasspath( String className )
    {
        try
        {
            Class.forName( className );
            return true;
        }
        catch ( Exception e )
        {
            System.out.println( "ERROR: " + e.getMessage() );
            return false;
        }
    }
}
