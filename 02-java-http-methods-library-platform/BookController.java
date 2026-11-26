package com.vizja.swp.lab2;

import com.vizja.swp.lab2.lib.BaseController;
import com.vizja.swp.lab2.lib.http.HttpRequest;
import com.vizja.swp.lab2.lib.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BookController extends BaseController {
    private static final List<Book> books = new ArrayList<>();
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    static {
        // Initial sample data
        books.add(new Book(idCounter.getAndIncrement(), "The Hobbit", "J.R.R. Tolkien", "Fantasy"));
        books.add(new Book(idCounter.getAndIncrement(), "1984", "George Orwell", "Dystopian"));
        books.add(new Book(idCounter.getAndIncrement(), "Dune", "Frank Herbert", "Sci-Fi"));
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(200, "OK");

        // Simple JSON serialization of the book list
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            json.append(String.format("{\"id\":%d,\"title\":\"%s\",\"author\":\"%s\",\"genre\":\"%s\"}",
                    b.getId(), b.getTitle(), b.getAuthor(), b.getGenre()));
            if (i < books.size() - 1) json.append(",");
        }
        json.append("]");

        response.getWriter().println(json.toString());
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String body = request.getBody();
        String title = parseJsonField(body, "title");
        String author = parseJsonField(body, "author");
        String genre = parseJsonField(body, "genre");

        if (title == null || author == null || genre == null) {
            response.setStatus(400, "Bad Request");
            response.getWriter().println("{\"error\":\"Missing fields (title, author, genre required)\"}");
            return;
        }

        Book newBook = new Book(idCounter.getAndIncrement(), title, author, genre);
        books.add(newBook);

        response.setStatus(201, "Created");
        response.getWriter().println(String.format("{\"id\":%d,\"title\":\"%s\",\"author\":\"%s\",\"genre\":\"%s\"}",
                newBook.getId(), newBook.getTitle(), newBook.getAuthor(), newBook.getGenre()));
    }

    @Override
    public void doPut(HttpRequest request, HttpResponse response) {
        String body = request.getBody();
        String idStr = parseJsonField(body, "id");

        if (idStr == null) {
            response.setStatus(400, "Bad Request");
            response.getWriter().println("{\"error\":\"Book ID required for update\"}");
            return;
        }

        int id = Integer.parseInt(idStr);
        Book book = books.stream().filter(b -> b.getId() == id).findFirst().orElse(null);

        if (book == null) {
            response.setStatus(404, "Not Found");
            response.getWriter().println("{\"error\":\"Book not found\"}");
            return;
        }

        String title = parseJsonField(body, "title");
        String author = parseJsonField(body, "author");
        String genre = parseJsonField(body, "genre");

        if (title != null) book.setTitle(title);
        if (author != null) book.setAuthor(author);
        if (genre != null) book.setGenre(genre);

        response.setStatus(200, "OK");
        response.getWriter().println(String.format("{\"id\":%d,\"title\":\"%s\",\"author\":\"%s\",\"genre\":\"%s\"}",
                book.getId(), book.getTitle(), book.getAuthor(), book.getGenre()));
    }

    @Override
    public void doDelete(HttpRequest request, HttpResponse response) {
        String body = request.getBody();
        String idStr = parseJsonField(body, "id");

        if (idStr == null) {
            response.setStatus(400, "Bad Request");
            response.getWriter().println("{\"error\":\"Book ID required for deletion\"}");
            return;
        }

        int id = Integer.parseInt(idStr);
        boolean removed = books.removeIf(b -> b.getId() == id);

        if (removed) {
            response.setStatus(200, "OK");
            response.getWriter().println("{\"message\":\"Book deleted successfully\"}");
        } else {
            response.setStatus(404, "Not Found");
            response.getWriter().println("{\"error\":\"Book not found\"}");
        }
    }

    // Helper to parse JSON fields without external libraries
    private String parseJsonField(String json, String key) {
        if (json == null) return null;
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start != -1) {
            start += pattern.length();
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        }
        // Check for numeric values like id
        pattern = "\"" + key + "\":";
        start = json.indexOf(pattern);
        if (start != -1) {
            start += pattern.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return json.substring(start, end).trim();
        }
        return null;
    }
}