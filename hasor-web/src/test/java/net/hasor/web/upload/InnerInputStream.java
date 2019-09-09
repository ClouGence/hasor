package net.hasor.web.upload;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

class InnerInputStream extends ServletInputStream {
    private InputStream inputStream;

    public InnerInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public boolean isFinished() {
        try {
            return inputStream.available() != 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }
}