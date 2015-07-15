package com.benq.Derek.Netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeServer {
	
	public static void main(String[] args) throws Exception {

		int port = 8080;
		if (args != null && args.length > 0) {

			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				// 取默认值8080
			}
		}
		new TimeServer().bind(port);
	}

	public void bind(int port)throws Exception{
		//配置服务器端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b= new ServerBootstrap();
			b.group(bossGroup,workerGroup)
			 .channel(NioServerSocketChannel.class)
			 .option(ChannelOption.SO_BACKLOG, 1024)
			 .childHandler(new ChildChannelhandler());
			//绑定端口，等待同步成功
			//sync()方法是同步阻塞的
			//綁定完成之後，Netty將會返回一個ChannelFuture，功能類似于java.util.concurrent.Future，主要用於異步操作的通知囘調
			ChannelFuture f = b.bind(port).sync();
			
			//等待服务器监听端口关闭
			//該方法也是阻塞方法，等待服务器的链路关闭之后，才退出main函数
			f.channel().closeFuture().sync();			
			
		} finally{
			//释放线程资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			
		}
	}
	/**
	 * I/O事件的處理類，類似Reactor模式中的Handler，主要用於處理網絡的I/O事件，比如記錄日誌，對消息進行解編碼等等
	 * @author Derek.P.Dai
	 *
	 */
	private class ChildChannelhandler extends ChannelInitializer<io.netty.channel.socket.SocketChannel>{
		
		@Override
		protected void initChannel(io.netty.channel.socket.SocketChannel ch)
				throws Exception {
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new TimeServerHandler());
		}
		
	}
	
}
