package webserver.HttpMessage.utils;

import webserver.HttpMessage.constants.WebServerConst;
import webserver.HttpMessage.constants.eums.FileType;

import java.io.*;
import java.util.*;

import static webserver.HttpMessage.constants.WebServerConst.HEADER_DELIM;
import static webserver.HttpMessage.utils.InputReadHelper.biReadLine;
import static webserver.WebServer.staticSourcePath;

public class MultiTypeParser {
    Map<FileType, String> contents = new HashMap<>();

    public MultiTypeParser(byte[] body, String boundary) throws IOException {
        byte[] boundaryBytes = ("--" + boundary).getBytes();
        List<Integer> boundaryIndex = findAllBoundary(body, boundaryBytes);

        byte[] currentPart;
        for (int i = 0; i < boundaryIndex.size() - 1; i++) {
            int startOfContent = boundaryIndex.get(i) + boundaryBytes.length; // index Of CR
            int endOfContent = boundaryIndex.get(i + 1);

            currentPart = Arrays.copyOfRange(body, startOfContent, endOfContent);
            readeOneBlock(currentPart);
        }
    }

    public Map<FileType, String> getParsed() {
        return contents;
    }

    private void readeOneBlock(byte[] content) throws IOException {
        if(Arrays.equals(content , "--".getBytes())) return; // End of Body
        if(content.length == 0) return;

        BufferedInputStream bis;
        FileType fileType;
        bis = new BufferedInputStream(new ByteArrayInputStream(content));

        biReadLine(bis); // CRLF
        String disposition = biReadLine(bis); // Content-Disposition
        String contentType = biReadLine(bis); // Content-Type

        if (contentType.equals("")) {
            fileType = FileType.TXT;
        } else {
            fileType = FileType.of(contentType.split(HEADER_DELIM)[1]);
             biReadLine(bis); // CRLF
        }

        // Content data
        byte[] contentData = new byte[bis.available()];
        bis.read(contentData);
        contents.put(fileType, Base64.getEncoder().encodeToString(Arrays.copyOfRange(contentData, 0, contentData.length - 2)));
    }

    private List<Integer> findAllBoundary(byte[] body, byte[] boundary) {
        List<Integer> result = new ArrayList<>();

        int  boundaryLength= boundary.length;
        int currentIndex = 0;
        byte[] currentPart;
        while (currentIndex+boundaryLength < body.length) {
            currentPart = Arrays.copyOfRange(body, currentIndex, currentIndex+boundaryLength);
            if (Arrays.equals(currentPart, boundary)) {
                result.add(currentIndex);
                currentIndex += boundaryLength+2;
                continue;
            }
            currentIndex += 1;
        }
        return result;
    }
}
