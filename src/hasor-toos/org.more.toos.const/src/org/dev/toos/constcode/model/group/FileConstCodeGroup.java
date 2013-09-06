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
package org.dev.toos.constcode.model.group;
import org.dev.toos.constcode.data.ConstDao;
import org.dev.toos.constcode.data.VarDao;
import org.dev.toos.constcode.data.xml.XmlConstDao;
import org.dev.toos.constcode.data.xml.XmlVarDao;
import org.dev.toos.constcode.model.ConstGroup;
import org.dev.toos.internal.util.Message;
import org.eclipse.core.resources.IFile;
/**
 * 
 * @version : 2013-2-2
 * @author ’‘”¿¥∫ (zyc@byshell.org) 
 */
public class FileConstCodeGroup extends ConstGroup {
    private IFile    inFile   = null;
    private ConstDao constDao = null;
    private VarDao   varDao   = null;
    //
    //
    public FileConstCodeGroup(String name, IFile fileSource) {
        super(FromType.Source);
        this.inFile = fileSource;
        this.setName(name);
        this.setReadOnly(this.inFile.isReadOnly());
    }
    @Override
    public void finishSave() throws Throwable {
        this.constDao.getSource().save();
    }
    @Override
    protected void initGroup() {
        try {
            this.constDao = new XmlConstDao(this.inFile);
            this.varDao = new XmlVarDao((XmlConstDao) this.constDao);
        } catch (Exception e) {
            Message.errorInfo("Load °Æ" + this.getName() + "°Ø Resource Error.", e);
        }
    }
    @Override
    public boolean reloadData() {
        if (this.getConstDao().getSource().isUpdate() == true)
            this.setConstChanged(false);
        return super.reloadData();
    }
    @Override
    protected ConstDao getConstDao() {
        return this.constDao;
    }
    @Override
    protected VarDao getVarDao() {
        return this.varDao;
    }
}