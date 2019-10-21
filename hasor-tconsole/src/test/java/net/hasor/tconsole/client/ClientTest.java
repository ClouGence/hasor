package net.hasor.tconsole.client;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.hasor.tconsole.TelAttribute;
import net.hasor.tconsole.launcher.telnet.TellnetTelService;
import net.hasor.utils.future.BasicFuture;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.hasor.tconsole.TelOptions.ENDCODE_OF_SILENT;
import static net.hasor.tconsole.TelOptions.SILENT;

public class ClientTest {
    @Test
    public void coverage_test_1() {
        ChannelHandlerContext context = PowerMockito.mock(ChannelHandlerContext.class);
        TelAttribute telAttribute = PowerMockito.mock(TelAttribute.class);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        TelClientEventListener closeFuture = () -> atomicBoolean.set(true);
        BasicFuture<Object> future = new BasicFuture<>();
        TelClientHandler handler = new TelClientHandler(telAttribute, future, closeFuture, PowerMockito.mock(ByteBuf.class));
        //
        Exception exception = new Exception("sss");
        handler.exceptionCaught(context, exception);
        assert atomicBoolean.get();
        try {
            future.get();
            assert false;
        } catch (Exception e) {
            assert e.getCause() == exception;
        }
    }

    @Test
    public void attribute_test_1() throws UnknownHostException {
        //
        try (TellnetTelService server = new TellnetTelService("127.0.0.1", 8082, s -> true)) {
            server.init();
            //
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.setAttribute("abc", "cba");
            client.init();
            //
            assert "cba".equals(client.sendCommand("get abc"));
            client.close();
        }
    }

    @Test
    public void attribute_test_2() {
        //
        TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
        try {
            client.setAttribute(SILENT, "cba");
            assert false;
        } catch (Exception e) {
            assert "the client does not support set SILENT attribute.".equals(e.getMessage());
        }
        //
        try {
            client.setAttribute(ENDCODE_OF_SILENT, "cba");
            assert false;
        } catch (Exception e) {
            assert "the client does not support set ENDCODE_OF_SILENT attribute.".equals(e.getMessage());
        }
        //
        client.setAttribute("abc", "cba");
        assert true;
        //
        //
        try {
            client.sendCommand("abc");
            assert false;
        } catch (Exception e) {
            assert "the Container has been inited.".equals(e.getMessage());
        }
    }
}