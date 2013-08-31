/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jasper.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;

/**
 * Contains static utilities for generating SMAP data based on the
 * current version of Jasper.
 * 
 * @author Jayson Falkner
 * @author Shawn Bayern
 * @author Robert Field (inner SDEInstaller class)
 * @author Mark Roth
 * @author Kin-man Chung
 */
public class SmapUtil {

    private JspCompilationContext ctxt;
    private List<ClassInfo> classInfos;

    //*********************************************************************
    // Constants

    public static final String SMAP_ENCODING = "UTF-8";

    //*********************************************************************
    // Public entry points

    SmapUtil(JspCompilationContext ctxt) {
        this.ctxt = ctxt;
    }

    /**
     * Generates an appropriate SMAP representing the current compilation
     * context.  (JSR-045.)
     *
     * @param ctxt Current compilation context
     * @param pageNodes The current JSP page
     * @return a SMAP for the page
     */
    public void generateSmap(Node.Nodes pageNodes)
        throws IOException {

        classInfos = new ArrayList<ClassInfo>();
        String className = ctxt.getFullClassName();

        // Instantiate a SmapStratum for the main JSP page.
        SmapStratum s = new SmapStratum("JSP");
        classInfos.add(new ClassInfo(className, s));

        // Map out Node.Nodes and putting LineInfo into SmapStratum
        evaluateNodes(pageNodes, s, ctxt.getOptions().getMappedFile());

        String classFileName = ctxt.getClassFileName();
        for (ClassInfo entry: classInfos) {
            // Get SmapStratum
            s = entry.getSmapStratum();
            s.optimizeLineSection();

            // Set up our SMAP generator
            SmapGenerator g = new SmapGenerator();
            g.setOutputFileName(unqualify(ctxt.getServletJavaFileName()));
            g.addStratum(s, true);
 
            String name = entry.getClassName();  // class name
            // Compute the class name and output file name for inner classes
            if (!className.equals(name)) {
                classFileName = ctxt.getOutputDir() + 
                    name.substring(name.lastIndexOf('.')+1) + ".class";
            }
            entry.setClassFileName(classFileName);
            entry.setSmap(g.getString());

            if (ctxt.getOptions().isSmapDumped()) {
                File outSmap = new File(classFileName + ".smap");
                PrintWriter so =
                    new PrintWriter(
                        new OutputStreamWriter(
                            new FileOutputStream(outSmap),
                            SMAP_ENCODING));
                so.print(g.getString());
                so.close();
            }
        }
    }

    public void installSmap() throws IOException {

        for (ClassInfo ci: classInfos) {
            String className = ci.getClassName();
            byte[] classfile = ctxt.getRuntimeContext().getBytecode(className);
            if (classfile == null) {
                SDEInstaller.install(new File(ci.getClassFileName()),
                                     ci.getSmap().getBytes());
            }
            else {
                classfile = SDEInstaller.install(classfile,
                                                 ci.getSmap().getBytes());
                ctxt.getRuntimeContext().setBytecode(className, classfile);
            }
        }
    }

    //*********************************************************************
    // Private utilities

    /**
     * Returns an unqualified version of the given file path.
     */
    private static String unqualify(String path) {
        path = path.replace('\\', '/');
        return path.substring(path.lastIndexOf('/') + 1);
    }

    //*********************************************************************
    // Installation logic (from Robert Field, JSR-045 spec lead)
    private static class SDEInstaller {

        static final String nameSDE = "SourceDebugExtension";

        byte[] orig;
        byte[] sdeAttr;
        byte[] gen;

        int origPos = 0;
        int genPos = 0;

        int sdeIndex;

        public static void main(String[] args) throws IOException {
            if (args.length == 2) {
                install(new File(args[0]), new File(args[1]));
            } else if (args.length == 3) {
                install(
                    new File(args[0]),
                    new File(args[1]),
                    new File(args[2]));
            } else {
                System.err.println(
                    "Usage: <command> <input class file> "
                        + "<attribute file> <output class file name>\n"
                        + "<command> <input/output class file> <attribute file>");
            }
        }

        static void install(File inClassFile, File attrFile, File outClassFile)
            throws IOException {
            new SDEInstaller(inClassFile, attrFile, outClassFile);
        }

        static void install(File inOutClassFile, File attrFile)
            throws IOException {
            File tmpFile = new File(inOutClassFile.getPath() + "tmp");
            new SDEInstaller(inOutClassFile, attrFile, tmpFile);
            if (!inOutClassFile.delete()) {
                throw new IOException("inOutClassFile.delete() failed");
            }
            if (!tmpFile.renameTo(inOutClassFile)) {
                throw new IOException("tmpFile.renameTo(inOutClassFile) failed");
            }
        }

        static void install(File classFile, byte[] smap) throws IOException {
            File tmpFile = new File(classFile.getPath() + "tmp");
            new SDEInstaller(classFile, smap, tmpFile);
            if (!classFile.delete()) {
                throw new IOException("classFile.delete() failed");
            }
            if (!tmpFile.renameTo(classFile)) {
                throw new IOException("tmpFile.renameTo(classFile) failed");
            }
        }

        static byte[] install(byte[] classfile, byte[] smap)
                throws IOException {
            SDEInstaller installer = new SDEInstaller(classfile, smap);
            byte[] tmp = new byte[installer.genPos];
            System.arraycopy(installer.gen, 0, tmp, 0, installer.genPos);
            return tmp;
        }

        SDEInstaller(byte[] classfile, byte[] sdeAttr) 
		throws IOException {
            orig = classfile;
            this.sdeAttr = sdeAttr;
            gen = new byte[orig.length + sdeAttr.length + 100];
            addSDE();
        }

        SDEInstaller(File inClassFile, byte[] sdeAttr, File outClassFile)
            throws IOException {
            if (!inClassFile.exists()) {
                throw new FileNotFoundException("no such file: " + inClassFile);
            }

            this.sdeAttr = sdeAttr;
            // get the bytes
            orig = readWhole(inClassFile);
            gen = new byte[orig.length + sdeAttr.length + 100];

            // do it
            addSDE();

            // write result
            FileOutputStream outStream = new FileOutputStream(outClassFile);
            outStream.write(gen, 0, genPos);
            outStream.close();
        }

        SDEInstaller(File inClassFile, File attrFile, File outClassFile)
            throws IOException {
            this(inClassFile, readWhole(attrFile), outClassFile);
        }

        static byte[] readWhole(File input) throws IOException {
            FileInputStream inStream = new FileInputStream(input);
            int len = (int)input.length();
            byte[] bytes = new byte[len];
            if (inStream.read(bytes, 0, len) != len) {
                throw new IOException("expected size: " + len);
            }
            inStream.close();
            return bytes;
        }

        void addSDE() throws UnsupportedEncodingException, IOException {
            int i;
            copy(4 + 2 + 2); // magic min/maj version
            int constantPoolCountPos = genPos;
            int constantPoolCount = readU2();
            writeU2(constantPoolCount);

            // copy old constant pool return index of SDE symbol, if found
            sdeIndex = copyConstantPool(constantPoolCount);
            if (sdeIndex < 0) {
                // if "SourceDebugExtension" symbol not there add it
                writeUtf8ForSDE();

                // increment the countantPoolCount
                sdeIndex = constantPoolCount;
                ++constantPoolCount;
                randomAccessWriteU2(constantPoolCountPos, constantPoolCount);
            }
            copy(2 + 2 + 2); // access, this, super
            int interfaceCount = readU2();
            writeU2(interfaceCount);
            copy(interfaceCount * 2);
            copyMembers(); // fields
            copyMembers(); // methods
            int attrCountPos = genPos;
            int attrCount = readU2();
            writeU2(attrCount);
            // copy the class attributes, return true if SDE attr found (not copied)
            if (!copyAttrs(attrCount)) {
                // we will be adding SDE and it isn't already counted
                ++attrCount;
                randomAccessWriteU2(attrCountPos, attrCount);
            }
            writeAttrForSDE(sdeIndex);
        }

        void copyMembers() {
            int count = readU2();
            writeU2(count);
            for (int i = 0; i < count; ++i) {
                copy(6); // access, name, descriptor
                int attrCount = readU2();
                writeU2(attrCount);
                copyAttrs(attrCount);
            }
        }

        boolean copyAttrs(int attrCount) {
            boolean sdeFound = false;
            for (int i = 0; i < attrCount; ++i) {
                int nameIndex = readU2();
                // don't write old SDE
                if (nameIndex == sdeIndex) {
                    sdeFound = true;
                } else {
                    writeU2(nameIndex); // name
                    int len = readU4();
                    writeU4(len);
                    copy(len);
                }
            }
            return sdeFound;
        }

        void writeAttrForSDE(int index) {
            writeU2(index);
            writeU4(sdeAttr.length);
            for (int i = 0; i < sdeAttr.length; ++i) {
                writeU1(sdeAttr[i]);
            }
        }

        void randomAccessWriteU2(int pos, int val) {
            int savePos = genPos;
            genPos = pos;
            writeU2(val);
            genPos = savePos;
        }

        int readU1() {
            return ((int)orig[origPos++]) & 0xFF;
        }

        int readU2() {
            int res = readU1();
            return (res << 8) + readU1();
        }

        int readU4() {
            int res = readU2();
            return (res << 16) + readU2();
        }

        void writeU1(int val) {
            gen[genPos++] = (byte)val;
        }

        void writeU2(int val) {
            writeU1(val >> 8);
            writeU1(val & 0xFF);
        }

        void writeU4(int val) {
            writeU2(val >> 16);
            writeU2(val & 0xFFFF);
        }

        void copy(int count) {
            for (int i = 0; i < count; ++i) {
                gen[genPos++] = orig[origPos++];
            }
        }

        byte[] readBytes(int count) {
            byte[] bytes = new byte[count];
            for (int i = 0; i < count; ++i) {
                bytes[i] = orig[origPos++];
            }
            return bytes;
        }

        void writeBytes(byte[] bytes) {
            for (int i = 0; i < bytes.length; ++i) {
                gen[genPos++] = bytes[i];
            }
        }

        int copyConstantPool(int constantPoolCount)
            throws UnsupportedEncodingException, IOException {
            int sdeIndex = -1;
            // copy const pool index zero not in class file
            for (int i = 1; i < constantPoolCount; ++i) {
                int tag = readU1();
                writeU1(tag);
                switch (tag) {
                    case 7 : // Class
                    case 8 : // String
                        copy(2);
                        break;
                    case 9 : // Field
                    case 10 : // Method
                    case 11 : // InterfaceMethod
                    case 3 : // Integer
                    case 4 : // Float
                    case 12 : // NameAndType
                        copy(4);
                        break;
                    case 5 : // Long
                    case 6 : // Double
                        copy(8);
                        i++;
                        break;
                    case 1 : // Utf8
                        int len = readU2();
                        writeU2(len);
                        byte[] utf8 = readBytes(len);
                        String str = new String(utf8, "UTF-8");
                        if (str.equals(nameSDE)) {
                            sdeIndex = i;
                        }
                        writeBytes(utf8);
                        break;
                    default :
                        throw new IOException("unexpected tag: " + tag);
                }
            }
            return sdeIndex;
        }

        void writeUtf8ForSDE() {
            int len = nameSDE.length();
            writeU1(1); // Utf8 tag
            writeU2(len);
            for (int i = 0; i < len; ++i) {
                writeU1(nameSDE.charAt(i));
            }
        }
    }

    private void evaluateNodes(
        Node.Nodes nodes,
        SmapStratum s,
        boolean breakAtLF) {
        try {
            nodes.visit(new SmapGenVisitor(s, breakAtLF, classInfos));
        } catch (JasperException ex) {
        }
    }

    static class SmapGenVisitor extends Node.Visitor {

        private SmapStratum smapStratum;
        private boolean breakAtLF;
        private List<ClassInfo> classInfos;

        SmapGenVisitor(SmapStratum s, boolean breakAtLF,
                       List<ClassInfo> classInfos) {
            this.smapStratum = s;
            this.breakAtLF = breakAtLF;
            this.classInfos = classInfos;
        }

        public void visitBody(Node n) throws JasperException {
            SmapStratum smapStratumSave = smapStratum;
            String innerClass = n.getInnerClassName();
            if (innerClass != null) {
                found: {
                    for (ClassInfo ci: classInfos) {
                        if (innerClass.equals(ci.getClassName())) {
                            smapStratum = ci.getSmapStratum();
                            break found;
                        }
                    }
                    smapStratum = new SmapStratum("JSP");
                    classInfos.add(new ClassInfo(innerClass, smapStratum));
                }
            }
            super.visitBody(n);
            smapStratum = smapStratumSave;
        }

        public void visit(Node.Declaration n) throws JasperException {
            doSmapText(n);
        }

        public void visit(Node.Expression n) throws JasperException {
            doSmapText(n);
        }

        public void visit(Node.Scriptlet n) throws JasperException {
            doSmapText(n);
        }

        public void visit(Node.IncludeAction n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.ForwardAction n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.GetProperty n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.SetProperty n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.UseBean n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.PlugIn n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.CustomTag n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.UninterpretedTag n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.JspElement n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.JspText n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.NamedAttribute n) throws JasperException {
            visitBody(n);
        }

        public void visit(Node.JspBody n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.InvokeAction n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.DoBodyAction n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.ELExpression n) throws JasperException {
            doSmap(n);
        }

        public void visit(Node.TemplateText n) throws JasperException {
            Mark mark = n.getStart();
            if (mark == null) {
                return;
            }

            //Add the file information
            String fileName = mark.getFile();
            smapStratum.addFile(unqualify(fileName), fileName);

            //Add a LineInfo that corresponds to the beginning of this node
            int iInputStartLine = mark.getLineNumber();
            int iOutputStartLine = n.getBeginJavaLine();
            int iOutputLineIncrement = breakAtLF? 1: 0;
            smapStratum.addLineData(iInputStartLine, fileName, 1,
                             iOutputStartLine, iOutputLineIncrement);

            // Output additional mappings in the text
            java.util.ArrayList extraSmap = n.getExtraSmap();

            if (extraSmap != null) {
                for (int i = 0; i < extraSmap.size(); i++) {
                    iOutputStartLine += iOutputLineIncrement;
                    smapStratum.addLineData(
                        iInputStartLine+((Integer)extraSmap.get(i)).intValue(),
                        fileName,
                        1,
                        iOutputStartLine,
                        iOutputLineIncrement);
                }
            }
        }

        private void doSmap(
            Node n,
            int inLineCount,
            int outIncrement,
            int skippedLines) {
            Mark mark = n.getStart();
            if (mark == null) {
                return;
            }

            String unqualifiedName = unqualify(mark.getFile());
            smapStratum.addFile(unqualifiedName, mark.getFile());
            smapStratum.addLineData(
                mark.getLineNumber() + skippedLines,
                mark.getFile(),
                inLineCount - skippedLines,
                n.getBeginJavaLine() + skippedLines,
                outIncrement);
        }

        private void doSmap(Node n) {
            doSmap(n, 1, n.getEndJavaLine() - n.getBeginJavaLine(), 0);
        }

        private void doSmapText(Node n) {
            String text = n.getText();
            int index = 0;
            int next = 0;
            int lineCount = 1;
            int skippedLines = 0;
            boolean slashStarSeen = false;
            boolean beginning = true;

            // Count lines inside text, but skipping comment lines at the
            // beginning of the text.
            while ((next = text.indexOf('\n', index)) > -1) {
                if (beginning) {
                    String line = text.substring(index, next).trim();
                    if (!slashStarSeen && line.startsWith("/*")) {
                        slashStarSeen = true;
                    }
                    if (slashStarSeen) {
                        skippedLines++;
                        int endIndex = line.indexOf("*/");
                        if (endIndex >= 0) {
                            // End of /* */ comment
                            slashStarSeen = false;
                            if (endIndex < line.length() - 2) {
                                // Some executable code after comment
                                skippedLines--;
                                beginning = false;
                            }
                        }
                    } else if (line.length() == 0 || line.startsWith("//")) {
                        skippedLines++;
                    } else {
                        beginning = false;
                    }
                }
                lineCount++;
                index = next + 1;
            }

            doSmap(n, lineCount, 1, skippedLines);
        }
    }

    /*
     * A place to keep some temporary info on a class.  More than a class
     * may be produced in a compilation, since it may generate inner classes
     */
    static public class ClassInfo {

        private String className;
        private String classFileName;
        private String smap;
        private SmapStratum smapStratum;

        public ClassInfo(String className, SmapStratum smapStratum) {
            this.className = className;
            this.smapStratum = smapStratum;
        }

        public String getClassName() {
            return className;
        }

        public SmapStratum getSmapStratum() {
            return smapStratum;
        }

        public String getClassFileName() {
            return classFileName;
        }

        public void setClassFileName(String classFileName) {
            this.classFileName = classFileName;
        }

        public String getSmap() {
            return smap;
        }

        public void setSmap(String smap) {
            this.smap = smap;
        }
    }

}
