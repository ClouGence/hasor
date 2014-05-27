package org.dev.toos.constcode.data.xml;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.dev.toos.constcode.data.Source;
import org.dev.toos.constcode.data.xml.define.ConfigCodes;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IFile;
/**
 * 
 * @version : 2013-2-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlSource implements Source<ConfigCodes> {
    private ConfigCodes configCodes        = null;
    private boolean     canModify          = false;
    private InputStream constSource_Stream = null;
    private IFile       constSource_File   = null;
    private long        lastTime           = 0;
    //
    //
    //
    public XmlSource(IFile constSource) {
        canModify = !constSource.isReadOnly();
        this.constSource_File = constSource;
    }
    public XmlSource(InputStream constSource) {
        canModify = false;
        this.constSource_Stream = constSource;
    }
    //
    @Override
    public ConfigCodes getSource() throws MalformedURLException, IOException, JAXBException {
        if (this.isUpdate() == true)
            this.configCodes = null;
        if (this.configCodes == null) {
            InputStream inStream = null;
            this.lastTime = 0;
            if (this.constSource_Stream != null)
                inStream = this.constSource_Stream;
            else {
                this.lastTime = this.constSource_File.getLocalTimeStamp();
                inStream = this.constSource_File.getLocation().toFile().toURI().toURL().openStream();
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(ConfigCodes.class);
            this.configCodes = (ConfigCodes) jaxbContext.createUnmarshaller().unmarshal(inStream);
        }
        return this.configCodes;
    }
    @Override
    public boolean canModify() {
        return this.canModify;
    }
    @Override
    public void save() throws JAXBException, UnsupportedEncodingException, FileNotFoundException {
        if (this.canModify() == false)
            throw new UnsupportedOperationException();
        if (this.isUpdate() == true)
            throw new UnsupportedEncodingException(constSource_File.getName() + " 文件发生变化..");
        JAXBContext jaxbContext = JAXBContext.newInstance(ConfigCodes.class);
        File outputFile = this.constSource_File.getLocation().toFile();
        Marshaller marshaller = jaxbContext.createMarshaller();
        OutputFormat formater = OutputFormat.createPrettyPrint();
        formater.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(outputFile), formater);
        marshaller.marshal(this.configCodes, writer);
    }
    @Override
    public boolean isAutoSave() {
        return false;
    }
    @Override
    public void setAutoSave(boolean autoSave) {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean isUpdate() {
        if (constSource_File == null)
            return false;
        if (this.lastTime != this.constSource_File.getLocalTimeStamp())
            return true;
        return false;
    }
}