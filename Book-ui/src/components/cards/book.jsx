import React, { Component } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import { withStyles } from '@material-ui/core/styles';
import {
    ExpansionPanel,
    ExpansionPanelSummary,
    ExpansionPanelDetails,
    ExpansionPanelActions,
    Typography,
    Divider,
    Button,
    TextField,
    IconButton,
} from '@material-ui/core';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import DeleteIcon from '@material-ui/icons/Delete';
import withRoot from '../../themes/withRoot';
import { styles } from './book.js';
import { isNullOrUndefined } from '../../utility/utility.js'

class Book extends Component {
    constructor(props) {
        super(props);
        let content = props.book.pages.join("<br/>");
        this.state = {
            title: props.book.title,
            content: content,
            author: props.book.author,
            editing: false,
            totalPages: props.book.pages.length,
            totalCharacters: this.getTotalCharacters(props.book.pages),
        }
    }
    getTotalCharacters=(pages)=>{
        let totalCharacters=0;
        if(pages.length>0){
            totalCharacters = (pages.length-1)*255;
            let lastPage = pages[pages.length-1];
            if(isNullOrUndefined(lastPage)){
                lastPage=""
            }
            let numNewlines = lastPage.split("\n").length-1;
            if(isNullOrUndefined(numNewlines)||numNewlines<0){
                numNewlines=0;
            }
            let augmentedLength = lastPage.length+numNewlines;
            totalCharacters+=augmentedLength;
        }
        return totalCharacters;
    }

    updateHelperText=(content)=>{
        let pages = this.parsePages(content)
        let totalPages = pages.length;
        let totalCharacters = this.getTotalCharacters(pages);
        this.setState({totalPages:totalPages,totalCharacters:totalCharacters})
    }

    save = () => {
        this.setEditing(false);
        const { id } = this.props.book;
        const { title, content, author } = this.state;
        let pages = this.parsePages(content);
        if(this.props.isNew){
            this.props.createBook({book:{id:"",author:author,title:title,pages:pages}});
            this.props.removeNewBook()
        }else{
            this.props.updateBook({ book: { id: id, author: author, title: title, pages: pages } })
        }
        }

    splitString = (string, size, multiline = true) => {
        var matchAllToken = (multiline === true) ? '[^]' : '.';
        var re = new RegExp(matchAllToken + '{1,' + size + '}', 'g');
        return string.match(re);
    }

    delete = ()=>{
        this.setEditing(false);
        this.props.deleteBook({book:this.props.book});
        if(this.props.isNew){
            this.props.removeNewBook();
        }
    }

    parsePages = (content) => {
        //respect page split on <br/>
        let parts;
        if (content.includes("<br/>")) {
            parts = content.split("<br/>");
        } else {
            parts = [content];
        }

        let pages = [];
        for (let part of parts) {
            //maximum allowed character per page is 255 and newlines count as 2
            let newPages = this.splitString(part, 255);
            pages = pages.concat(newPages);
        }
        return pages;
    }

    setEditing = (bool) => {
        this.setState({ editing: bool })
    }

    onChange = e => { 
        this.setState({ [e.target.name]: e.target.value })
        if(e.target.name==="content"){
            this.updateHelperText(e.target.value);
        }
    }

    renderSummary = (classes) => {
        if (this.state.editing) {
            return (
                <div className={classes.container}>
                    <div className={classes.column}>
                        <Typography >title</Typography>
                        <TextField
                            name="title"
                            onChange={this.onChange}
                            value={this.state.title}
                            className={classNames(classes.textfield)}
                            margin="none"
                        />
                    </div>
                    <div className={classes.column}>
                        <Typography  className={classNames(classes.textfield)}>author</Typography>
                        <TextField
                            name="author"
                            onChange={this.onChange}
                            value={this.state.author}
                            margin="none"
                        />
                    </div>
                    <div className={classes.flex} />
                    <IconButton
                            className={classNames({ [`${classes.hide}`]: !this.state.editing })}
                            onClick={() => { this.delete() }}
                        >
                            <DeleteIcon/>
                        </IconButton>
                </div>
            )
        } else {
            return (
                <div className={classes.flexContainer}>
                    <div className={classNames(classes.title)}>{this.state.title}</div>
                    <div className={classes.flex} />
                    <div className={classNames(classes.author)}>{"written by " + this.state.author}</div>
                </div>
            )
        }
    }
    renderDetails = (classes) => {
        if (this.state.editing) {
            return (
                <div className={classes.fullwidth}>
                    <Typography  >content [pages: {this.state.totalPages}/50, characters: {this.state.totalCharacters}/12800]</Typography>
                    <textarea
                        name="content"
                        value={this.state.content}
                        onChange={this.onChange}
                        className={classNames(classes.textarea)} />
                </div>
            )
        }
        else {
            let content = this.state.content;
            content = content.replace(new RegExp("<br/>","g"), "")
            return (
                <div className={classes.content}>
                    {content}
                </div>
            )
        }
    }

    cancel=()=>{
        const book = this.props.book;
        let content = book.pages.join("<br/>"); 
        this.setState({ title: book.title,
            content: content,
            author: book.author,
            editing: false,
            totalPages: book.pages.length,
            totalCharacters: this.getTotalCharacters(book.pages),})
    }

    render() {
        const {  classes } = this.props
        const { editing } = this.state;
        return (
            <div className={classNames(classes.card)}>
                <ExpansionPanel>
                    <ExpansionPanelSummary expandIcon={<ExpandMoreIcon />}>
                        {this.renderSummary(classes)}
                    </ExpansionPanelSummary>
                    <ExpansionPanelDetails>
                        {this.renderDetails(classes)}
                    </ExpansionPanelDetails>
                    <Divider />
                    <ExpansionPanelActions>
                        <Button
                            size="small"
                            className={classNames({ [`${classes.hide}`]: !editing })}
                            onClick={() => { this.cancel() }}
                        >
                            Cancel
                        </Button>
                        <Button
                            size="small"
                            color="primary"
                            className={classNames({ [`${classes.hide}`]: !editing })}
                            onClick={() => { this.save() }}>
                            Save
                        </Button>
                        <Button
                            size="small"
                            color="primary"
                            className={classNames({ [`${classes.hide}`]: editing })}
                            onClick={() => { this.setEditing(true) }}
                        >
                            Edit
                        </Button>
                    </ExpansionPanelActions>
                </ExpansionPanel>


            </div>
        )
    }
}


Book.propTypes = {
    classes: PropTypes.object.isRequired,
    theme: PropTypes.object.isRequired,
    book: PropTypes.object.isRequired,
    updateBook: PropTypes.func.isRequired,
    deleteBook: PropTypes.func.isRequired,
    createBook: PropTypes.func.isRequired,
    //isNew: PropTypes.boolean,
    removeNewBook: PropTypes.func,
};

export default withRoot(withStyles(styles, { withTheme: true })(Book));