import React, { Component } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import { withStyles } from '@material-ui/core/styles';
import {
    IconButton,
    Typography,
    Toolbar
} from '@material-ui/core';
import {
    Refresh as RefreshIcon,
    Add as AddIcon,
} from '@material-ui/icons';
import withRoot from '../../themes/withRoot';
import { styles } from './home.js';
import Book from '../cards/book.jsx';
import { isNullOrUndefined } from '../../utility/utility.js'
class Home extends Component {
    state = {
        newBook: null
    };
    componentWillMount = () => {
        const kvs = this.props.router.location.search.split("&")
        let bookId
        for (let kv of kvs) {
          const kv_pair = kv.split("=")
          const key = kv_pair[0]
          const value = kv_pair[1]
          if (key.includes("bookId")){
             bookId = value;
          }
          this.props.refreshBooks({bookId:bookId})
        }
    }

    createEmptyBook = () => {
        let newBook = {author:"Unknown",pages:[],title:"Untitled",id:"N/A"}
        this.setState({newBook:newBook});
    }

    renderBooks = (books,isNew) => {
        if (isNullOrUndefined(books)) {
            return null
        }
        return books.map((book, index) => {
            let uuid=null;
            if (isNullOrUndefined(book.id)){
                uuid =  Math.floor(Math.random()*90000) + 10000;
            } else{
                uuid = book.id;
            }
            return (
                <Book
                    key={uuid}
                    index={index}
                    book={book}
                    updateBook={this.props.updateBook}
                    deleteBook={this.props.deleteBook}
                    createBook={this.props.createBook}
                    isNew ={isNew}
                    removeNewBook={this.removeNewBook}
                />
            )
        })
    }
    removeNewBook=()=>{
        this.setState({newBook:null})
    }
    render() {
        const { books,  classes } = this.props;
        const booksSavedList = this.renderBooks(books,false)
        let booksUnsavedList = [];
        if(!isNullOrUndefined(this.state.newBook)){
            booksUnsavedList = this.renderBooks([this.state.newBook],true)
        }
        const allBooksList = booksUnsavedList.concat(booksSavedList)
        return (
            <div className={classNames(classes.root)}>
                <Toolbar>
                     <Typography variant="h5">Cocoapebble's Book Mod</Typography>
                    <div className={classes.flex}/>
                    <IconButton onClick={() => { this.createEmptyBook() }} >
                        <AddIcon />
                    </IconButton>
                    <IconButton onClick={() => { this.props.refreshBooks() }} >
                        <RefreshIcon />
                    </IconButton>
                </Toolbar>
                {allBooksList}
            </div>
        )
    }
}

Home.propTypes = {
    classes: PropTypes.object.isRequired,
    theme: PropTypes.object.isRequired,
    refreshBooks: PropTypes.func.isRequired,
    books: PropTypes.array.isRequired,
    updateBook: PropTypes.func.isRequired,
    deleteBook: PropTypes.func.isRequired,
    createBook: PropTypes.func.isRequired,
};


export default withRoot(withStyles(styles, { withTheme: true })(Home));