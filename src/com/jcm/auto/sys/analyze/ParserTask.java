package com.jcm.auto.sys.analyze;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URI;

import com.jcm.auto.sys.beans.analyze.Site;

public class ParserTask implements Runnable {

    private final Site s;
    private final EventLoopGroup group;
    
    private int restartTimes = 0;
    
    public ParserTask(Site s, EventLoopGroup group) {
        this.s = s;
        this.group = group;
    }
    
    public void run() {
    	// uri is not correct.
    	if (s.getHost() == null) return;
        Bootstrap b = new Bootstrap();
        b.group(group)
         .channel(NioSocketChannel.class)
         .handler(new SiteParserInitializer(s));

        // Make the connection attempt.
        Channel ch;
		try {
			ch = b.connect(s.getHost(), s.getPort()).sync().channel();

	        // Prepare the HTTP request.
	        URI path = new URI(s.getParseSite());
	        String uri = path.getRawPath();
	        if (path.getRawQuery() != null) uri += "?" + path.getRawQuery();
	        HttpRequest request = new DefaultFullHttpRequest(
	                HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
	        request.headers().set(HttpHeaders.Names.HOST, s.getHost());
	        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
	        request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
	
	        // Send the HTTP request.
	        ch.writeAndFlush(request);
	        // Wait for the server to close the connection.
	        ch.closeFuture().sync();
		} catch (Exception e) {
			ParserManager.log.warn(String.format("发送到【%s】的连接请求出现异常，第%s次重新请求。", s.getParseSite(), ++restartTimes), e);
			// 由于网络连接稳定性不可靠，重试次数是否必要？
//			if (restartTimes < 10) {
			// 重新添加到任务队列
			ParserManager.addTask(s);
			// 异常任务结束
			ParserManager.taskComplete();
    	}
    }
}
