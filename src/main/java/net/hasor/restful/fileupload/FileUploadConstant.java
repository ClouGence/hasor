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
package net.hasor.restful.fileupload;
/**
 *
 * @version : 2016年1月11日
 * @author 赵永春(zyc@hasor.net)
 */
public interface FileUploadConstant {
    /**
     * HTTP content type header name.
     */
    public static final String CONTENT_TYPE        = "Content-type";
    /**
     * HTTP content disposition header name.
     */
    public static final String CONTENT_DISPOSITION = "Content-disposition";
    /**
     * HTTP content length header name.
     */
    public static final String CONTENT_LENGTH      = "Content-length";
    /**
     * Content-disposition value for form data.
     */
    public static final String FORM_DATA           = "form-data";
    /**
     * Content-disposition value for file attachment.
     */
    public static final String ATTACHMENT          = "attachment";
    /**
     * Part of HTTP content type header.
     */
    public static final String MULTIPART           = "multipart/";
    /**
     * HTTP content type header for multipart forms.
     */
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    /**
     * HTTP content type header for multiple uploads.
     */
    public static final String MULTIPART_MIXED     = "multipart/mixed";
}