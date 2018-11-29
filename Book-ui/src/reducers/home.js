import {BOOKS,BOOKS_LOADING} from '../constants/actions.js'

//books
export function books(state=[],action){
    switch(action.type){
        case BOOKS:
            return action.payload;
        default:
            return state;
    }
}
export function booksLoading(state=false,action){
    switch(action.type){
        case BOOKS_LOADING:
        return action.payload;
        default:
        return state;
    }
}