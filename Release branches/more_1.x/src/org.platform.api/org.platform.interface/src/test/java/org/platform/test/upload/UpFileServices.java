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
package org.platform.test.upload;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.platform.api.event.InitEvent;
import org.platform.api.upfile.IFileItem;
import org.platform.api.upfile.IUpFile;
import org.platform.api.upfile.IUpFilePolicy;
import org.platform.api.upfile.IUpInfo;
import org.platform.api.upfile.InitParam;
import org.platform.api.upfile.UpFilePolicy;
import org.platform.api.upfile.UpLoadFile;
/**
 * 
 * @version : 2013-3-26
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@UpLoadFile(value = "UpFileServices", initParam = { @InitParam(name = "", value = "") })
@UpFilePolicy(policyName = "UpFileServicesPolicy", withService = "UpFileServices")
public class UpFileServices implements IUpFile, IUpFilePolicy {
    @Override
    public void initPolicy(InitEvent event) {}
    @Override
    public PolicyResult runPolicy(IUpInfo upData, List<IFileItem> list) {
        return PolicyResult.ExitPolicy;
    }
    @Override
    public void initUpFile(InitEvent event) {}
    @Override
    public void doUpFile(IUpInfo upData) throws IOException {
        // TODO Auto-generated method stub
        upData.getItem("").write(new File(""), false);
    }
}