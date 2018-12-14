# Cocoapebble's Book Mod
This spigot mod makes it easy to write a library worth of books in Minecraft. If you have ever tried to use the in game book editor you will know how much of a struggle it is to write anything more than a few pages. This mod starts up a web app on your minecraft server to allow you to easily edit books of any length. Book configurations are stored in json on the server and can be created in Minecraft by using a simple command.

![alt text](https://raw.githubusercontent.com/scottwinkler/cocoapebbles-book/master/cool-pic.png)

## Setup
To build the plugin jar file

'''
cd ./Book
mvn install
'''

Copy this jar file into your /plugins folder on your spigot server and reload or restart your server. Your web app will now be available on port 8080. This port can be configured in the config.yml.

## Use
In the web app you can create, update and delete books. When editing a book, the content will map to the pages of a Minecraft book. You can paste raw text in, and when you save the book, it will automatically insert `<br/>` tags where necessary to indicate new pages in Minecraft. This also means you can force a new page by using this `<br/>` tag, which can be helpful when writing the the last page of a chapter of your book, for example. In addition to setting the content, you can give the book a title and and author to your liking.

Once you are satisfied with your masterpiece, you can log into minecraft and create this book by first typing the command `/book list`. This will give you the 8 digit id of your book which you will use to create it in game by using the command `/book create <id>`. If you already wrote a book and wish to import it into this mods config, simply equip the book in your main hand and type `/book import`.


### List of Commands

```
/book create <id> -- creates a written book with the given id
/book delete <id> -- deletes server data for a book with the given id
/book help -- makes all your dreams come true
/book import -- reads a book equipped in player's main hand into server data
/book list -- lists all registered book titles and their ids
```

### Permissions
The only permission for this plugin is "book" which is default set to true because I am lazy and didn't want to come up with anything more comprehensive than that. Anyways I doubt any young kids are going to care enough to abuse a plugin like this anyways.
