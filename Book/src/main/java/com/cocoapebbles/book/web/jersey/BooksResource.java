package com.cocoapebbles.book.web.jersey;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;


@Path("/")
public class BooksResource {

    @Inject
    private Logger logger;
    @Inject
    private BookDAO bookDAO;

    @OPTIONS
    public Response getOptions(){
        return Response.status(200)
                .header("Access-Control-Allow-Origin","*")
                .header("Access-Control-Allow-Methods","GET, POST, PATCH, DELETE")
                .header("Access-Control-Allow-Headers","origin, content-type, accept").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks() {
        List<Book> books = bookDAO.getBooks();
        GenericEntity<List<Book>> genericEntity = new GenericEntity<List<Book>>(books) {};
        return Response.status(200).header("Access-Control-Allow-Origin","*").entity(genericEntity).build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBook(Book book){
        book.setId(UUID.randomUUID().toString().substring(0,8));
        logger.info("Creating book with id: "+book.getId());
        bookDAO.createBook(book);
        String output = "success";
        return Response.status(200).header("Access-Control-Allow-Origin","*").entity(output).build();

    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteBook(Book book){
        logger.info("Deleting book with id: "+book.getId());
        bookDAO.deleteBook(book);
        String output = "success";
        return Response.status(200).header("Access-Control-Allow-Origin","*").entity(output).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBook(Book book){
        logger.info("updating: ");
        if(book.getAuthor().trim().equals("")&&book.getTitle().trim().equals("")&&book.getPages().isEmpty()){
            //in the case that someone wants to save a book that has no useful data then simply delete it
            bookDAO.deleteBook(book);
        } else if (book.getId().trim().equals("")||book.getId()==null) {
            //if we try to update a book that doesnt exist then we need to simply create that book instead
            logger.info("Creating book with id: "+book.getId());
            book.setId(UUID.randomUUID().toString().substring(0,8));
            createBook(book);
        }
        else{
            logger.info("Updating book with id: "+book.getId());
            bookDAO.updateBook(book);
        }

        String output = "success";
        return Response.status(200).header("Access-Control-Allow-Origin","*").entity(output).build();
    }

    @OPTIONS
    @Path("/{id}")
    public Response getIdOptions(){
        return Response.status(200)
                .header("Access-Control-Allow-Origin","*")
                .header("Access-Control-Allow-Methods","GET, POST, PATCH, DELETE")
                .header("Access-Control-Allow-Headers","origin, content-type, accept").build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBook(@PathParam("id") String bookId) {
        Book book = bookDAO.getBook(bookId);
        GenericEntity<Book> genericEntity = new GenericEntity<Book>(book){};
        return Response.status(200).header("Access-Control-Allow-Origin","*").entity(genericEntity).build();
    }

}