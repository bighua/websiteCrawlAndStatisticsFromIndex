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
package com.jcm.auto.sys.analyze;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;

import java.nio.charset.Charset;

import com.jcm.auto.sys.analyze.parser.AbstractParser;
import com.jcm.auto.sys.beans.analyze.Site;

public class SiteParserHandler extends SimpleChannelInboundHandler<HttpObject> {

	// handler是线程安全的
	private final Site site;
//	private ByteBuf contentBuf = null;
	private final StringBuffer sb = new StringBuffer();
	private boolean taskCompleted = false;
	
	public SiteParserHandler (Site site) {
		this.site = site;
	}
    @Override
    public void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;

            System.out.println("STATUS: " + response.getStatus());
            System.out.println("VERSION: " + response.getProtocolVersion());
            System.out.println();

            if (!response.headers().isEmpty()) {
                for (String name: response.headers().names()) {
                    for (String value: response.headers().getAll(name)) {
                        System.out.println("HEADER: " + name + " = " + value);
                    }
                }
                System.out.println();
            }

            if (HttpHeaders.isTransferEncodingChunked(response)) {
                System.out.println("CHUNKED CONTENT {");
            } else {
                System.out.println("CONTENT {");
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            
            sb.append(content.content().toString(Charset.forName(site.getContentEncoding())));
//            ByteBuf chunk = content.content();
//        	ByteBuf tmp = Unpooled.buffer(chunk.writerIndex());
//            if (chunk.isDirect()) {
//            	chunk.readBytes(tmp);
//            } else {
//            	tmp = chunk;
//            }
//            if (contentBuf == null) {
//            	contentBuf = tmp;
//            } else {
//            	contentBuf = Unpooled.wrappedBuffer(contentBuf, tmp);
//            }

            if (content instanceof LastHttpContent) {
//            	ctx.channel().closeFuture();
//            	System.out.println(site.getParseSite());
            	AbstractParser parser = ParserManager.getParser(site.getParser());
            	if (parser != null) {
            		parser.parseAndSave(site, sb.toString());
//            		parser.parseHtml(site, contentBuf.toString(Charset.forName(site.getContentEncoding())));
            	}
            	// 在parse中已经提交了新的任务，本任务结束
            	ParserManager.taskComplete();
            	// 解析完成后的异常发生，不再调用taskComplete
            	taskCompleted = true;
            	ParserManager.log.info(String.format("【%s】的任务完成！【%s】", site.getParseSite(), site.getName()));
//                System.out.println("} END OF CONTENT");
            }
        }
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        ParserManager.log.error(String.format("从【%s】读取数据时发生异常!", site.getParseSite()), cause);
        // 解析完成前，异常结束的任务标注为完成，
        if (!taskCompleted) {
        	// 先添加任务，再结束本次异常任务，顺序不能颠倒，防止main线程hastask=0而终止
        	ParserManager.addTask(site);
        	ParserManager.taskComplete();
        }
    }
}
