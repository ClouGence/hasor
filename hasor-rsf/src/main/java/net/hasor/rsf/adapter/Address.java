/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.adapter;
import java.net.URL;
import net.hasor.core.EventListener;
/**
 * 
 * @version : 2015年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public interface Address {
    public URL getAddress();
    public boolean isInvalid();
    public boolean isStatic();
    public boolean equals(Object obj);
    public void setInvalid();
    //
    public void addListener(EventListener listener);
    public void removeListener(EventListener listener);
}