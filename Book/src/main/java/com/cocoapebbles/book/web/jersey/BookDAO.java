package com.cocoapebbles.book.web.jersey;

import com.google.gson.Gson;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class BookDAO {
    private JavaPlugin p;
    private Gson gson;
    private String dirPath;
    private Logger logger;

    public BookDAO(JavaPlugin p){
        this.p = p;
        this.logger = p.getLogger();
        dirPath = p.getDataFolder().getAbsolutePath()+"/books";
        try {
            FileUtils.forceMkdir(new File(dirPath));
        }catch(IOException e){
            logger.severe(e.getMessage());
        }
        gson = new Gson();
    }

   public void deleteBook(Book book){
        File file = new File(dirPath+"/"+book.getId()+".json");
        try {
            FileUtils.deleteQuietly(file);
        }catch(Exception e){
            logger.severe(e.getMessage());
        }
    }

    public void updateBook(Book book){
        deleteBook(book);
        createBook(book);
    }

    public void createBook(Book book){
        try{
            File file = new File(dirPath+"/"+ book.getId()+".json");
            FileUtils.write(file,gson.toJson(book),"UTF-8");
        } catch(IOException e){
            logger.severe(e.getMessage());
        }
    }

    public Book getBook(String bookId){
        Book book = null;
        try{
            String s = FileUtils.readFileToString(new File(dirPath+"/"+bookId+".json"),"UTF-8");
            book = gson.fromJson(s,Book.class);
        }catch(IOException e){
            logger.severe(e.getMessage());
        }
        return book;
    }

     public List<Book> getBooks(){
        ArrayList<Book> books = new ArrayList<>();
        File dir = new File(dirPath);
        File[] fileArr = dir.listFiles();
        for (File file : fileArr){
            String bookId = FilenameUtils.getBaseName(file.getName());
            Book book = getBook(bookId);
            books.add(book);
        }
         return books;
    }
}
