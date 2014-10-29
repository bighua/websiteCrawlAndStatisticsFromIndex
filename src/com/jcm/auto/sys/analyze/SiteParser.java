package com.jcm.auto.sys.analyze;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jcm.auto.sys.beans.analyze.Site;

public class SiteParser {

	/**
	 * 程序运行时长（毫秒计）
	 */
	private long executeDelay = Long.MAX_VALUE;
	
    public void run() throws Exception {

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup(20);
        long start = System.currentTimeMillis();
        ExecutorService service = Executors.newCachedThreadPool();
        try {
            // 判断提交的任务是否都完成，或者网络原因不断重试导致超时
            while(ParserManager.hasTask() && ((System.currentTimeMillis() - start) < executeDelay)) {
            	Site s = ParserManager.getTask();
                // 任务在进行中，则可能队列中没有任务
                if (s != null) {
            		service.submit(new ParserTask(s, group));
                }
            }
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
            service.shutdown();
            
            ParserManager.log.info("未完成任务数 ==========" + ParserManager.uncompleteTaskCount());
            ParserManager.log.info("耗时 ==========" + (System.currentTimeMillis() - start));
        }
    }

    public static void main(String[] args) throws Exception {
        new SiteParser().run();
    }
}
