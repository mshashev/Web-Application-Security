# Digital Library Web Application

A Java web application that implements a digital library catalog to demonstrate HTTP request methods (`GET`, `POST`, `PUT`, `DELETE`) and client-server data exchange[cite: 21, 24].

## What It Does
* **REST API (`BookController`):** Handles creating, reading, updating, and deleting books via JSON payloads over `/api/books`.
* **Interactive Web Interface (`HomeController`):** Provides a browser UI using asynchronous `fetch` calls to add, edit, delete, and filter books by genre or author.

## Screenshots
![Trigering GET and POST](Trigering-GET-and-POST-HTTP-methods.png)
![GET and POST Results](GET-POST.png)
![HTTP PUT Method](HTTP-PUT-method.png)
![Editing Triggering PUT HTTP method](Editing-Trigering-PUT-HTTP-method.png)
![HTTP DELETE Method](HTTP-DELETE-method.png)

## How to Run
1. Open the project in IntelliJ IDEA and run `Application.java` (starts the server on port `8080`)[cite: 22].
2. Open `http://localhost:8080` in your browser.

