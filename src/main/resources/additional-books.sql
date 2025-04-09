-- Additional books for the bookstore
INSERT INTO books (title, author, isbn, price, description, category, stock_quantity, format, image_url, created_at, updated_at) 
VALUES 
    -- Fiction
    ('The Midnight Library', 'Matt Haig', '978-0525559474', 19.99, 'Between life and death there is a library, and within that library, the shelves go on forever.', 'Fiction', 45, 'PHYSICAL', 'https://example.com/midnight-library.jpg', NOW(), NOW()),
    ('Where the Crawdads Sing', 'Delia Owens', '978-0735219090', 16.99, 'A novel about a young woman who raised herself in the marshes of the deep South', 'Fiction', 38, 'PHYSICAL', 'https://example.com/crawdads.jpg', NOW(), NOW()),
    ('The Silent Patient', 'Alex Michaelides', '978-1250301697', 14.99, 'A psychological thriller about a woman's act of violence against her husband', 'Thriller', 52, 'PHYSICAL', 'https://example.com/silent-patient.jpg', NOW(), NOW()),
    ('The Vanishing Half', 'Brit Bennett', '978-0525536291', 18.99, 'The lives of twin sisters who choose to live in different racial worlds', 'Fiction', 33, 'PHYSICAL', 'https://example.com/vanishing-half.jpg', NOW(), NOW()),

    -- Fantasy
    ('A Game of Thrones', 'George R. R. Martin', '978-0553593716', 9.99, 'The first novel in A Song of Ice and Fire, an epic series set in the Seven Kingdoms of Westeros', 'Fantasy', 65, 'PHYSICAL', 'https://example.com/got.jpg', NOW(), NOW()),
    ('The Way of Kings', 'Brandon Sanderson', '978-0765365279', 12.99, 'The first volume in the Stormlight Archive, an epic fantasy series', 'Fantasy', 70, 'PHYSICAL', 'https://example.com/way-of-kings.jpg', NOW(), NOW()),
    ('The Name of the Wind', 'Patrick Rothfuss', '978-0756404741', 10.99, 'The first day in the three-day account of Kvothe, an adventurer and notorious musician', 'Fantasy', 42, 'PHYSICAL', 'https://example.com/name-of-wind.jpg', NOW(), NOW()),
    ('Mistborn: The Final Empire', 'Brandon Sanderson', '978-0765350381', 11.99, 'A dystopian fantasy novel set in a world where ash falls from the sky', 'Fantasy', 55, 'PHYSICAL', 'https://example.com/mistborn.jpg', NOW(), NOW()),

    -- Science Fiction
    ('Project Hail Mary', 'Andy Weir', '978-0593135204', 15.99, 'A lone astronaut must save the earth from disaster', 'Science Fiction', 48, 'PHYSICAL', 'https://example.com/hail-mary.jpg', NOW(), NOW()),
    ('Ready Player One', 'Ernest Cline', '978-0307887436', 9.99, 'A dystopian novel set in a virtual reality world', 'Science Fiction', 52, 'PHYSICAL', 'https://example.com/ready-player.jpg', NOW(), NOW()),
    ('The Three-Body Problem', 'Cixin Liu', '978-0765382030', 17.99, 'A science fiction novel by Chinese writer Liu Cixin', 'Science Fiction', 37, 'PHYSICAL', 'https://example.com/three-body.jpg', NOW(), NOW()),
    ('Hyperion', 'Dan Simmons', '978-0553283686', 11.99, 'A science fiction novel set on the planet Hyperion', 'Science Fiction', 29, 'PHYSICAL', 'https://example.com/hyperion.jpg', NOW(), NOW()),

    -- Mystery/Thriller
    ('Gone Girl', 'Gillian Flynn', '978-0307588371', 13.99, 'A thriller novel about a woman who mysteriously disappears on her wedding anniversary', 'Thriller', 63, 'PHYSICAL', 'https://example.com/gone-girl.jpg', NOW(), NOW()),
    ('The Girl with the Dragon Tattoo', 'Stieg Larsson', '978-0307454546', 12.99, 'A psychological thriller novel and the first book in the Millennium series', 'Thriller', 58, 'PHYSICAL', 'https://example.com/dragon-tattoo.jpg', NOW(), NOW()),
    ('The Da Vinci Code', 'Dan Brown', '978-0307474278', 9.99, 'A mystery thriller novel', 'Thriller', 72, 'PHYSICAL', 'https://example.com/davinci-code.jpg', NOW(), NOW()),
    ('And Then There Were None', 'Agatha Christie', '978-0062073488', 8.99, 'A mystery novel by one of the most famous mystery writers', 'Mystery', 44, 'PHYSICAL', 'https://example.com/and-then.jpg', NOW(), NOW()),

    -- Romance
    ('The Notebook', 'Nicholas Sparks', '978-1455558025', 10.99, 'A romantic novel about the enduring power of love', 'Romance', 55, 'PHYSICAL', 'https://example.com/notebook.jpg', NOW(), NOW()),
    ('Me Before You', 'Jojo Moyes', '978-0143124542', 9.99, 'A romance novel between a quadriplegic and his caregiver', 'Romance', 49, 'PHYSICAL', 'https://example.com/me-before-you.jpg', NOW(), NOW()),
    ('Outlander', 'Diana Gabaldon', '978-0440212560', 12.99, 'A historical romance with elements of science fiction', 'Romance', 61, 'PHYSICAL', 'https://example.com/outlander.jpg', NOW(), NOW()),
    ('Red, White & Royal Blue', 'Casey McQuiston', '978-1250316776', 10.99, 'A romantic comedy about a romance between the US President's son and a British prince', 'Romance', 53, 'PHYSICAL', 'https://example.com/red-white.jpg', NOW(), NOW()),

    -- Non-Fiction
    ('Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', '978-0062316097', 24.99, 'A brief history of humanity', 'Non-Fiction', 78, 'PHYSICAL', 'https://example.com/sapiens.jpg', NOW(), NOW()),
    ('Educated', 'Tara Westover', '978-0399590504', 18.99, 'A memoir about a woman who leaves her survivalist family', 'Non-Fiction', 67, 'PHYSICAL', 'https://example.com/educated.jpg', NOW(), NOW()),
    ('Atomic Habits', 'James Clear', '978-0735211292', 14.99, 'A guide to building good habits and breaking bad ones', 'Self-Help', 85, 'PHYSICAL', 'https://example.com/atomic-habits.jpg', NOW(), NOW()),
    ('Becoming', 'Michelle Obama', '978-1524763138', 19.99, 'A memoir by the former First Lady of the United States', 'Biography', 72, 'PHYSICAL', 'https://example.com/becoming.jpg', NOW(), NOW()),

    -- Young Adult
    ('The Hunger Games', 'Suzanne Collins', '978-0439023481', 10.99, 'A dystopian novel set in a post-apocalyptic nation', 'Young Adult', 92, 'PHYSICAL', 'https://example.com/hunger-games.jpg', NOW(), NOW()),
    ('The Fault in Our Stars', 'John Green', '978-0142424179', 9.99, 'A young adult novel about a young girl with cancer', 'Young Adult', 78, 'PHYSICAL', 'https://example.com/fault-stars.jpg', NOW(), NOW()),
    ('Percy Jackson & The Olympians: The Lightning Thief', 'Rick Riordan', '978-0786838653', 10.99, 'A fantasy adventure novel based on Greek mythology', 'Young Adult', 86, 'PHYSICAL', 'https://example.com/percy-jackson.jpg', NOW(), NOW()),
    ('Six of Crows', 'Leigh Bardugo', '978-1627792127', 11.99, 'A fantasy heist novel set in the Grishaverse', 'Young Adult', 74, 'PHYSICAL', 'https://example.com/six-crows.jpg', NOW(), NOW()),

    -- Digital/eBook format books
    ('The Alchemist', 'Paulo Coelho', '978-0061122415', 7.99, 'A philosophical novel about following your dreams', 'Fiction', 0, 'DIGITAL', 'https://example.com/alchemist.jpg', NOW(), NOW()),
    ('The Power of Habit', 'Charles Duhigg', '978-0812981605', 9.99, 'An examination of the science of habit formation', 'Self-Help', 0, 'DIGITAL', 'https://example.com/power-habit.jpg', NOW(), NOW()),
    ('The Martian', 'Andy Weir', '978-0553418026', 8.99, 'A science fiction novel about an astronaut stranded on Mars', 'Science Fiction', 0, 'DIGITAL', 'https://example.com/martian.jpg', NOW(), NOW()),
    ('The Subtle Art of Not Giving a F*ck', 'Mark Manson', '978-0062457714', 9.99, 'A counterintuitive approach to living a good life', 'Self-Help', 0, 'DIGITAL', 'https://example.com/subtle-art.jpg', NOW(), NOW()),

    -- Audiobooks
    ('Thinking, Fast and Slow', 'Daniel Kahneman', '978-0374533557', 14.99, 'A book about the two systems that drive the way we think', 'Psychology', 0, 'AUDIO', 'https://example.com/thinking.jpg', NOW(), NOW()),
    ('Harry Potter and the Sorcerer\'s Stone', 'J.K. Rowling', '978-0590353427', 29.99, 'The first novel in the Harry Potter series', 'Fantasy', 0, 'AUDIO', 'https://example.com/harry-potter.jpg', NOW(), NOW()),
    ('Born a Crime', 'Trevor Noah', '978-0399588174', 19.99, 'Stories from a South African childhood', 'Biography', 0, 'AUDIO', 'https://example.com/born-crime.jpg', NOW(), NOW()),
    ('Sherlock Holmes: The Definitive Collection', 'Arthur Conan Doyle', '978-1511358156', 34.99, 'The complete Sherlock Holmes tales', 'Mystery', 0, 'AUDIO', 'https://example.com/sherlock.jpg', NOW(), NOW()),

    -- Classics
    ('Jane Eyre', 'Charlotte Bronte', '978-0141441146', 8.99, 'A classic novel about the experiences of a governess', 'Classics', 47, 'PHYSICAL', 'https://example.com/jane-eyre.jpg', NOW(), NOW()),
    ('Crime and Punishment', 'Fyodor Dostoevsky', '978-0143107637', 11.99, 'A novel about the mental anguish of a man who commits a murder', 'Classics', 32, 'PHYSICAL', 'https://example.com/crime-punishment.jpg', NOW(), NOW()),
    ('One Hundred Years of Solitude', 'Gabriel Garcia Marquez', '978-0060883287', 12.99, 'The multigenerational story of the Buendia family', 'Classics', 39, 'PHYSICAL', 'https://example.com/hundred-years.jpg', NOW(), NOW()),
    ('Brave New World', 'Aldous Huxley', '978-0060850524', 9.99, 'A dystopian novel set in a futuristic World State', 'Classics', 51, 'PHYSICAL', 'https://example.com/brave-new-world.jpg', NOW(), NOW()),

    -- Poetry
    ('Milk and Honey', 'Rupi Kaur', '978-1449474256', 11.99, 'A collection of poetry and prose about survival', 'Poetry', 43, 'PHYSICAL', 'https://example.com/milk-honey.jpg', NOW(), NOW()),
    ('The Sun and Her Flowers', 'Rupi Kaur', '978-1449486792', 12.99, 'A poetry collection about growth and healing', 'Poetry', 37, 'PHYSICAL', 'https://example.com/sun-flowers.jpg', NOW(), NOW()),
    ('The Odyssey', 'Homer', '978-0143039952', 14.99, 'One of the oldest works of literature, attributed to Homer', 'Poetry', 25, 'PHYSICAL', 'https://example.com/odyssey.jpg', NOW(), NOW()),
    ('The Iliad', 'Homer', '978-0140275360', 14.99, 'An ancient Greek epic poem set during the Trojan War', 'Poetry', 22, 'PHYSICAL', 'https://example.com/iliad.jpg', NOW(), NOW()),

    -- Historical Fiction
    ('All the Light We Cannot See', 'Anthony Doerr', '978-1501173219', 13.99, 'A novel about a blind French girl and a German boy in WWII', 'Historical Fiction', 49, 'PHYSICAL', 'https://example.com/all-light.jpg', NOW(), NOW()),
    ('The Book Thief', 'Markus Zusak', '978-0375842207', 12.99, 'A novel about a girl living with a foster family in Nazi Germany', 'Historical Fiction', 54, 'PHYSICAL', 'https://example.com/book-thief.jpg', NOW(), NOW()),
    ('Pachinko', 'Min Jin Lee', '978-1455563937', 15.99, 'A novel following four generations of a Korean family', 'Historical Fiction', 46, 'PHYSICAL', 'https://example.com/pachinko.jpg', NOW(), NOW()),
    ('Wolf Hall', 'Hilary Mantel', '978-0312429980', 14.99, 'A historical novel about the rise of Thomas Cromwell', 'Historical Fiction', 38, 'PHYSICAL', 'https://example.com/wolf-hall.jpg', NOW(), NOW()); 