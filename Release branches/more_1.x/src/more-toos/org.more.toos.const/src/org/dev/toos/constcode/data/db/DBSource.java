package org.dev.toos.constcode.data.db;
//package org.noe.devtoos.constcode.model.data.db;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.sql.Connection;
//import java.sql.SQLException;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import org.eclipse.core.resources.IFile;
//import org.noe.devtoos.constcode.model.data.Source;
//import org.noe.devtoos.constcode.model.data.xml.define.ConfigCodes;
///**
// * 
// * @version : 2013-2-17
// * @author ’‘”¿¥∫ (zyc@byshell.org)
// */
//public class DBSource implements Source<Connection> {
//    private Connection connection = null;
//    private Boolean    canModify  = null;
//    //
//    //
//    //
//    public DBSource(IFile constSource) {
//        canModify = !constSource.isReadOnly();
//        this.constSource_File = constSource;
//    }
//    public DBSource(InputStream constSource) {
//        canModify = false;
//        this.constSource_Stream = constSource;
//    }
//    //
//    @Override
//    public Connection getSource() throws MalformedURLException, IOException, JAXBException {
//        if (this.connection == null) {
//            InputStream inStream = null;
//            if (this.constSource_Stream != null)
//                inStream = this.constSource_Stream;
//            else
//                inStream = this.constSource_File.getLocation().toFile().toURI().toURL().openStream();
//            JAXBContext jaxbContext = JAXBContext.newInstance(ConfigCodes.class);
//            this.configCodes = (ConfigCodes) jaxbContext.createUnmarshaller().unmarshal(inStream);
//        }
//        return this.configCodes;
//    }
//    @Override
//    public boolean canModify() {
//        if (this.canModify == null)
//            try {
//                this.canModify = this.connection.isReadOnly();
//            } catch (SQLException e) {
//                openConn();
//                this.canModify = this.canModify = this.connection.isReadOnly();
//            }
//        return this.canModify;
//    }
//    @Override
//    public void save() throws JAXBException {
//        if (this.canModify() == false)
//            throw new UnsupportedOperationException();
//        JAXBContext jaxbContext = JAXBContext.newInstance(ConfigCodes.class);
//        File outputFile = this.constSource_File.getLocation().toFile();
//        jaxbContext.createMarshaller().marshal(this.configCodes, outputFile);
//    }
//    @Override
//    public boolean isAutoSave() {
//        return false;
//    }
//    @Override
//    public void setAutoSave(boolean autoSave) {
//        throw new UnsupportedOperationException();
//    }
//}