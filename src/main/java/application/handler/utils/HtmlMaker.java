package application.handler.utils;

import application.model.Article;
import application.model.User;

import java.util.List;

public class HtmlMaker {
    public static String getArticlePage(Article article , String template, int index){
        String nextPath = "/main/article?index=" + (index + 1);
        String prevPath = "/main/article?index=" + (index - 1);

        return template
                .replace("article_image" , article.getFilePath())
                .replace("writer_account" , article.getWriter())
                .replace("article_content" , article.getContent())
                .replace("next_article", nextPath)
                .replace("prev_article", prevPath);
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
