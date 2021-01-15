/*
    - 클라이언트를 초기화하기 위한 Bootstrap 인스턴스를 생성한다.
    - 새로운 연결을 생성하고 인바운드와 아웃바운드 데이터를 처리하는 것을 포함하는
       이벤트 처리를 제어할 NioEventLoopGroup 인스턴스를 만들고 할당한다.
    - 서버로 연결하기 위한 InetSocketAddress를 생성한다.
    - 연결이 만들어지면 파이프라인에 EchoFClientHandler 하나를 추가한다.
    - 모든 준비가 완료되면 Bootstrap.connect() 를 호출 해 원격 서버로 연결한다.

    서버와 클라이언트는 각기 다른 전송을 이용해도 된다.
    예를들어 서버는 NIO 전송을, 클라이언트는 OIO 전송을 이용할 수 있다.


 */

package echoclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 부트스트랩 생성
            Bootstrap bootstrap = new Bootstrap();

            // 클라이언트 이벤트를 처리할 EventLoopGroup 지정
            bootstrap.group(group)
                    // NIO 전송 유형 중 하나를 지정
                    .channel(NioSocketChannel.class)
                    // 서버의 InetSocketAddress를 설정
                    .remoteAddress(new InetSocketAddress(host, port))
                    // 채널이 생성될 때 파이프라인에 EchoClientHandler 하나를 추가
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });

            // 원격 피어로 연결하고 연결이 완료되기를 기다림
            ChannelFuture channelFuture = bootstrap.connect().sync();
            // 채널이 닫힐 때까지 블로킹
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 스레드 풀을 종료하고 모든 리소스를 해제
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.err.println("Usage : " + EchoClient.class.getSimpleName() + " <host><port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        new EchoClient(host, port).start();
    }
}
