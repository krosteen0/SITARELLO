-- Sample data for Sitarello marketplace
-- Execute this script to populate your database with example products

-- Clear existing data (ATTENZIONE: questo cancellerà tutti i dati esistenti!)
DELETE FROM rating;
DELETE FROM product_image;
DELETE FROM product;
DELETE FROM users;
DELETE FROM category;

-- Insert sample categories with explicit IDs
INSERT INTO category (id, name, icon_class) VALUES 
(1, 'Elettronica', 'fas fa-laptop'),
(2, 'Abbigliamento', 'fas fa-tshirt'),
(3, 'Casa e Giardino', 'fas fa-home'),
(4, 'Sport e Tempo Libero', 'fas fa-futbol'),
(5, 'Libri e Media', 'fas fa-book'),
(6, 'Automotive', 'fas fa-car'),
(7, 'Salute e Bellezza', 'fas fa-heartbeat'),
(8, 'Giocattoli', 'fas fa-gamepad');

-- Insert sample users (sellers) with explicit IDs
INSERT INTO users (id, username, password, email, role) VALUES 
(1, 'techstore', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'tech@example.com', 'USER'),
(2, 'fashionista', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'fashion@example.com', 'USER'),
(3, 'homegarden', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'home@example.com', 'USER'),
(4, 'sportshop', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'sport@example.com', 'USER'),
(5, 'bookworm', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'books@example.com', 'USER'),
(6, 'autoparts', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'auto@example.com', 'USER'),
(7, 'beautyexpert', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'beauty@example.com', 'USER'),
(8, 'toysrus', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'toys@example.com', 'USER');

-- Insert sample products with explicit references
INSERT INTO product (id, name, description, price, category_id, seller_id) VALUES 
-- Elettronica (category_id: 1, seller_id: 1)
(1, 'iPhone 15 Pro', 'Ultimo modello Apple con chip A17 Pro, fotocamera avanzata e design in titanio. Perfetto per foto professionali e prestazioni elevate.', 1199.99, 1, 1),
(2, 'MacBook Air M3', 'Laptop ultraleggero con chip M3, 16GB RAM e SSD da 512GB. Ideale per lavoro e creatività con autonomia di 18 ore.', 1399.99, 1, 1),
(3, 'Samsung Galaxy S24', 'Smartphone Android flagship con fotocamera da 200MP, display Dynamic AMOLED 2X e prestazioni AI avanzate.', 899.99, 1, 1),
(4, 'iPad Pro 12.9"', 'Tablet professionale con chip M2, display Liquid Retina XDR e supporto Apple Pencil. Perfetto per designer e artisti.', 1099.99, 1, 1),
(5, 'AirPods Pro 2', 'Auricolari wireless con cancellazione attiva del rumore, audio spaziale e custodia MagSafe. Qualità audio superiore.', 279.99, 1, 1),

-- Abbigliamento (category_id: 2, seller_id: 2)
(6, 'Giacca Pelle Vintage', 'Giacca in vera pelle bovina, stile motociclista vintage. Fodera interna e tasche multiple. Disponibile in nero e marrone.', 189.99, 2, 2),
(7, 'Jeans Skinny Premium', 'Jeans skinny fit in denim stretch premium, vestibilità perfetta e comfort superiore. Lavaggio stone washed moderno.', 79.99, 2, 2),
(8, 'Sneakers Limited Edition', 'Sneakers esclusive in edizione limitata, suola ammortizzata e design urbano. Perfette per outfit casual e sportivi.', 149.99, 2, 2),
(9, 'Vestito Elegante Sera', 'Abito lungo da sera in chiffon con ricami dorati, perfetto per eventi speciali e cerimonie. Taglie disponibili S-XL.', 249.99, 2, 2),
(10, 'Maglione Cashmere', 'Maglione in puro cashmere, morbidezza eccezionale e calore naturale. Disponibile in 5 colori classici.', 159.99, 2, 2),

-- Casa e Giardino (category_id: 3, seller_id: 3)
(11, 'Set Pentole Antiaderenti', 'Set completo 12 pezzi in alluminio forgiato con rivestimento antiaderente. Include padelle, pentole e coperchi.', 129.99, 3, 3),
(12, 'Aspirapolvere Robot', 'Robot aspirapolvere intelligente con mappatura laser, controllo app e svuotamento automatico. Ideale per case moderne.', 399.99, 3, 3),
(13, 'Divano 3 Posti Moderno', 'Divano in tessuto premium con struttura in legno massello. Design scandinavo, disponibile in grigio e beige.', 699.99, 3, 3),
(14, 'Set Attrezzi Giardinaggio', 'Kit completo per giardinaggio con 15 attrezzi professionali, vanga, rastrello e annaffiatoio. Manici ergonomici.', 89.99, 3, 3),
(15, 'Lampada LED Smart', 'Lampada da tavolo LED con controllo vocale, 16 milioni di colori e timer programmabile. Compatibile Alexa e Google.', 79.99, 3, 3),

-- Sport e Tempo Libero (category_id: 4, seller_id: 4)
(16, 'Bicicletta Mountain Bike', 'MTB professionale con telaio in carbonio, sospensioni full e cambio Shimano 21 velocità. Per trail e cross-country.', 899.99, 4, 4),
(17, 'Tapis Roulant Elettrico', 'Tapis roulant pieghevole con motore 2.5HP, velocità max 16 km/h e programmi preimpostati. Display LCD touch.', 649.99, 4, 4),
(18, 'Set Pesi Regolabili', 'Set manubri regolabili da 5 a 50kg, sistema di regolazione rapida e supporto incluso. Perfetti per home gym.', 299.99, 4, 4),
(19, 'Tenda Campeggio 4 Posti', 'Tenda impermeabile per 4 persone con doppio tetto e zanzariera. Montaggio facile in 10 minuti, peso 4.2kg.', 149.99, 4, 4),
(20, 'Kayak Gonfiabile', 'Kayak gonfiabile per 2 persone, materiale PVC resistente e pagaie incluse. Perfetto per laghi e fiumi tranquilli.', 199.99, 4, 4),

-- Libri e Media (category_id: 5, seller_id: 5)
(21, 'Il Nome della Rosa - Umberto Eco', 'Capolavoro della letteratura italiana, romanzo storico ambientato in un monastero medievale. Edizione rilegata di pregio.', 24.99, 5, 5),
(22, 'Corso Chitarra Completo', 'Metodo completo per imparare la chitarra con CD audio incluso. 200 pagine con spartiti, tablature e esercizi progressivi.', 34.99, 5, 5),
(23, 'Atlas Geografico Mondiale', 'Atlante geografico aggiornato con mappe dettagliate, dati demografici e informazioni climatiche. Formato grande 30x40cm.', 49.99, 5, 5),
(24, 'Box Set Harry Potter', 'Collezione completa dei 7 libri di Harry Potter in cofanetto regalo. Edizione italiana con copertine originali.', 89.99, 5, 5),
(25, 'Vinile Pink Floyd - The Wall', 'Album doppio su vinile 180g, rimasterizzato digitalmente. Include booklet con testi e foto. Condizioni mint.', 39.99, 5, 5),

-- Automotive (category_id: 6, seller_id: 6)
(26, 'Set Pneumatici Estivi', 'Set 4 pneumatici estivi 205/55 R16, marca premium con tecnologia anti-aquaplaning. Montaggio e bilanciatura inclusi.', 299.99, 6, 6),
(27, 'Navigatore GPS Auto', 'Navigatore GPS 7" con mappe Europa aggiornate, bluetooth e dashcam integrata. Aggiornamenti mappe gratuiti a vita.', 149.99, 6, 6),
(28, 'Kit Tagliando Auto', 'Kit completo per tagliando: olio motore 5W30, filtri aria/olio/carburante e candele. Compatibile con la maggior parte delle auto.', 79.99, 6, 6),
(29, 'Coprisedili Universali', 'Set coprisedili universali in ecopelle nera con cuciture rosse. Facile installazione e compatibili con airbag laterali.', 59.99, 6, 6),
(30, 'Caricabatterie Auto 12V', 'Caricabatterie intelligente per batterie auto 12V, riparazione automatica e protezione da sovraccarico. Display LCD.', 89.99, 6, 6),

-- Salute e Bellezza (category_id: 7, seller_id: 7)
(31, 'Crema Viso Anti-Età', 'Crema viso premium con acido ialuronico e vitamina C. Riduce rughe e macchie, texture leggera non grassa. 50ml.', 49.99, 7, 7),
(32, 'Spazzolino Elettrico', 'Spazzolino elettrico sonico con 5 modalità di pulizia, timer e custodia da viaggio. Batteria autonomia 3 settimane.', 79.99, 7, 7),
(33, 'Bilancia Smart', 'Bilancia digitale con analisi corporea completa: peso, massa grassa, muscolare e idratazione. App smartphone inclusa.', 39.99, 7, 7),
(34, 'Set Trucchi Professionale', 'Kit makeup completo con 40 prodotti: ombretti, rossetti, fondotinta e pennelli. Perfetto per makeup artist o principianti.', 89.99, 7, 7),
(35, 'Diffusore Oli Essenziali', 'Diffusore ultrasonico con luci LED, timer e spegnimento automatico. Include set 6 oli essenziali puri. Capacità 300ml.', 34.99, 7, 7),

-- Giocattoli (category_id: 8, seller_id: 8)
(36, 'LEGO Creator Expert', 'Set LEGO Creator Expert Taj Mahal con 5923 pezzi. Costruzione avanzata per adulti, dimensioni 51x41cm una volta completato.', 399.99, 8, 8),
(37, 'Drone per Bambini', 'Drone facile da pilotare per bambini 8+, controllo remoto semplice e protezioni per le eliche. Volo stabile e sicuro.', 79.99, 8, 8),
(38, 'Puzzle 2000 Pezzi', 'Puzzle panoramico 2000 pezzi raffigurante il Colosseo romano. Dimensioni finite 98x33cm, qualità cartone premium.', 24.99, 8, 8),
(39, 'Bambola Interattiva', 'Bambola che parla e canta, riconosce la voce e risponde a oltre 100 frasi. Include accessori e vestitini intercambiabili.', 59.99, 8, 8),
(40, 'Set Macchinine Vintage', 'Collezione 12 auto d''epoca in metallo scala 1:64, modelli storici dettagliati dal 1950 al 1980. Con espositore incluso.', 49.99, 8, 8);

-- Insert some sample ratings for products (using direct ID references)
INSERT INTO rating (id, value, product_id, user_id) VALUES 
-- Ratings for products with explicit IDs
(1, 5, 1, 2), (2, 4, 1, 3), (3, 5, 1, 4),   -- iPhone 15 Pro ratings
(4, 4, 2, 1), (5, 5, 2, 3), (6, 4, 2, 5),   -- MacBook Air M3 ratings  
(7, 5, 3, 2), (8, 5, 3, 4), (9, 4, 3, 6),   -- Samsung Galaxy S24 ratings
(10, 5, 6, 1), (11, 4, 6, 3), (12, 5, 6, 7), -- Giacca Pelle Vintage ratings
(13, 4, 11, 2), (14, 5, 11, 4), (15, 4, 11, 8), -- Set Pentole Antiaderenti ratings
(16, 5, 16, 1), (17, 4, 16, 3), (18, 5, 16, 5), -- Bicicletta Mountain Bike ratings
(19, 5, 21, 2), (20, 4, 21, 6), (21, 5, 21, 8), -- Il Nome della Rosa ratings
(22, 4, 26, 1), (23, 5, 26, 4), (24, 4, 26, 7), -- Set Pneumatici Estivi ratings
(25, 5, 31, 2), (26, 4, 31, 5), (27, 5, 31, 8), -- Crema Viso Anti-Età ratings
(28, 4, 36, 1), (29, 5, 36, 3), (30, 4, 36, 6); -- LEGO Creator Expert ratings

-- Display summary
SELECT 'Sample data inserted successfully!' as message;
SELECT 'Categories: ' || COUNT(*) as categories FROM category;
SELECT 'Users: ' || COUNT(*) as users FROM users;
SELECT 'Products: ' || COUNT(*) as products FROM product;
SELECT 'Ratings: ' || COUNT(*) as ratings FROM rating;
