import {
    BOOKS,
    BOOKS_LOADING,
    API_URL,
} from '../constants/actions.js';


//books
export function refreshBooks() {
    return (dispatch) => {
        dispatch(booksLoading(true));
        dispatch(listBooks());
    }
}

export function updateBook({book}){
    return (dispatch) =>{
        const endpoint = `${API_URL}/books/${book.id}`;
        fetch(endpoint,{
                method: 'PATCH',
                body: JSON.stringify(book),
                headers: {
                    "Content-Type": "application/json",
                },
        })
        .then(res=>{
            dispatch(refreshBooks())
        })
        .catch(
            e => {console.log(e);}
        )
    }
}

export function createBook({ book }) {
    return (dispatch) => {
        const endpoint = `${API_URL}/books`;
        fetch(endpoint,{
                method: 'POST',
                body: JSON.stringify(book),
                headers: {
                    "Content-Type": "application/json",
                },
        }).then(res=>{
            dispatch(refreshBooks())
        })
        .catch(
            e => {console.log(e);}
        )
    }
}

export function deleteBook({ book }) {
    //console.log(JSON.stringify(book));
    return (dispatch) => {
        const endpoint = `${API_URL}/books`;
        fetch(endpoint,{
                method: 'DELETE',
                body: JSON.stringify(book),
                headers: {
                    "Content-Type": "application/json",
                },
        }).then(res=>{
            dispatch(refreshBooks())
        })
        .catch(
            e => {console.log(e);}
        )
    }
}

function listBooks() {
    return (dispatch) => {
        const endpoint = `${API_URL}/books`;
        fetch(endpoint, { method: 'GET', })
            .then(res => {
                res.json().then((json) => {
                    dispatch(books(json));
                })
            })
            .catch(e => { console.log(e); })
            .finally(() => {
                dispatch(booksLoading(false));
            })
    }
}


export function booksLoading(bool) {
    return {
        type: BOOKS_LOADING,
        payload: bool
    }
}


export function books(booksList) {
    return {
        type: BOOKS,
        payload: booksList
    }
}