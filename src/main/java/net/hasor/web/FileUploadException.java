/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.web;
import java.io.IOException;
/**
 * 文件上传异常。
 * @version 2009-4-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class FileUploadException extends IOException {
    public static enum UploadErrorCodes {
        MalformedStreamException(),         //
        FileSizeLimitExceededException(),   //
        FileUploadException(),              //
        IllegalBoundaryException(),         //
        InvalidContentTypeException(),      //
        ItemSkippedException(),             //
        SizeLimitExceededException(),;      //
    }
    //
    private UploadErrorCodes errorCode;
    //
    public FileUploadException(String errorMessage) {
        this(UploadErrorCodes.FileUploadException, errorMessage);
    }
    public FileUploadException(UploadErrorCodes errorCode) {
        super();
        this.errorCode = errorCode;
    }
    public FileUploadException(UploadErrorCodes errorCode, String errorMEssage) {
        super(errorMEssage);
        this.errorCode = errorCode;
    }
    public UploadErrorCodes getErrorCode() {
        return errorCode;
    }
}