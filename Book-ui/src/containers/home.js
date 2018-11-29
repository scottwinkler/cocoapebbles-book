import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import Home from '../components/views/home.jsx'
import {
    createBook,
    refreshBooks,
    updateBook,
    deleteBook,
} from '../actions/home.js';

const mapStateToProps=(state)=>{
   
return{
    books: state.books.books,
    router: state.router,
    };
};

const mapDispatchToProps = (dispatch) =>{
    return{
      refreshBooks: bindActionCreators(refreshBooks,dispatch),
      createBook: bindActionCreators(createBook,dispatch),
      updateBook: bindActionCreators(updateBook,dispatch),
      deleteBook: bindActionCreators(deleteBook,dispatch),
    }
};

export default connect(mapStateToProps,mapDispatchToProps)(Home);