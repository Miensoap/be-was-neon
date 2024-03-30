package application.handler.utils;

public abstract class HtmlConst {
    // 상단
    public static final String USERNAME = "<!--UserName-->";

    // 글
    public static final String WRITER = "writer_account";
    public static final String WELCOME = "../img/welcome.jpg";
    public static final String CONTENT = "<!--content-->";
    public static final String COMMENT = "<!--comment_block-->";


    // TODO . html 파일 직접 읽어서 사용
    public static final String COMMENT_BLOCK = """
            <li class="comment__item">
                <div class="comment__item__user">
                    <img class="comment__item__user__img"/>
                    <p class="comment__item__user__nickname">writer_account</p>
                </div>
                <p class="comment__item__article">
                    <!--content-->
                </p>
            </li>
            """;

    // nav
    public static final String NEXT = "next_article";
    public static final String PREV = "prev_article";


    // url
    public static final String ARTICLE_URL = "/main/article?index=";
    public static final String COMMENT_POST = "post_comment";
    public static final String COMMENT_URL = "/comment?index=";
}
