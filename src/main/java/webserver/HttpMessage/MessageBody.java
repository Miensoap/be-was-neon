package webserver.HttpMessage;

import webserver.HttpMessage.constants.eums.FileType;
import webserver.HttpMessage.utils.MultiTypeParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static webserver.HttpMessage.constants.WebServerConst.*;

public class MessageBody {
    private final byte[] body;
    private final FileType contentType;

    /**
     * Key - Value 형식의 내용일 경우 HashMap 에 저장
     */
    private Map<String, String> content = new ConcurrentHashMap<>();

    private Map<FileType , String> multiContents;

    /**
     * @param body        HTTP Message body 문자열
     * @param contentType body 내용 유형
     */
    public MessageBody(String body, FileType contentType) {
        this.body = body.getBytes();
        this.contentType = contentType;

        if (contentType == FileType.URLENCODED) {
            parseUrlEncoded(body);
        }
    }

    public MessageBody(byte[] content , FileType contentType){
        this.body = content;
        this.contentType = contentType;
    }

    /**
     * 멀티파트 타입 Body 생정자
     * @param content
     * @param boundary
     * @throws IOException
     */
    public MessageBody(byte[] content , String boundary) throws IOException {
        this.body = content;
        this.contentType = FileType.MULTIPART;
        this.multiContents = new MultiTypeParser(content , boundary).getParsed();
    }

    /**
     * File Path 사용하는 생성자
     * @param file
     * @throws IOException
     */
    public MessageBody(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            body = new byte[(int) file.length()];
            fis.read(body);
        }

        String[] fileName = file.getName().split(EXTENDER_START);
        this.contentType = FileType.valueOf(fileName[fileName.length - 1].toUpperCase());
    }

    /**
     * key에 해당하는 Content value 반환
     * (ex : urlencoded)
     * @param key
     * @return value
     * @throws IllegalArgumentException
     */
    public String getContentByKey(String key) throws IllegalArgumentException {
        String value = content.get(key);

        if (value == null) throw new IllegalArgumentException("not exists key :" + key);
        return content.get(key);
    }

    /**
     * 멀티파트 타입 Body 에서 지정한 Content Type의 데이터를 반환
     * @param fileType
     * @return
     */
    public String getMultiContent(FileType fileType){
        return this.multiContents.get(fileType);
    }

    public long getContentLength() {
        return body.length;
    }

    public FileType getContentType() {
        return contentType;
    }

    public byte[] getBody() {
        return body;
    }

    private void parseUrlEncoded(String body) {
        StringTokenizer st = new StringTokenizer(URLDecoder.decode(body, UTF_8), QUERY_START + QUERY_DELIM);

        while (st.hasMoreTokens()) {
            String[] token = st.nextToken().split(QUERY_VALUE_DELIM);
            content.put(token[0], token[1]);
        }
    }
}
