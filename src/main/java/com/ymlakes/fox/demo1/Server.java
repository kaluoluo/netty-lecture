package com.ymlakes.fox.demo1;

import com.ymlakes.fox.chatdemo.server.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

    public static void main(String[] args)throws Exception{
        EventLoopGroup pGroup = new NioEventLoopGroup();    //一个是用于处理服务器客户端连接的
        EventLoopGroup cGroup = new NioEventLoopGroup();    //一个是进行网络通信的
        ServerBootstrap b  = new ServerBootstrap();         //create an accessibility class,a range of configurations for server communication

        b.group(pGroup,cGroup)//绑定两个线程组
                .channel(NioServerSocketChannel.class)//指定nio模式
                .option(ChannelOption.SO_BACKLOG,1024)//指定tcp缓冲区大小
                .option(ChannelOption.SO_SNDBUF,32*1024)//指定发送缓冲区大小
                .option(ChannelOption.SO_RCVBUF,32*1024)//指定接受缓冲区大小
                .option(ChannelOption.SO_KEEPALIVE,true)//保持连接
                //.handler(new NettyServerHandler())
                //childHandler是针对cGroup的，handler是针对pGroup
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ServerHandler());//配置具体数据接受方法的处理
                    }
                });
        ChannelFuture channelFuture = b.bind(8765).sync();//进行绑定
        System.out.println("服务启动成功！");

        channelFuture.channel().closeFuture().sync();//等待关闭
        pGroup.shutdownGracefully();
        cGroup.shutdownGracefully();
    }


}
