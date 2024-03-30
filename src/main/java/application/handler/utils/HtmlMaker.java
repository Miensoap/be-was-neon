package application.handler.utils;

import application.model.Article;
import application.model.Comment;
import application.model.User;

import java.util.List;
import java.util.StringJoiner;

import static application.handler.utils.HtmlConst.*;
import static webserver.HttpMessage.constants.WebServerConst.CRLF;

public class HtmlMaker {
    public static String getArticlePage(Article article , String template, List<Comment> comments){
        int index = article.index();
        String nextPath = ARTICLE_URL + (index + 1);
        String prevPath = ARTICLE_URL + (index - 1);

        return template
                .replace(WELCOME , article.filePath())
                .replace(WRITER , article.writer())
                .replace(CONTENT , article.content())
                .replace(NEXT, nextPath)
                .replace(PREV, prevPath)
                .replace(COMMENT , makeCommentBlock(comments))
                .replace("/comment", COMMENT_URL+ article.index());
    }

    public static String getCommentPage(String template , int articleIndex){
        return template.replace(COMMENT_POST , COMMENT_URL+articleIndex);
    }

    private static String makeCommentBlock(List<Comment> comments){
        StringJoiner sj = new StringJoiner(CRLF);

        comments.forEach(comment -> {
            sj.add(COMMENT_BLOCK
                .replace(WRITER , comment.writer())
                .replace(CONTENT, comment.content())
            );
        });

        return sj.toString();
    }


    public static String getListHtml(List<User> users){
        StringBuilder sb = new StringBuilder();
        sb.append("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>유저 목록</title>
                  회원가입 된 유저들
                </head>
                <body>
                """);

        for(User user : users){
            sb.append("<br>");
            sb.append(user.getName());
        }

        sb.append(
                "</body>\n" +
                "</html>");

        return sb.toString();
    }
}
