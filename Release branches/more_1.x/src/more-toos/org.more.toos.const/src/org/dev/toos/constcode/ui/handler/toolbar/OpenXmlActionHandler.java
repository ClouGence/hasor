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
import org.dev.toos.constcode.ui.handler.AbstractHandler;
import org.dev.toos.constcode.ui.view.ConstCodeView;
import org.dev.toos.ui.internal.ui.eclipse.wb.swt.ResourceManager;
/**
 * 
 * @version : 2013-2-2
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class OpenXmlActionHandler extends AbstractHandler {
    public OpenXmlActionHandler(ConstCodeView uiView) {
        super("Open XML", uiView);
        this.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.eclipse.ui", "/icons/full/etool16/editor_area.gif"));
    }
    @Override
    public void run() {
        //
        //        ConstModel activateMode = ConstModelSet.getActivateModel();
        //        ConstGroup constGroup = activateMode.getCurrentGroup();
        //        if (constGroup == null)
        //            constGroup = activateMode.getGroups().get(0);
        //        //
        //        try {
        //            IPath inputFile = constGroup.getSourceEnt();
        //            IWorkbenchPage workPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        //            //                    workPage
        //            IEditorDescriptor xmlDesc = IDE.getEditorDescriptor("*.xml");
        //            IEditorDescriptor defaultDesc = IDE.getEditorDescriptor("*.txt");
        //            FileEditorInput input = new FileEditorInput();
        //            IDE.openEditor(workPage, inputFile.toFile().toURI(), xmlDesc.getId(), true);
        //            System.out.println();
        //        } catch (PartInitException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
    }
}