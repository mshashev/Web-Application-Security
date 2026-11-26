package com.vizja.swp.lab2;

import com.vizja.swp.lab2.lib.BaseController;
import com.vizja.swp.lab2.lib.http.HttpRequest;
import com.vizja.swp.lab2.lib.http.HttpResponse;

public class HomeController extends BaseController {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(200, "OK");
        response.getWriter().println("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Library Platform</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; background: #f4f4f9; color: #333; }
                    .container { max-width: 700px; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
                    h1 { color: #2c3e50; }
                    .form-group { display: flex; gap: 10px; margin-bottom: 15px; }
                    input, select { padding: 8px; flex: 1; border: 1px solid #ccc; border-radius: 4px; }
                    button { padding: 8px 15px; background: #3498db; color: white; border: none; border-radius: 4px; cursor: pointer; }
                    button:hover { background: #2980b9; }
                    .book-card { background: #fafafa; border: 1px solid #e1e1e1; padding: 12px; margin-bottom: 10px; border-radius: 4px; display: flex; justify-content: space-between; align-items: center; }
                    .filter-section { margin-bottom: 20px; display: flex; gap: 10px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>📚 Digital Library Platform</h1>
                    
                    <div class="filter-section">
                        <select id="filterGenre" onchange="filterBooks()">
                            <option value="">All Genres</option>
                            <option value="Fantasy">Fantasy</option>
                            <option value="Sci-Fi">Sci-Fi</option>
                            <option value="Dystopian">Dystopian</option>
                        </select>
                        <input type="text" id="filterAuthor" placeholder="Filter by author..." oninput="filterBooks()">
                    </div>

                    <h3>Add New Book</h3>
                    <div class="form-group">
                        <input type="text" id="title" placeholder="Book Title">
                        <input type="text" id="author" placeholder="Author">
                        <input type="text" id="genre" placeholder="Genre">
                        <button onclick="addBook()">Add</button>
                    </div>

                    <h3>Library Catalog</h3>
                    <div id="bookList"></div>
                </div>

                <script>
                    let allBooks = [];

                    async function fetchBooks() {
                        const res = await fetch('/api/books');
                        allBooks = await res.json();
                        displayBooks(allBooks);
                    }

                    function displayBooks(books) {
                        const list = document.getElementById('bookList');
                        list.innerHTML = '';
                        books.forEach(b => {
                            list.innerHTML += `
                                <div class="book-card">
                                    <div>
                                        <strong>${b.title}</strong> by ${b.author} <em>(${b.genre})</em>
                                    </div>
                                    <div>
                                        <button onclick="editBook(${b.id}, '${b.title}', '${b.author}', '${b.genre}')" style="background: #f39c12; margin-right: 5px;">Edit</button>
                                        <button onclick="deleteBook(${b.id})" style="background: #e74c3c;">Delete</button>
                                    </div>
                                </div>
                            `;
                        });
                    }

                    function filterBooks() {
                        const genre = document.getElementById('filterGenre').value;
                        const author = document.getElementById('filterAuthor').value.toLowerCase();
                        
                        const filtered = allBooks.filter(b => {
                            return (genre === "" || b.genre === genre) &&
                                   (b.author.toLowerCase().includes(author));
                        });
                        displayBooks(filtered);
                    }

                    async function addBook() {
                        const title = document.getElementById('title').value;
                        const author = document.getElementById('author').value;
                        const genre = document.getElementById('genre').value;

                        if(!title || !author || !genre) return alert('Please fill all fields');

                        await fetch('/api/books', {
                            method: 'POST',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify({title, author, genre})
                        });

                        document.getElementById('title').value = '';
                        document.getElementById('author').value = '';
                        document.getElementById('genre').value = '';
                        fetchBooks();
                    }

                    async function editBook(id, oldTitle, oldAuthor, oldGenre) {
                        const title = prompt("Edit Book Title:", oldTitle);
                        const author = prompt("Edit Author:", oldAuthor);
                        const genre = prompt("Edit Genre:", oldGenre);

                        if (!title || !author || !genre) return;

                        await fetch('/api/books', {
                            method: 'PUT',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify({ id, title, author, genre })
                        });
                        fetchBooks();
                    }

                    async function deleteBook(id) {
                        await fetch('/api/books', {
                            method: 'DELETE',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify({id})
                        });
                        fetchBooks();
                    }

                    fetchBooks();
                </script>
            </body>
            </html>
            """);
    }
}