package application.model;

public class Article {
    private String filePath;
    private final String content;
    private final String writer;
    private final int index;

    public Article(String content ,String writer, int index){
        this.content = content;
        this.writer = writer;
        this.index = index;
    }

    public Article(String content , String filePath, String writer, int index){
        this.filePath = filePath;
        this.content = content;
        this.writer = writer;
        this.index = index;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getContent() {
        return content;
    }

    public String getWriter() {
        return writer;
    }

    public int getIndex() {
        return this.index;
    }
}
