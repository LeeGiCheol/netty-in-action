/*
    클라이언트에서는 SimpleChannelInboundHandler를 이용했다.
    SimpleChannelInboundHandler와 ChannelInboundHandler의 차이는 무엇일까

    비즈니스 논리가 메시지를 처리하는 방법과 네티가 리소스를 관리하는 방법 두 요소 간 상호작용과 관계가 있다.

    즉 클라이언트에서 channelRead0 메서드가 완료된 시점에는
    들어오는 메시지가 이미 확보됐고, 이용이 끝난 상태이다.
    따라서 메서드가 반환될때 SimpleChannelInboundHandler는 메시지가 들어있는 ByteBuf에 대한 메모리 참조를 해제한다.

    그에비해 EchoServerHandler에서는 아직 들어오는 메시지를 발신자에게 출력해야 하며,
    channelRead 메서드가 반환될 때까지 비동기식인 write 메서드 작업이 완료되지 않았을 수도 있다.
    메시지는 channelReadComplete 메서드에서 writeAndFlush 메서드가 호출될 때 해제된다.

    그렇기 때문에 EchoServer에선 이 시점까지 메시지를 해제하지 않는 ChannelInboundHandlerAdapter를 확장한다.

 */

package echoclient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

@Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 채널 활성화 알림을 받으면 메시지를 전송한다.
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        // 수신한 메시지의 덤프를 로깅
        System.out.println("Client received: " + byteBuf.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
