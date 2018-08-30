/*
 * Copyright (c) 2001-2008 Caucho Technology, Inc.  All rights reserved.
 *
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Caucho Technology (http://www.caucho.com/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Burlap", "Resin", and "Caucho" must not be used to
 *    endorse or promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    info@caucho.com.
 *
 * 5. Products derived from this software may not be called "Resin"
 *    nor may "Resin" appear in their names without prior written
 *    permission of Caucho Technology.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL CAUCHO TECHNOLOGY OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Scott Ferguson
 */
package net.hasor.rsf.libs.com.caucho.hessian.io;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
/**
 * Input stream for Hessian 2 streaming requests using WebSocket.
 */
public class Hessian2StreamingInput {
    private static final Logger log = LoggerFactory.getLogger(Hessian2StreamingInput.class);
    private StreamingInputStream _is;
    private Hessian2Input        _in;
    /**
     * Creates a new Hessian input stream, initialized with an
     * underlying input stream.
     *
     * @param is the underlying output stream.
     */
    public Hessian2StreamingInput(InputStream is) {
        _is = new StreamingInputStream(is);
        _in = new Hessian2Input(_is);
    }
    public void setSerializerFactory(SerializerFactory factory) {
        _in.setSerializerFactory(factory);
    }
    public boolean isDataAvailable() {
        StreamingInputStream is = _is;
        return is != null && is.isDataAvailable();
    }
    public Hessian2Input startPacket() throws IOException {
        if (_is.startPacket()) {
            _in.resetReferences();
            _in.resetBuffer(); // XXX:
            return _in;
        } else
            return null;
    }
    public void endPacket() throws IOException {
        _is.endPacket();
        _in.resetBuffer(); // XXX:
    }
    public Hessian2Input getHessianInput() {
        return _in;
    }
    /**
     * Read the next object
     */
    public Object readObject() throws IOException {
        _is.startPacket();
        Object obj = _in.readStreamingObject();
        _is.endPacket();
        return obj;
    }
    /**
     * Close the output.
     */
    public void close() throws IOException {
        _in.close();
    }
    static class StreamingInputStream extends InputStream {
        private InputStream _is;
        private int         _length;
        private boolean     _isPacketEnd;
        StreamingInputStream(InputStream is) {
            _is = is;
        }
        public boolean isDataAvailable() {
            try {
                return _is != null && _is.available() > 0;
            } catch (IOException e) {
                log.debug(e.toString(), e);
                return true;
            }
        }
        public boolean startPacket() throws IOException {
            // skip zero-length packets
            do {
                _isPacketEnd = false;
            } while ((_length = readChunkLength(_is)) == 0);
            return _length > 0;
        }
        public void endPacket() throws IOException {
            while (!_isPacketEnd) {
                if (_length <= 0)
                    _length = readChunkLength(_is);
                if (_length > 0)
                    _is.skip(_length);
            }
        }
        public int read() throws IOException {
            if (_isPacketEnd)
                throw new IllegalStateException();
            InputStream is = _is;
            if (_length == 0) {
                _length = readChunkLength(is);
                if (_length <= 0)
                    return -1;
            }
            _length--;
            return is.read();
        }
        public int read(byte[] buffer, int offset, int length) throws IOException {
            if (_isPacketEnd)
                throw new IllegalStateException();
            InputStream is = _is;
            if (_length <= 0) {
                _length = readChunkLength(is);
                if (_length <= 0)
                    return -1;
            }
            int sublen = _length;
            if (length < sublen)
                sublen = length;
            sublen = is.read(buffer, offset, sublen);
            if (sublen < 0)
                return -1;
            _length -= sublen;
            return sublen;
        }
        private int readChunkLength(InputStream is) throws IOException {
            if (_isPacketEnd)
                return -1;
            int length = 0;
            int code = is.read();
            if (code < 0) {
                _isPacketEnd = true;
                return -1;
            } else if ((code & 0x80) != 0x80) {
                int len = 256;
                StringBuilder sb = new StringBuilder();
                int ch;
                while ((len-- > 0 && is.available() > 0 && (ch = is.read()) >= 0))
                    sb.append((char) ch);
                throw new IllegalStateException("WebSocket binary must begin with a 0x80 packet at 0x" + Integer.toHexString(code) + " (" + (char) code + ")" + " context[" + sb + "]");
            }
            while ((code = is.read()) >= 0) {
                length = (length << 7) + (code & 0x7f);
                if ((code & 0x80) == 0) {
                    if (length == 0)
                        _isPacketEnd = true;
                    return length;
                }
            }
            _isPacketEnd = true;
            return -1;
        }
    }
}
