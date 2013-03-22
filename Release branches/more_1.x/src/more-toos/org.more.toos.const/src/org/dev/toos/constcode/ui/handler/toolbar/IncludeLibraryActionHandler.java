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
package org.dev.toos.constcode.ui.handler.toolbar;
import org.dev.toos.constcode.model.ConstModelSet;
import org.dev.toos.constcode.ui.handler.AbstractHandler;
import org.dev.toos.constcode.ui.job.InitLibraryJob;
import org.dev.toos.constcode.ui.view.ConstCodeView;
import org.dev.toos.internal.util.Message;
import org.dev.toos.ui.internal.ui.eclipse.wb.swt.ResourceManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
/**
 * 
 * @version : 2013-2-2
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class IncludeLibraryActionHandler extends AbstractHandler {
    public IncludeLibraryActionHandler(ConstCodeView uiView) {
        super("Load Library Const", uiView);
        this.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.eclipse.jdt.ui", "/icons/full/obj16/library_obj.gif"));
        this.setChecked(false);
    }
    @Override
    public void run() {
        this.getUiView().hideViewPage();
        if (this.isChecked() == true)
            this.doInit();
        else
            this.doHideGroup();
    }
    private void doInit() {
        InitLibraryJob job = new InitLibraryJob("load full library.....", new Runnable() {
            @Override
            public void run() {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        IncludeLibraryActionHandler.this.updataView();
                    }
                });
            }
        });
        job.setUser(true);
        job.schedule(100);
    }
    private void doHideGroup() {
        try {
            ConstModelSet.getActivateModel().cleanLibraryGroup();
            ConstModelSet.getActivateModel().refresh(new NullProgressMonitor());
        } catch (CoreException e) {
            Message.errorInfo("Refresh Job", e);
        }
        this.updataView();
    }
}