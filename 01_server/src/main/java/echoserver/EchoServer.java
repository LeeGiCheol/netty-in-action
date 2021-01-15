/*
    main 메서드는 서버를 bootstrap한다.
        - bootstrap을 줄이면 흔히 말하는 부팅/시동이다. (ex) 컴퓨터 부팅)

    부트스트랩
        - 서버를 부트스트랩하고 바인딩하는데 이용할 ServerBootstrap 인스턴스를 생성한다.
        - 새로운 연결 수락, 데이터 읽고/쓰기와 같은 이벤트 처리를 수행할 NioEventLoopGroup 인스턴스를 생성하고 할당한다.
        - 서버가 바인딩하는 로컬 InetSocketAddress를 지정한다.
        - EchoServerHandler 인스턴스를 이용해 새로운 각 Channel을 초기화한다.
        - ServerBootstrap.bind()를 호출해 서버를 바인딩한다.
 */

package echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage : " + EchoServer.class.getSimpleName() + " <port>");
        }

        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(serverHandler);
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
