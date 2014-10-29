package com.jcm.auto.sys.analyze;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import javax.net.ssl.SSLEngine;

import com.jcm.auto.sys.analyze.ssl.SslContextFactory;
import com.jcm.auto.sys.beans.analyze.Site;

public class SiteParserInitializer extends ChannelInitializer<SocketChannel> {

    private final Site site;

    public SiteParserInitializer(Site site) {
        this.site = site;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline p = ch.pipeline();

//        p.addLast("log", new LoggingHandler(LogLevel.INFO));
        // Enable HTTPS if necessary.
        if (site.isSsl()) {
            SSLEngine engine =
                SslContextFactory.getClientContext().createSSLEngine();
            engine.setUseClientMode(true);

            p.addLast("ssl", new SslHandler(engine));
        }

        p.addLast("codec", new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
        p.addLast("inflater", new HttpContentDecompressor());

        // Uncomment the following line if you don't want to handle HttpChunks.
//        p.addLast("aggregator", new HttpObjectAggregator(1048576));
        // 加上读取超时设置，可以迅速重新连接
        p.addLast("readtimeout", new ReadTimeoutHandler(30));

        p.addLast("handler", new SiteParserHandler(site));
    }
}
