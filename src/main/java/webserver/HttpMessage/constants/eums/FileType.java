package webserver.HttpMessage.constants.eums;

import java.util.Arrays;
import java.util.List;

public enum FileType {
    // text
    HTML("text/html; charset=utf-8"),
    CSS("text/css"),
    TXT("text/plain"),

    // image
    SVG("image/svg+xml"),
    PNG("image/png"),
    JPG("image/jpeg"),
    JPEG("image/jpeg"),
    GIF("image/gif"),
    ICO("image/x-icon"),


    NONE("none"),
    JS("Application/javascript"),
    URLENCODED("application/x-www-form-urlencoded"),
    MULTIPART("multipart/form-data");

    private final String mimeType;

    FileType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static FileType of(String contentType) {
        return Arrays.stream(FileType.values())
                .filter(t -> t.getMimeType().equalsIgnoreCase(contentType))
                .findAny().orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파일 타입"));
    }

//    public static boolean isImageType(FileType fileType){
//        return List.of(PNG , JPEG , JPG , SVG).contains(fileType);
//    }
    public static List<FileType> imageTypes( ){
        return List.of(PNG , JPEG , JPG , SVG, GIF);
    }
}
