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
package net.hasor.core.environment;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.core.setting.FileSettings;
/**
 * {@link Environment}接口实现类。
 * @version : 2013-9-11
 * @author 赵永春(zyc@hasor.net)
 */
public class FileEnvironment extends AbstractEnvironment {
    public FileEnvironment(String mainSettings) throws IOException, URISyntaxException {
        if (mainSettings != null)
            this.settingFile = new File(mainSettings);
        this.initEnvironment();
    }
    public FileEnvironment(File mainSettings) {
        if (mainSettings != null)
            this.settingFile = mainSettings;
        this.initEnvironment();
    }
    //---------------------------------------------------------------------------------Basic Method
    private File settingFile;
    public final URI getSettingURI() {
        if (this.settingFile != null)
            return this.settingFile.toURI();
        return null;
    }
    /**获取配置文件*/
    protected final File getSettingFile() {
        return this.settingFile;
    }
    protected Settings createSettings() throws IOException {
        FileSettings fileSettings = new FileSettings();
        fileSettings.addFile(this.settingFile);
        return fileSettings;
    }
}