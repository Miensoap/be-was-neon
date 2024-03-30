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

    /**
     * 멀티파트 Body를 읽고 파싱해 Map에 저장
     * @param body 멀티파트 타입 body 전체
     * @param boundary
     * @throws IOException
     */
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

    /**
     * 멀티파트 Body 의 한 블럭을 읽어 본문을 FileType , byte[] 형태로 저장
     * @param content 한 블럭에 해당하는 바이트 배열
     * @throws IOException
     */
    private void readeOneBlock(byte[] content) throws IOException {
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

    /**
     * 멀티파트 타입 Body에 존재하는 모든 Boundary의 시작 인덱스를 반환
     * @param body 멀티파트 타입 Body 전체
     * @param boundary
     * @return
     */
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
