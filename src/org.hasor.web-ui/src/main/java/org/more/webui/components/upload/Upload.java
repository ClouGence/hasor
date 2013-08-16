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
package org.more.webui.components.upload;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.more.webui.component.UIComponent;
import org.more.webui.component.support.NoState;
import org.more.webui.component.support.UICom;
import org.more.webui.component.values.MethodExpression;
import org.more.webui.components.UIInput;
import org.more.webui.context.ViewContext;
import org.more.webui.event.Event;
import org.more.webui.event.EventListener;
/**
 * <b>作用</b>：文件上传组建。
 * <br><b>组建类型</b>：ui_Upload
 * <br><b>标签</b>：@ui_Upload
 * <br><b>服务端事件</b>：OnUpLoad
 * <br><b>渲染器</b>：{@link UploadRender}
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
@UICom(tagName = "ui_Upload", renderType = UploadRender.class)
public class Upload extends UIInput {
    /**通用属性表*/
    public enum Propertys {
        /**接收上传的action地址，如果配置了该属性bizActionEL属性会失效。（R）*/
        uploadAction,
        /**文件上传的临时目录，默认目录是_uploadFileTempDir（-）。*/
        uploadTempDir,
        /**用于上传的内存缓存大小，默认10MB（-）。*/
        uploadSizeThreshold,
        /**默认允许的文件：任意（RW）*/
        allowFiles,
        /**默认允许的文件类型说明（RW）*/
        allowFilesDesc,
        /**默认允许的大小，例：1MB（RW）*/
        allowSize,
        /**上传按钮宽（RW）*/
        buttonWidth,
        /**上传按钮高（RW）*/
        buttonHeight,
        /**按钮上的图片（RW）*/
        buttonImage,
        /**是否允许多文件上传，默认不允许（RW）*/
        allowMulti,
        /**服务端处理动作，如果配置了uploadAction则该属性会失效。（-）*/
        bizActionEL,
        /**是否显示进度条（RW）*/
        showProgress,
    }
    @Override
    public String getComponentType() {
        return "ui_Upload";
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setPropertyMetaValue(Propertys.uploadAction.name(), null);
        this.setPropertyMetaValue(Propertys.uploadTempDir.name(), "/_uploadFileTempDir");
        this.setPropertyMetaValue(Propertys.uploadSizeThreshold.name(), 10 * 1024 * 1024);
        this.setPropertyMetaValue(Propertys.allowFiles.name(), "*");
        this.setPropertyMetaValue(Propertys.allowFilesDesc.name(), "ALL Files");
        this.setPropertyMetaValue(Propertys.allowSize.name(), "10MB");
        this.setPropertyMetaValue(Propertys.buttonWidth.name(), 65);
        this.setPropertyMetaValue(Propertys.buttonHeight.name(), 29);
        this.setPropertyMetaValue(Propertys.buttonImage.name(), null);
        this.setPropertyMetaValue(Propertys.allowMulti.name(), false);
        this.setPropertyMetaValue(Propertys.bizActionEL.name(), null);
        this.setPropertyMetaValue(Propertys.showProgress.name(), true);
        this.addEventListener(SWFUpload_Event_OnUpLoad.OnUpLoad, new SWFUpload_Event_OnUpLoad());
    }
    public String getUploadAction() {
        return this.getProperty(Propertys.uploadAction.name()).valueTo(String.class);
    }
    @NoState
    public void setUploadAction(String uploadAction) {
        this.getProperty(Propertys.uploadAction.name()).value(uploadAction);
    }
    @NoState
    public String getUploadTempDir() {
        return this.getProperty(Propertys.uploadTempDir.name()).valueTo(String.class);
    }
    @NoState
    public void setUploadTempDir(String uploadTempDir) {
        this.getProperty(Propertys.uploadTempDir.name()).value(uploadTempDir);
    }
    @NoState
    public int getUploadSizeThreshold() {
        return this.getProperty(Propertys.uploadSizeThreshold.name()).valueTo(Integer.TYPE);
    }
    @NoState
    public void setUploadSizeThreshold(int uploadSizeThreshold) {
        this.getProperty(Propertys.uploadSizeThreshold.name()).value(uploadSizeThreshold);
    }
    public String getAllowFilesDesc() {
        return this.getProperty(Propertys.allowFilesDesc.name()).valueTo(String.class);
    }
    public void setAllowFilesDesc(String allowFilesDesc) {
        this.getProperty(Propertys.allowFilesDesc.name()).value(allowFilesDesc);
    }
    public String getAllowFiles() {
        return this.getProperty(Propertys.allowFiles.name()).valueTo(String.class);
    }
    public void setAllowFiles(String allowFiles) {
        this.getProperty(Propertys.allowFiles.name()).value(allowFiles);
    }
    public String getAllowSize() {
        return this.getProperty(Propertys.allowSize.name()).valueTo(String.class);
    }
    public void setAllowSize(String allowSize) {
        this.getProperty(Propertys.allowSize.name()).value(allowSize);
    }
    public int getButtonWidth() {
        return this.getProperty(Propertys.buttonWidth.name()).valueTo(Integer.TYPE);
    }
    public void setButtonWidth(int buttonWidth) {
        this.getProperty(Propertys.buttonWidth.name()).value(buttonWidth);
    }
    public int getButtonHeight() {
        return this.getProperty(Propertys.buttonHeight.name()).valueTo(Integer.TYPE);
    }
    public void setButtonHeight(int buttonHeight) {
        this.getProperty(Propertys.buttonHeight.name()).value(buttonHeight);
    }
    public String getButtonImage() {
        return this.getProperty(Propertys.buttonImage.name()).valueTo(String.class);
    }
    public void setButtonImage(String buttonImage) {
        this.getProperty(Propertys.buttonImage.name()).value(buttonImage);
    }
    public boolean getAllowMulti() {
        return this.getProperty(Propertys.allowMulti.name()).valueTo(Boolean.TYPE);
    }
    public void setAllowMulti(boolean allowMulti) {
        this.getProperty(Propertys.allowMulti.name()).value(allowMulti);
    }
    public boolean getShowProgress() {
        return this.getProperty(Propertys.showProgress.name()).valueTo(Boolean.TYPE);
    }
    public void setShowProgress(boolean showProgress) {
        this.getProperty(Propertys.showProgress.name()).value(showProgress);
    }
    @NoState
    public void setBizActionEL(String bizActionEL) {
        this.getProperty(Propertys.bizActionEL.name()).value(bizActionEL);
    }
    @NoState
    public String getBizActionEL() {
        return this.getProperty(Propertys.bizActionEL.name()).valueTo(String.class);
    }
    private MethodExpression onBizActionExp = null;
    public MethodExpression getOnBizActionExpression() {
        if (this.onBizActionExp == null) {
            String onBizActionExpString = this.getBizActionEL();
            if (onBizActionExpString == null || onBizActionExpString.equals("")) {} else
                this.onBizActionExp = new MethodExpression(onBizActionExpString);
        }
        return this.onBizActionExp;
    }
}
/**负责处理OnUpLoad事件的EL调用*/
class SWFUpload_Event_OnUpLoad implements EventListener {
    public static Event OnUpLoad = Event.getEvent("OnUpLoad");
    public void onEvent(Event event, UIComponent component, ViewContext viewContext) throws Throwable {
        Upload swfUpload = (Upload) component;
        HttpServletRequest httpRequest = viewContext.getHttpRequest();
        ServletContext servletContext = httpRequest.getSession(true).getServletContext();
        if (ServletFileUpload.isMultipartContent(httpRequest) == false)
            return;// 检查输入请求是否包含multipart表单数据。
        try {
            //1.准备上传环境
            DiskFileItemFactory factory = new DiskFileItemFactory();// 为该请求创建一个DiskFileItemFactory对象，通过它来解析请求。执行解析后，所有的表单项目都保存在一个List中。
            ServletFileUpload upload = new ServletFileUpload(factory);
            String charset = httpRequest.getCharacterEncoding();
            if (charset != null)
                upload.setHeaderEncoding(charset);
            factory.setSizeThreshold(swfUpload.getUploadSizeThreshold());
            File uploadTempDir = new File(servletContext.getRealPath(swfUpload.getUploadTempDir()));
            if (uploadTempDir.exists() == false)
                uploadTempDir.mkdirs();
            factory.setRepository(uploadTempDir);
            //2.处理传完的文件
            List<FileItem> itemList = upload.parseRequest(httpRequest);
            List<FileItem> finalList = new ArrayList<FileItem>();
            Map<String, String> finalParam = new HashMap<String, String>();
            for (FileItem item : itemList)
                if (item.isFormField() == false)
                    finalList.add(item);
                else
                    finalParam.put(new String(item.getFieldName().getBytes("iso-8859-1")), new String(item.getString().getBytes("iso-8859-1")));
            //3.通知业务系统
            Object returnData = null;
            MethodExpression onBizActionExp = swfUpload.getOnBizActionExpression();
            if (onBizActionExp != null) {
                HashMap<String, Object> upObject = new HashMap<String, Object>();
                upObject.put("files", finalList);
                upObject.put("params", finalParam);
                HashMap<String, Object> upParam = new HashMap<String, Object>();
                upParam.put("up", upObject);
                returnData = onBizActionExp.execute(component, viewContext, upParam);
            }
            //4.清理数据
            for (FileItem item : itemList)
                try {
                    item.delete();
                } catch (Exception e) {}
            //5.相应输出结果
            viewContext.sendObject(returnData);
        } catch (Exception e) {
            viewContext.sendError(e);
        }
    }
};