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
package org.more.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Date;
/**
 * 文件拷贝类
 * @version 2009-4-29(08年开发，09年整理)
 * @author 赵永春 (zyc@byshell.org)
 */
public final class FileCopy {
    /**
     * Java原始方式拷贝文件，f1 拷贝到 f2。拷贝文件的缓冲区大小为2097152字节。
     * @param f1 原始文件
     * @param f2 目标文件
     * @param append 如果目标文件存在是追加还是覆盖
     * @return 返回执行时间。
     * @throws IOException 如果发生IO异常
     */
    public static long forJava(File f1, File f2, boolean append) throws IOException {
        long time = new Date().getTime();
        int length = 2097152;
        FileInputStream in = new FileInputStream(f1);
        FileOutputStream out = new FileOutputStream(f2, append);
        byte[] buffer = new byte[length];
        while (true) {
            int ins = in.read(buffer);
            if (ins == -1) {
                in.close();
                out.flush();
                out.close();
                return new Date().getTime() - time;
            } else
                out.write(buffer, 0, ins);
        }
    }
    /**
     * 使用JavaNIO管道对管道方式拷贝文件。拷贝文件的缓冲区大小为2097152字节。
     * @param f1 原始文件
     * @param f2 目标文件
     * @param append 如果目标文件存在是追加还是覆盖
     * @return 返回执行时间。
     * @throws IOException 如果发生IO异常
     */
    public static long forChannel(File f1, File f2, boolean append) throws IOException {
        long time = new Date().getTime();
        int length = 2097152;
        FileInputStream in = new FileInputStream(f1);
        FileOutputStream out = new FileOutputStream(f2, append);
        FileChannel inC = in.getChannel();
        FileChannel outC = out.getChannel();
        ByteBuffer b = null;
        while (true) {
            if (inC.position() == inC.size()) {
                inC.close();
                outC.close();
                return new Date().getTime() - time;
            }
            if ((inC.size() - inC.position()) < length) {
                length = (int) (inC.size() - inC.position());
            } else
                length = 2097152;
            b = ByteBuffer.allocateDirect(length);
            inC.read(b);
            b.flip();
            outC.write(b);
            outC.force(false);
        }
    }
    /**
     * 使用JavaNIO 文件内存映射方式拷贝文件。拷贝文件的缓冲区大小为2097152字节。
     * @param f1 原始文件
     * @param f2 目标文件
     * @return 返回执行时间。
     * @throws IOException 如果发生IO异常
     */
    public static long forImage(File f1, File f2) throws IOException {
        long time = new Date().getTime();
        int length = 2097152;
        FileInputStream in = new FileInputStream(f1);
        RandomAccessFile out = new RandomAccessFile(f2, "rw");
        FileChannel inC = in.getChannel();
        MappedByteBuffer outC = null;
        MappedByteBuffer inbuffer = null;
        byte[] b = new byte[length];
        while (true) {
            if (inC.position() == inC.size()) {
                inC.close();
                outC.force();
                out.close();
                return new Date().getTime() - time;
            }
            if ((inC.size() - inC.position()) < length) {
                length = (int) (inC.size() - inC.position());
            } else {
                length = 20971520;
            }
            b = new byte[length];
            inbuffer = inC.map(MapMode.READ_ONLY, inC.position(), length);
            inbuffer.load();
            inbuffer.get(b);
            outC = out.getChannel().map(MapMode.READ_WRITE, inC.position(), length);
            inC.position(b.length + inC.position());
            outC.put(b);
            outC.force();
        }
    }
    /**
     * 使用JavaNIO管道对管道传输方式拷贝文件。拷贝文件的缓冲区大小为2097152字节。
     * @param f1 原始文件
     * @param f2 目标文件
     * @param append 如果目标文件存在是追加还是覆盖
     * @return 返回执行时间。
     * @throws IOException 如果发生IO异常
     */
    public static long forTransfer(File f1, File f2, boolean append) throws IOException {
        long time = new Date().getTime();
        int length = 2097152;
        FileInputStream in = new FileInputStream(f1);
        FileOutputStream out = new FileOutputStream(f2, append);
        FileChannel inC = in.getChannel();
        FileChannel outC = out.getChannel();
        int i = 0;
        while (true) {
            if (inC.position() == inC.size()) {
                inC.close();
                outC.close();
                return new Date().getTime() - time;
            }
            if ((inC.size() - inC.position()) < 20971520)
                length = (int) (inC.size() - inC.position());
            else
                length = 20971520;
            inC.transferTo(inC.position(), length, outC);
            inC.position(inC.position() + length);
            i++;
        }
    }
}