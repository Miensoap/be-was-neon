package webserver.HttpHandler.utils;

import webserver.WebServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public interface FileReadable {
    default byte[] readFileToBytes(String path){
        File file = new File(WebServer.staticSourcePath + path);
        byte[] bytes;
        try (FileInputStream fis = new FileInputStream(file)){
            bytes = new byte[(int) file.length()];
            fis.read(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bytes;
    }

    default String readFileToString(String path){
        return new String(readFileToBytes(path));
    }
}
