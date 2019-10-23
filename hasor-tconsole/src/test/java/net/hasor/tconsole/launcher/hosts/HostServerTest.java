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
package net.hasor.tconsole.launcher.hosts;
import net.hasor.tconsole.AbstractTelTest;
import net.hasor.test.beans.TestExecutor;
import net.hasor.utils.StringUtils;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class HostServerTest extends AbstractTelTest {
    private Thread createCopyThread(Writer writer) {
        LinkedList<String> preCommand = new LinkedList<String>() {{
            this.addAll(Arrays.asList("help", "test", "exit"));
        }};
        return new Thread(() -> {
            while (!preCommand.isEmpty()) {
                try {
                    Thread.sleep(1000);
                    String pop = preCommand.pop();
                    if (StringUtils.isNotBlank(pop)) {
                        writer.write(pop + "\n");
                    }
                } catch (Exception e) { /**/ }
            }
        });
    }

    private Thread createCopyThread2(PipedHostTelService service) {
        LinkedList<String> preCommand = new LinkedList<String>() {{
            this.addAll(Arrays.asList("help", "test", "exit"));
        }};
        return new Thread(() -> {
            while (!preCommand.isEmpty()) {
                try {
                    Thread.sleep(1000);
                    String pop = preCommand.pop();
                    if (StringUtils.isNotBlank(pop)) {
                        service.sendCommand(pop);
                    }
                } catch (Exception e) { /**/ }
            }
        });
    }

    @Test
    public void pip_host_test_1() throws IOException {
        // .创建服务
        StringWriter stringWriter = new StringWriter();
        PipedHostTelService pipedHostTelService = new PipedHostTelService(stringWriter);
        pipedHostTelService.addCommand("test", new TestExecutor());
        pipedHostTelService.init();
        //
        // .执行3条命令(每秒钟产生一条命令到 piped)
        Thread copyThread = this.createCopyThread2(pipedHostTelService);
        copyThread.start();
        // .等待exit退出
        pipedHostTelService.join(5, TimeUnit.SECONDS);
        //
        String string = stringWriter.toString();
        assert string.contains("use the 'exit' or 'quit' out of the console."); // 非静默模式，所以有欢迎信息
        assert string.contains(" - test  hello help.");                         // help 命令
        assert string.contains("{\"args\":\"\",\"name\":\"test\"}");            // test 命令
        assert string.contains("bye.");                                         // exit 命令
        assert !pipedHostTelService.isSilent();
    }

    @Test
    public void pip_host_test_2() throws IOException {
        // .创建服务
        StringWriter stringWriter = new StringWriter();
        PipedHostTelService pipedHostTelService = new PipedHostTelService(stringWriter);
        pipedHostTelService.addCommand("test", new TestExecutor());
        pipedHostTelService.init();
        //
        // .执行3条命令(每秒钟产生一条命令到 piped)
        Thread copyThread = this.createCopyThread(pipedHostTelService.getInDataWriter());
        copyThread.start();
        // .等待exit退出
        pipedHostTelService.join(5, TimeUnit.SECONDS);
        //
        String string = stringWriter.toString();
        assert string.contains("use the 'exit' or 'quit' out of the console."); // 非静默模式，所以有欢迎信息
        assert string.contains(" - test  hello help.");                         // help 命令
        assert string.contains("{\"args\":\"\",\"name\":\"test\"}");            // test 命令
        assert string.contains("bye.");                                         // exit 命令
        assert !pipedHostTelService.isSilent();
    }

    @Test
    public void pip_host_test_3() throws IOException {
        // .创建服务
        StringWriter stringWriter = new StringWriter();
        PipedHostTelService pipedHostTelService = new PipedHostTelService(stringWriter);
        pipedHostTelService.addCommand("test", new TestExecutor());
        pipedHostTelService.silent();
        pipedHostTelService.init();
        //
        // .执行3条命令(每秒钟产生一条命令到 piped)
        Thread copyThread = this.createCopyThread(pipedHostTelService.getInDataWriter());
        copyThread.start();
        // .等待exit退出
        pipedHostTelService.join(5, TimeUnit.SECONDS);
        //
        String string = stringWriter.toString();
        assert !string.contains("use the 'exit' or 'quit' out of the console.");// 静默模式下，不会有欢迎信息
        assert string.contains(" - test  hello help.");                         // help 命令
        assert string.contains("{\"args\":\"\",\"name\":\"test\"}");            // test 命令
        assert !string.contains("bye.");                                        // 静默模式下退出不会输出 bye
        assert pipedHostTelService.isSilent();
    }

    @Test
    public void pip_host_test_4() throws IOException {
        // .创建服务
        StringWriter stringWriter = new StringWriter();
        PipedHostTelService pipedHostTelService = new PipedHostTelService(stringWriter);
        pipedHostTelService.addCommand("test", new TestExecutor());
        pipedHostTelService.silent();
        pipedHostTelService.endcodeOfSilent("-abcdefg-");
        pipedHostTelService.init();
        //
        // .执行3条命令(每秒钟产生一条命令到 piped)
        Thread copyThread = this.createCopyThread(pipedHostTelService.getInDataWriter());
        copyThread.start();
        // .等待exit退出
        pipedHostTelService.join(5, TimeUnit.SECONDS);
        //
        String string = stringWriter.toString();
        String[] strings = string.split("-abcdefg-");
        assert strings.length == 3;                                     // 3条指令三个结果
        assert strings[0].contains(" - test  hello help.");             // help 命令
        assert strings[1].contains("{\"args\":\"\",\"name\":\"test\"}");// test 命令
        assert strings[2].trim().equals("");                            // 静默模式下退出不会输出 bye
        assert pipedHostTelService.isSilent();
        assert pipedHostTelService.endcodeOfSilent().equals("-abcdefg-");
    }

    @Test
    public void host_test_1() throws IOException {
        // .创建服务
        StringWriter stringWriter = new StringWriter();
        PipedWriter inDataWriter = new PipedWriter();
        HostTelService pipedHostTelService = new HostTelService(new PipedReader(inDataWriter), stringWriter);
        pipedHostTelService.addCommand("test", new TestExecutor());
        pipedHostTelService.init();
        //
        // .执行3条命令(每秒钟产生一条命令到 piped)
        Thread copyThread = this.createCopyThread(inDataWriter);
        copyThread.start();
        // .等待exit退出
        pipedHostTelService.join(5, TimeUnit.SECONDS);
        //
        String string = stringWriter.toString();
        assert string.contains("use the 'exit' or 'quit' out of the console."); // 非静默模式，所以有欢迎信息
        assert string.contains(" - test  hello help.");                         // help 命令
        assert string.contains("{\"args\":\"\",\"name\":\"test\"}");            // test 命令
        assert string.contains("bye.");                                         // exit 命令
        assert !pipedHostTelService.isSilent();
    }

    @Test
    public void coverage_test_1() throws IOException {
        PipedHostTelService pipedHostTelService = new PipedHostTelService(new StringWriter());
        pipedHostTelService.setAttribute("abc", "abc");
        assert pipedHostTelService.getAttributeNames().contains("abc");
        //
        try {
            pipedHostTelService.join();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("the container is not started yet.");
        }
    }

    @Test
    public void coverage_test_2() throws IOException {
        PipedHostTelService pipedHostTelService = new PipedHostTelService(new StringWriter());
        pipedHostTelService.setAttribute("abc", "abc");
        pipedHostTelService.init();
        //
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                pipedHostTelService.close();
            } catch (Exception e) { /**/ }
        }).start();
        //
        pipedHostTelService.join();
    }
}