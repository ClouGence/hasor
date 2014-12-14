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

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author <a href="mailto:dsmiley@mitre.org">David Smiley</a>
 */
public class MainWithThreads
    extends Thread
{
    public static final String ALL_EXITED = "t1(interrupted td)(cancelled timer)";

    public static final String TIMER_IGNORED = "t1(interrupted td)";

    /**
     * both daemon threads will be interrupted as soon as the non daemon thread is done. the responsive daemon thread
     * will be interrupted right away. - if the timer is cancelled (using 'cancelTimer' as argument), the timer will die
     * on itself after all the other threads - if not, one must use a time out to stop joining on that unresponsive
     * daemon thread
     **/
    public static void main( String... args )
    {
        // long run so that we interrupt it before it ends itself
        Thread responsiveDaemonThread = new MainWithThreads( 60000, "td" );
        responsiveDaemonThread.setDaemon( true );
        responsiveDaemonThread.start();

        new MainWithThreads( 200, "t1" ).start();

        // Timer in Java <= 6 aren't interruptible
        final Timer t = new Timer( true );

        if ( optionsContains( args, "cancelTimer" ) )
        {
            t.schedule( new TimerTask()
            {
                public void run()
                {
                    System.out.print( "(cancelled timer)" );
                    t.cancel();
                }
            }, 400 );
        }
    }

    private static boolean optionsContains( String[] args, String option )
    {
        for ( String arg : args )
        {
            if ( arg.equals( option ) )
            {
                return true;
            }
        }
        return false;
    }

    private int millisecsToSleep;

    private String message;

    public MainWithThreads( int millisecsToSleep, String message )
    {
        this.millisecsToSleep = millisecsToSleep;
        this.message = message;
    }

    public void run()
    {
        try
        {
            Thread.sleep( millisecsToSleep );
        }
        catch ( InterruptedException e ) // IE's are a way to cancel a thread
        {
            System.out.print( "(interrupted " + message + ")" );
            return;
        }
        System.out.print( message );
    }
}
