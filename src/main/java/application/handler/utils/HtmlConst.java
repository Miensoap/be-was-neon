package application.handler.utils;

public abstract class HtmlConst {
    // 상단
    public static final String USERNAME = "<!--UserName-->";

    // 글
    public static final String WRITER = "writer_account";
    public static final String WELCOME = "../img/welcome.jpg";
    public static final String CONTENT = "<!--content-->";
    public static final String COMMENT = "<!--comment_block-->";
    public static final String ARTICLE_PAGE_PATH = "article/index.html";

    // nav
    public static final String NAV_HTML_PATH = "article/article_nav.html";
    public static final String NEXT = "next_article";
    public static final String PREV = "prev_article";
    public static final String NAV = "<!--article_nav-->";

    // comment
    public static final String COMMENT_BLOCK_PATH = "comment/comment_block.html";
    public static final String COMMENT_PAGE_PATH = "comment/index.html";


    // url
    public static final String ARTICLE_URL = "/main/article?index=";
    public static final String COMMENT_POST = "post_comment";
    public static final String COMMENT_URL = "/comment?index=";
}
