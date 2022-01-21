package com.geekbrains.netty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.geekbrains.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractMessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private Path serverPath;

    public AbstractMessageHandler() {

        try {
            Path defaultPath = Paths.get(System.getProperty("user.dir")).resolve("storage").resolve("ServerCommonStorage");
            this.serverPath = defaultPath;

            if (!Files.exists(defaultPath)) {
                log.debug("trying create Folder: " +  defaultPath);
                Files.createDirectory(defaultPath);
            } else {
                log.debug("folder already exists");
            }

            this.serverPath = defaultPath;

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new FilesList(serverPath));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                AbstractMessage message) throws Exception {
        log.debug("received: {}", message);
        switch (message.getMessageType()) {
            case FILE_REQUEST:
                FileRequest req = (FileRequest) message;
                ctx.writeAndFlush(
                        new FileMessage(serverPath.resolve(req.getFileName()))
                );
                break;
            case FILE:
                FileMessage fileMessage = (FileMessage) message;
                Files.write(
                        serverPath.resolve(fileMessage.getFileName()),
                        fileMessage.getBytes()
                );
                ctx.writeAndFlush(new FilesList(serverPath));
                break;
            case FILE_DELETE:
                FileDelete reqDel = (FileDelete) message;
                System.out.println("SERVER: " +  "  delete file: "+ reqDel.getFileName());

                Path serverFilePath = serverPath.resolve(reqDel.getFileName());
                Files.delete(Paths.get(String.valueOf(serverFilePath)));
                ctx.writeAndFlush(new FilesList(serverPath));
                break;
        }
    }
}
