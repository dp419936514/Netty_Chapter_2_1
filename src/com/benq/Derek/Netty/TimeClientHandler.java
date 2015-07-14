package com.benq.Derek.Netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Logger;

public class TimeClientHandler extends ChannelHandlerAdapter {

	private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());
	
	private final ByteBuf firstMessage;
	
	/*
	 * create a Client-side handler
	 */
	public TimeClientHandler(){
		byte[] req = "QUERY TIME ORDER".getBytes();
		firstMessage = Unpooled.buffer(req.length);
		firstMessage.writeBytes(req);
	}
	
	/**
	 * 当TCP链路建立之后，NIO线程就会调用channelActive方法，
	 * 通过ChannelHandlerContext的writeAndFlush()方法将请求消息发送到服务器
	 */
	public void channelActive(ChannelHandlerContext ctx,Object msg) {
		System.out.println("Client Channel Active");
		ctx.writeAndFlush(firstMessage);
	}

	
	
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client Channel Registered");
		
		ctx.writeAndFlush(firstMessage);
		super.channelRegistered(ctx);
	}

	/***
	 * 服务器返回消息是，channelRead方法被调用
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req,"UTF-8");
		System.out.println("Now is : "+ body);
	}
	/***
	 * 服务器发生异常的时候，记录一异常，并释放资源
	 */
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		//释放资源
		logger.warning("Unexpected exception from downDtream : "+cause.getMessage());
		ctx.close();
	}


}
