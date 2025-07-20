-- Sample data for Sitarello marketplace (VERSIONE SICURA)
-- Execute this script to add sample products without deleting existing data

-- Insert sample categories (ignora se esistono già)
INSERT INTO category (name, icon_class) VALUES 
('Elettronica', 'fas fa-laptop'),
('Abbigliamento', 'fas fa-tshirt'),
('Casa e Giardino', 'fas fa-home'),
('Sport e Tempo Libero', 'fas fa-futbol'),
('Libri e Media', 'fas fa-book'),
('Automotive', 'fas fa-car'),
('Salute e Bellezza', 'fas fa-heartbeat'),
('Giocattoli', 'fas fa-gamepad')
ON CONFLICT (name) DO NOTHING;

-- Insert sample users (sellers) (ignora se esistono già)
INSERT INTO users (username, password, email, role) VALUES 
('techstore', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'tech@example.com', 'USER'),
('fashionista', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'fashion@example.com', 'USER'),
('homegarden', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'home@example.com', 'USER'),
('sportshop', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'sport@example.com', 'USER'),
('bookworm', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'books@example.com', 'USER'),
('autoparts', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'auto@example.com', 'USER'),
('beautyexpert', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'beauty@example.com', 'USER'),
('toysrus', '$2a$10$8K1p/o/MN6nYhCQh5OdIbOJx6IxEZfIzZqxKzXL8E1q0RYZq2ZD2e', 'toys@example.com', 'USER')
ON CONFLICT (username) DO NOTHING;

-- Insert sample products usando le foreign key dinamiche
INSERT INTO product (name, description, price, category_id, seller_id) VALUES 
-- Elettronica
('iPhone 15 Pro', 'Ultimo modello Apple con chip A17 Pro, fotocamera avanzata e design in titanio. Perfetto per foto professionali e prestazioni elevate.', 1199.99, 
 (SELECT id FROM category WHERE name = 'Elettronica'), (SELECT id FROM users WHERE username = 'techstore')),
('MacBook Air M3', 'Laptop ultraleggero con chip M3, 16GB RAM e SSD da 512GB. Ideale per lavoro e creatività con autonomia di 18 ore.', 1399.99, 
 (SELECT id FROM category WHERE name = 'Elettronica'), (SELECT id FROM users WHERE username = 'techstore')),
('Samsung Galaxy S24', 'Smartphone Android flagship con fotocamera da 200MP, display Dynamic AMOLED 2X e prestazioni AI avanzate.', 899.99, 
 (SELECT id FROM category WHERE name = 'Elettronica'), (SELECT id FROM users WHERE username = 'techstore')),
('iPad Pro 12.9"', 'Tablet professionale con chip M2, display Liquid Retina XDR e supporto Apple Pencil. Perfetto per designer e artisti.', 1099.99, 
 (SELECT id FROM category WHERE name = 'Elettronica'), (SELECT id FROM users WHERE username = 'techstore')),
('AirPods Pro 2', 'Auricolari wireless con cancellazione attiva del rumore, audio spaziale e custodia MagSafe. Qualità audio superiore.', 279.99, 
 (SELECT id FROM category WHERE name = 'Elettronica'), (SELECT id FROM users WHERE username = 'techstore')),

-- Abbigliamento
('Giacca Pelle Vintage', 'Giacca in vera pelle bovina, stile motociclista vintage. Fodera interna e tasche multiple. Disponibile in nero e marrone.', 189.99, 
 (SELECT id FROM category WHERE name = 'Abbigliamento'), (SELECT id FROM users WHERE username = 'fashionista')),
('Jeans Skinny Premium', 'Jeans skinny fit in denim stretch premium, vestibilità perfetta e comfort superiore. Lavaggio stone washed moderno.', 79.99, 
 (SELECT id FROM category WHERE name = 'Abbigliamento'), (SELECT id FROM users WHERE username = 'fashionista')),
('Sneakers Limited Edition', 'Sneakers esclusive in edizione limitata, suola ammortizzata e design urbano. Perfette per outfit casual e sportivi.', 149.99, 
 (SELECT id FROM category WHERE name = 'Abbigliamento'), (SELECT id FROM users WHERE username = 'fashionista')),
('Vestito Elegante Sera', 'Abito lungo da sera in chiffon con ricami dorati, perfetto per eventi speciali e cerimonie. Taglie disponibili S-XL.', 249.99, 
 (SELECT id FROM category WHERE name = 'Abbigliamento'), (SELECT id FROM users WHERE username = 'fashionista')),
('Maglione Cashmere', 'Maglione in puro cashmere, morbidezza eccezionale e calore naturale. Disponibile in 5 colori classici.', 159.99, 
 (SELECT id FROM category WHERE name = 'Abbigliamento'), (SELECT id FROM users WHERE username = 'fashionista')),

-- Casa e Giardino
('Set Pentole Antiaderenti', 'Set completo 12 pezzi in alluminio forgiato con rivestimento antiaderente. Include padelle, pentole e coperchi.', 129.99, 
 (SELECT id FROM category WHERE name = 'Casa e Giardino'), (SELECT id FROM users WHERE username = 'homegarden')),
('Aspirapolvere Robot', 'Robot aspirapolvere intelligente con mappatura laser, controllo app e svuotamento automatico. Ideale per case moderne.', 399.99, 
 (SELECT id FROM category WHERE name = 'Casa e Giardino'), (SELECT id FROM users WHERE username = 'homegarden')),
('Divano 3 Posti Moderno', 'Divano in tessuto premium con struttura in legno massello. Design scandinavo, disponibile in grigio e beige.', 699.99, 
 (SELECT id FROM category WHERE name = 'Casa e Giardino'), (SELECT id FROM users WHERE username = 'homegarden')),
('Set Attrezzi Giardinaggio', 'Kit completo per giardinaggio con 15 attrezzi professionali, vanga, rastrello e annaffiatoio. Manici ergonomici.', 89.99, 
 (SELECT id FROM category WHERE name = 'Casa e Giardino'), (SELECT id FROM users WHERE username = 'homegarden')),
('Lampada LED Smart', 'Lampada da tavolo LED con controllo vocale, 16 milioni di colori e timer programmabile. Compatibile Alexa e Google.', 79.99, 
 (SELECT id FROM category WHERE name = 'Casa e Giardino'), (SELECT id FROM users WHERE username = 'homegarden')),

-- Sport e Tempo Libero
('Bicicletta Mountain Bike', 'MTB professionale con telaio in carbonio, sospensioni full e cambio Shimano 21 velocità. Per trail e cross-country.', 899.99, 
 (SELECT id FROM category WHERE name = 'Sport e Tempo Libero'), (SELECT id FROM users WHERE username = 'sportshop')),
('Tapis Roulant Elettrico', 'Tapis roulant pieghevole con motore 2.5HP, velocità max 16 km/h e programmi preimpostati. Display LCD touch.', 649.99, 
 (SELECT id FROM category WHERE name = 'Sport e Tempo Libero'), (SELECT id FROM users WHERE username = 'sportshop')),
('Set Pesi Regolabili', 'Set manubri regolabili da 5 a 50kg, sistema di regolazione rapida e supporto incluso. Perfetti per home gym.', 299.99, 
 (SELECT id FROM category WHERE name = 'Sport e Tempo Libero'), (SELECT id FROM users WHERE username = 'sportshop')),
('Tenda Campeggio 4 Posti', 'Tenda impermeabile per 4 persone con doppio tetto e zanzariera. Montaggio facile in 10 minuti, peso 4.2kg.', 149.99, 
 (SELECT id FROM category WHERE name = 'Sport e Tempo Libero'), (SELECT id FROM users WHERE username = 'sportshop')),
('Kayak Gonfiabile', 'Kayak gonfiabile per 2 persone, materiale PVC resistente e pagaie incluse. Perfetto per laghi e fiumi tranquilli.', 199.99, 
 (SELECT id FROM category WHERE name = 'Sport e Tempo Libero'), (SELECT id FROM users WHERE username = 'sportshop')),

-- Libri e Media
('Il Nome della Rosa - Umberto Eco', 'Capolavoro della letteratura italiana, romanzo storico ambientato in un monastero medievale. Edizione rilegata di pregio.', 24.99, 
 (SELECT id FROM category WHERE name = 'Libri e Media'), (SELECT id FROM users WHERE username = 'bookworm')),
('Corso Chitarra Completo', 'Metodo completo per imparare la chitarra con CD audio incluso. 200 pagine con spartiti, tablature e esercizi progressivi.', 34.99, 
 (SELECT id FROM category WHERE name = 'Libri e Media'), (SELECT id FROM users WHERE username = 'bookworm')),
('Atlas Geografico Mondiale', 'Atlante geografico aggiornato con mappe dettagliate, dati demografici e informazioni climatiche. Formato grande 30x40cm.', 49.99, 
 (SELECT id FROM category WHERE name = 'Libri e Media'), (SELECT id FROM users WHERE username = 'bookworm')),
('Box Set Harry Potter', 'Collezione completa dei 7 libri di Harry Potter in cofanetto regalo. Edizione italiana con copertine originali.', 89.99, 
 (SELECT id FROM category WHERE name = 'Libri e Media'), (SELECT id FROM users WHERE username = 'bookworm')),
('Vinile Pink Floyd - The Wall', 'Album doppio su vinile 180g, rimasterizzato digitalmente. Include booklet con testi e foto. Condizioni mint.', 39.99, 
 (SELECT id FROM category WHERE name = 'Libri e Media'), (SELECT id FROM users WHERE username = 'bookworm')),

-- Automotive
('Set Pneumatici Estivi', 'Set 4 pneumatici estivi 205/55 R16, marca premium con tecnologia anti-aquaplaning. Montaggio e bilanciatura inclusi.', 299.99, 
 (SELECT id FROM category WHERE name = 'Automotive'), (SELECT id FROM users WHERE username = 'autoparts')),
('Navigatore GPS Auto', 'Navigatore GPS 7" con mappe Europa aggiornate, bluetooth e dashcam integrata. Aggiornamenti mappe gratuiti a vita.', 149.99, 
 (SELECT id FROM category WHERE name = 'Automotive'), (SELECT id FROM users WHERE username = 'autoparts')),
('Kit Tagliando Auto', 'Kit completo per tagliando: olio motore 5W30, filtri aria/olio/carburante e candele. Compatibile con la maggior parte delle auto.', 79.99, 
 (SELECT id FROM category WHERE name = 'Automotive'), (SELECT id FROM users WHERE username = 'autoparts')),
('Coprisedili Universali', 'Set coprisedili universali in ecopelle nera con cuciture rosse. Facile installazione e compatibili con airbag laterali.', 59.99, 
 (SELECT id FROM category WHERE name = 'Automotive'), (SELECT id FROM users WHERE username = 'autoparts')),
('Caricabatterie Auto 12V', 'Caricabatterie intelligente per batterie auto 12V, riparazione automatica e protezione da sovraccarico. Display LCD.', 89.99, 
 (SELECT id FROM category WHERE name = 'Automotive'), (SELECT id FROM users WHERE username = 'autoparts')),

-- Salute e Bellezza
('Crema Viso Anti-Età', 'Crema viso premium con acido ialuronico e vitamina C. Riduce rughe e macchie, texture leggera non grassa. 50ml.', 49.99, 
 (SELECT id FROM category WHERE name = 'Salute e Bellezza'), (SELECT id FROM users WHERE username = 'beautyexpert')),
('Spazzolino Elettrico', 'Spazzolino elettrico sonico con 5 modalità di pulizia, timer e custodia da viaggio. Batteria autonomia 3 settimane.', 79.99, 
 (SELECT id FROM category WHERE name = 'Salute e Bellezza'), (SELECT id FROM users WHERE username = 'beautyexpert')),
('Bilancia Smart', 'Bilancia digitale con analisi corporea completa: peso, massa grassa, muscolare e idratazione. App smartphone inclusa.', 39.99, 
 (SELECT id FROM category WHERE name = 'Salute e Bellezza'), (SELECT id FROM users WHERE username = 'beautyexpert')),
('Set Trucchi Professionale', 'Kit makeup completo con 40 prodotti: ombretti, rossetti, fondotinta e pennelli. Perfetto per makeup artist o principianti.', 89.99, 
 (SELECT id FROM category WHERE name = 'Salute e Bellezza'), (SELECT id FROM users WHERE username = 'beautyexpert')),
('Diffusore Oli Essenziali', 'Diffusore ultrasonico con luci LED, timer e spegnimento automatico. Include set 6 oli essenziali puri. Capacità 300ml.', 34.99, 
 (SELECT id FROM category WHERE name = 'Salute e Bellezza'), (SELECT id FROM users WHERE username = 'beautyexpert')),

-- Giocattoli
('LEGO Creator Expert', 'Set LEGO Creator Expert Taj Mahal con 5923 pezzi. Costruzione avanzata per adulti, dimensioni 51x41cm una volta completato.', 399.99, 
 (SELECT id FROM category WHERE name = 'Giocattoli'), (SELECT id FROM users WHERE username = 'toysrus')),
('Drone per Bambini', 'Drone facile da pilotare per bambini 8+, controllo remoto semplice e protezioni per le eliche. Volo stabile e sicuro.', 79.99, 
 (SELECT id FROM category WHERE name = 'Giocattoli'), (SELECT id FROM users WHERE username = 'toysrus')),
('Puzzle 2000 Pezzi', 'Puzzle panoramico 2000 pezzi raffigurante il Colosseo romano. Dimensioni finite 98x33cm, qualità cartone premium.', 24.99, 
 (SELECT id FROM category WHERE name = 'Giocattoli'), (SELECT id FROM users WHERE username = 'toysrus')),
('Bambola Interattiva', 'Bambola che parla e canta, riconosce la voce e risponde a oltre 100 frasi. Include accessori e vestitini intercambiabili.', 59.99, 
 (SELECT id FROM category WHERE name = 'Giocattoli'), (SELECT id FROM users WHERE username = 'toysrus')),
('Set Macchinine Vintage', 'Collezione 12 auto d''epoca in metallo scala 1:64, modelli storici dettagliati dal 1950 al 1980. Con espositore incluso.', 49.99, 
 (SELECT id FROM category WHERE name = 'Giocattoli'), (SELECT id FROM users WHERE username = 'toysrus'));

-- Display summary
SELECT 'Sample data inserted successfully!' as message;
SELECT 'Categories: ' || COUNT(*) as categories FROM category;
SELECT 'Users: ' || COUNT(*) as users FROM users;
SELECT 'Products: ' || COUNT(*) as products FROM product;
