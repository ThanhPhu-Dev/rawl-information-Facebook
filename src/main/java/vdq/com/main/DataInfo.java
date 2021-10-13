package vdq.com.main;

import java.util.ArrayList;
import java.util.List;

public class DataInfo {
    private String author;
    private String linkDetail;
    private String content;
    List<String> linkImg = new ArrayList<String>();

    public void addImage(String img){
        linkImg.add(img);
    }
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLinkDetail() {
        return linkDetail;
    }

    public void setLinkDetail(String linkDetail) {
        this.linkDetail = linkDetail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getLinkImg() {
        return linkImg;
    }
}
