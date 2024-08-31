INSERT INTO books_categories (book_id, category_id) VALUES ((SELECT id FROM books WHERE id = 10),(SELECT id FROM categories WHERE id = 10));
INSERT INTO books_categories (book_id, category_id) VALUES ((SELECT id FROM books WHERE id = 11),(SELECT id FROM categories WHERE id = 10));
INSERT INTO books_categories (book_id, category_id) VALUES ((SELECT id FROM books WHERE id = 12),(SELECT id FROM categories WHERE id = 11));
