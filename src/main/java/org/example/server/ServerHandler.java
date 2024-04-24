package org.example.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// @Sharable означает, что можно зарегистрироваться
// и поделиться обработчиком с несколькими клиентами.
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    // Список подключенных клиентских каналов.
    static final List<Channel> channels = new ArrayList<>();

    // Всякий раз, когда клиент подключается к серверу через канал,
    // добавляем его канал в список каналов.
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("User has joined - " + ctx);
        channels.add(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        String time = DATE_FORMAT.format(new Date());
        System.out.println("(" + time + ") " + "Message received: " + msg);
        for (Channel c : channels) {
            c.writeAndFlush("(" + time + ") " + msg + '\n');
        }
    }

    // В случае исключения закрываем канал.
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Closing connection for user - " + ctx);
        ctx.close();
    }
}
