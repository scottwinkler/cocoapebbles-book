import {combineReducers} from 'redux';
import {books,booksLoading} from './home.js';
const rootReducer=combineReducers({
   books:combineReducers({books,booksLoading}),
})
export default rootReducer;