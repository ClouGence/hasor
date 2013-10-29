/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.dev.toos.constcode.ui.handler.toolbar;
import org.dev.toos.constcode.ui.handler.AbstractHandler;
import org.dev.toos.constcode.ui.job.RefreshModelJob;
import org.dev.toos.constcode.ui.view.ConstCodeView;
import org.dev.toos.ui.internal.ui.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.widgets.Display;
/**
 * 
 * @version : 2013-2-2
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class RefreshActionHandler extends AbstractHandler {
    public RefreshActionHandler(ConstCodeView uiView) {
        super("Refresh XML", uiView);
        this.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.dev.toos.constcode", "icons/i_refresh.gif"));
    }
    @Override
    public void run() {
        this.getUiView().hideViewPage();
        RefreshModelJob job = new RefreshModelJob("refresh projects.....", new Runnable() {
            @Override
            public void run() {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        RefreshActionHandler.this.updataView();
                    }
                });
            }
        });
        job.setUser(true);
        job.schedule(100);
    }
}