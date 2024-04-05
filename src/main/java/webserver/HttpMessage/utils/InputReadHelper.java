package webserver.HttpMessage.utils;

import webserver.HttpMessage.MessageBody;
import webserver.HttpMessage.MessageHeader;
import webserver.HttpMessage.Request;
import webserver.HttpMessage.constants.eums.FileType;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import static webserver.HttpMessage.constants.WebServerConst.*;

public class InputReadHelper {

    /**
     * BufferedInputStream 에서 readLine 사용
     * CR , LF 전까지의 문자열을 반환
     * @param in BufferedInputStream
     * @return 한 Line 문자열
     * @throws IOException
     */
    public static String biReadLine(BufferedInputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();

        int previousByte = -1;
        int currentByte;
        while ((currentByte = in.read()) != -1) {
            if (previousByte == '\r' && currentByte == '\n') {
                break;
            }

            sb.append((char) currentByte);
            previousByte = currentByte;
        }
        return sb.substring(0, sb.length() - 1); // \r 삭제
    }


    /**
     * HTTP 요청 메시지를 Request 객체로 변환
     * @param in Socket InputStream
     * @return
     * @throws IOException
     */
    public static Request readRequestMessage(InputStream in) throws IOException {
        BufferedInputStream bi = new BufferedInputStream(in);

        // start -line
        String startLine = biReadLine(bi);

        if (startLine.isEmpty()) throw new IOException("Empty request message");
        Request request = new Request(startLine);

        //header
        MessageHeader.HeaderBuilder builder = MessageHeader.builder();
        String reqLine;
        while (!(reqLine = biReadLine(bi)).isEmpty()) {
            String[] headerField = reqLine.split(HEADER_DELIM);

            builder.field(headerField[0], headerField[1]);
        }
        request.header(builder.build());

        // body
        byte[] body;
        if (request.getHeader().hasContent()) {
            int contentLength = Integer.parseInt(request.getHeaderValue(CONTENT_LEN));
            body = new byte[contentLength];
            readBody(bi, body, contentLength);

            String[] contentType = request.getHeaderValue(CONTENT_TYPE).split(";");
            FileType fileType = FileType.of(contentType[0]);

            if (fileType.equals(FileType.MULTIPART)) {
                request.body(new MessageBody(body, contentType[1].split("=")[1])); // MultiPart 용 생성자
            } else {
                request.body(new MessageBody(new String(body), fileType));
            }
        }

        return request;
    }

    /**
     * BuffedInputStream 을 1024 바이트 단위로 나누어 읽고 , 읽은 전체를 반환
     * @param bi
     * @param body
     * @param contentLength 읽을 내용의 바이트 기준 길이
     * @throws IOException
     */
    private static void readBody(BufferedInputStream bi, byte[] body, int contentLength) throws IOException {
        int bytesRead = 0;
        int offset = 0;
        int bufferSize = 1024;
        while (bytesRead < contentLength) {
            int bytesToRead = Math.min(bufferSize, contentLength - bytesRead);
            int bytesReadThisRound = bi.read(body, offset, bytesToRead);
            if (bytesReadThisRound == -1) {
                break;
            }
            offset += bytesReadThisRound;
            bytesRead += bytesReadThisRound;
        }
    }
}
