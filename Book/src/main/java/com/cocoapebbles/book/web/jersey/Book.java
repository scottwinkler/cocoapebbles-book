package com.cocoapebbles.book.web.jersey;
import java.util.List;
public class Book {
    private String id;
    private String title;
    private List<String> pages;
    private String author;

    public Book(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> pages) {
        this.pages = pages;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String toString(){
        return "\""+this.getTitle() +"\" by " + this.getAuthor()+" ["+this.getId()+"]";
    }
}
