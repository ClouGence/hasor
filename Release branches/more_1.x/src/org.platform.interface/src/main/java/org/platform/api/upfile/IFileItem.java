package org.platform.api.upfile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
/**
 * 
 * @version : 2013-3-12
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public interface IFileItem {
    public InputStream getInputStream() throws IOException;
    public OutputStream getOutputStream() throws IOException;
    public String getContentType();
    public String getName();
    public boolean isInMemory();
    public long getSize();
    public byte[] get();
    public String getString(String encoding) throws UnsupportedEncodingException;
    public String getString();
    public void write(File file) throws Exception;
    public void delete();
    public String getFieldName();
    public boolean isFormField();
}
