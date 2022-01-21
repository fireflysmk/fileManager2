package com.geekbrains.model;

import lombok.Data;

@Data
public class FileDelete implements AbstractMessage {

    public String getFileName() {
        return fileName;
    }

    private final String fileName;

    public FileDelete(String file) {
        this.fileName = file;
    }

    @Override
    public MessageType getMessageType()    {
        return MessageType.FILE_DELETE;
    }
}
