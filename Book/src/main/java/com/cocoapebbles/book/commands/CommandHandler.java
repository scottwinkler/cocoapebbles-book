package com.cocoapebbles.book.commands;

import com.cocoapebbles.book.web.jersey.Book;
import com.cocoapebbles.book.web.jersey.BookDAO;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class CommandHandler implements CommandExecutor {
    private JavaPlugin p;

    public CommandHandler(JavaPlugin p) {
        this.p = p;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        String[] otherArgs = Arrays.copyOfRange(args, 1, args.length);
        switch(args[0]){
            case "create": return createHandler(sender,otherArgs);
            case "delete": return deleteHandler(sender,otherArgs);
            case "help": return helpHandler(sender,otherArgs);
            case "import": return importHandler(sender,otherArgs);
            case "list": return listHandler(sender,otherArgs);
            default: return false;
        }
    }

    public boolean createHandler(CommandSender sender, String[]args){
        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED+ "[BOOK] You must be a player to use this command!");
            return false;
        }
        Player player = (Player) sender;
        BookDAO bookDAO = new BookDAO(p);
        if(args.length<1){
            player.sendMessage(ChatColor.RED+ "[BOOK] You must include an id of the book to create");
            return false;
        }
        String bookId = args[0];
        Book book = bookDAO.getBook(bookId);
        if(book==null){
            player.sendMessage(ChatColor.RED+"[BOOK] No book found with id: "+bookId);
            return false;
        }

        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.setPages(book.getPages());
        bookMeta.setAuthor(book.getAuthor());
        bookMeta.setTitle(book.getTitle());
        bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);
        itemStack.setItemMeta(bookMeta);
        player.getInventory().addItem(itemStack);
        player.sendMessage(ChatColor.GOLD + "[BOOK] Fate smiles upon you this day");
        return true;
    }

    public boolean deleteHandler(CommandSender sender, String[]args){
        BookDAO bookDAO = new BookDAO(p);
        if(args.length<1){
            sender.sendMessage(ChatColor.RED+ "[BOOK] You must include an id of the book to delete");
            return false;
        }
        String bookId = args[0];
        Book book = bookDAO.getBook(bookId);
        if(book==null){
            sender.sendMessage(ChatColor.RED+ "[BOOK] No book found with id: "+bookId);
            return false;
        }

        bookDAO.deleteBook(book);
        sender.sendMessage(ChatColor.AQUA+ "[BOOK] Book with id: "+bookId+ " deleted");
        return true;
    }

    public boolean helpHandler(CommandSender sender, String[]args){
        String[] message = new String[]{
                ChatColor.AQUA+ "[BOOK] Cocoapebble's book Mod!",
                ChatColor.AQUA+"  create <id>:creates a written book with the given id",
                ChatColor.AQUA+"  delete <id>:deletes server data for a book with the given id",
                ChatColor.AQUA+"  help: makes all your dreams come true",
                ChatColor.AQUA+"  import: reads a book equipped in player's main hand into server data",
                ChatColor.AQUA+"  list: lists all registered book titles and their ids"
        };
        sender.sendMessage(message);
        return true;
    }

    public boolean importHandler(CommandSender sender, String[]args){
        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED+ "[BOOK] You must be a player to use this command!");
            return false;
        }
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType()!=Material.WRITTEN_BOOK){
            player.sendMessage(ChatColor.RED+"[BOOK] Must have a written book equipped the main hand to use the import command");
            return false;
        }
        BookMeta bookMeta = (BookMeta) item.getItemMeta();
        Book book = new Book();
        book.setId(UUID.randomUUID().toString().substring(0,8));
        ArrayList<String> cleanedPages = new ArrayList<>();
        for (String page : bookMeta.getPages()){
            //weird characters are being transcribed. not sure why this is happening
            page=page.replaceAll("§0","");
            cleanedPages.add(page);
        }
        book.setPages(cleanedPages);
        book.setTitle(bookMeta.getTitle());
        book.setAuthor(bookMeta.getAuthor());
        BookDAO bookDAO = new BookDAO(p);
        bookDAO.createBook(book);
        String message = ChatColor.GOLD + "[BOOK] Knowledge is power";
        sender.sendMessage(message);
        return true;
    }

    public boolean listHandler(CommandSender sender, String[]args){
        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED+ "[BOOK] You must be a player to use this command!");
            return false;
        }
        Player player = (Player) sender;
        BookDAO bookDAO = new BookDAO(p);
        player.sendMessage(ChatColor.AQUA+"[BOOK] List of Books:");
        ArrayList<Book> books = (ArrayList<Book>) bookDAO.getBooks();
        for (Book book : books){
            player.sendMessage(ChatColor.AQUA+"  "+ book.toString());
        }
        return true;
    }
}


