export const BOOKS = 'BOOKS';
export const BOOKS_LOADING = 'BOOKS_LOADING'
let href = window.location.href;
if (href.charAt(href.length - 1) == '/') {
  href = href.substr(0, href.length - 1);
}
export const API_URL= href;