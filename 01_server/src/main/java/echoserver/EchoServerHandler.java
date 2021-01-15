/*
    Echo 서버는 들어오는 메시지에 반응해야 하므로 인바운드 이벤트에 반응하는 메서드가 정의된
    ChannelInboundHandlerAdapter의 하위 클래스를 만들어야 한다.
 */

package echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@Sharable   // ChannelHandler를 여러 채널 간 안전하게 공유할 수 있음을 나타낸다.
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));

        ctx.write(in);  // outbound 메시지를 flush 하지 않고 받은 메시지를 발신자로 출력한다.
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
            .addListener(ChannelFutureListener.CLOSE);      // 대기 중인 메시지를 원격 피어로 flush 하고 채널을 닫는다
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
