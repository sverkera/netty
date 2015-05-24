/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.rtsp;

import static io.netty.handler.codec.http.HttpConstants.CR;
import static io.netty.handler.codec.http.HttpConstants.LF;
import static io.netty.handler.codec.http.HttpConstants.SP;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectEncoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

/**
 * Encodes an RTSP message represented in {@link FullHttpMessage} into
 * a {@link ByteBuf}.
 */
@Sharable
public class RtspEncoder extends HttpObjectEncoder<HttpMessage> {
    private static final byte[] CRLF = { CR, LF };

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return super.acceptOutboundMessage(msg) && ((msg instanceof HttpRequest) || (msg instanceof HttpResponse));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void encodeInitialLine(ByteBuf buf, HttpMessage message) throws Exception {
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) message;
            HttpHeaders.encodeAscii(request.method().toString(), buf);
            buf.writeByte(SP);
            buf.writeBytes(request.uri().getBytes(CharsetUtil.UTF_8));
            buf.writeByte(SP);
            HttpHeaders.encodeAscii(request.protocolVersion().toString(), buf);
            buf.writeBytes(CRLF);
        } else if (message instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) message;
            HttpHeaders.encodeAscii(response.protocolVersion().toString(), buf);
            buf.writeByte(SP);
            buf.writeBytes(String.valueOf(response.status().code()).getBytes(CharsetUtil.US_ASCII));
            buf.writeByte(SP);
            HttpHeaders.encodeAscii(String.valueOf(response.status().reasonPhrase()), buf);
            buf.writeBytes(CRLF);
        } else {
            throw new Exception("Unsupported type " + message.getClass().getName());
        }
    }
}
