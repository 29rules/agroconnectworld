-- =============================================================
-- AgroConnectWorld Seed Data
-- Run this after all services start and schemas are created
-- Usage: docker exec -i postgres_service psql -U agro -d agro_master < seed.sql
-- =============================================================

-- ── Suppliers ────────────────────────────────────────────────
SET search_path TO supplier_service;

INSERT INTO supplier (id, name, email, phone, address, country, description, verified, created_at)
VALUES
  (gen_random_uuid(), 'GreenHarvest Co.', 'contact@greenharvestco.com', '+1-555-0101', '123 Farm Lane, Iowa', 'USA', 'Premium grain and cereal supplier with 20 years of experience.', true, NOW()),
  (gen_random_uuid(), 'OrganicRoots Ltd.', 'info@organicroots.co.uk', '+44-20-5550-0102', '45 Organic Way, Devon', 'UK', 'Certified organic vegetables and fruits direct from farm.', true, NOW()),
  (gen_random_uuid(), 'AgroFresh India', 'sales@agrofreshindia.in', '+91-98765-43210', 'Plot 12, Agro Hub, Pune', 'India', 'Spices, pulses, and rice exports with ISO certification.', true, NOW()),
  (gen_random_uuid(), 'PampaSoy Brazil', 'export@pampasoy.com.br', '+55-11-5550-0104', 'Rua Agro 88, São Paulo', 'Brazil', 'South Americas largest soybean and corn exporter.', true, NOW()),
  (gen_random_uuid(), 'NorthernGrain Canada', 'info@northerngrain.ca', '+1-204-555-0105', '789 Prairie Road, Manitoba', 'Canada', 'Premium canola oil and wheat supplier serving global markets.', true, NOW())
ON CONFLICT DO NOTHING;

-- ── Products ─────────────────────────────────────────────────
SET search_path TO product_service;

INSERT INTO product (id, name, description, price, category, unit, min_order_quantity, stock_quantity, supplier_id, available, created_at)
VALUES
  (gen_random_uuid(), 'Organic Wheat (Hard Red)', 'Premium hard red winter wheat, protein 13%+, moisture <13%. Ideal for bread flour.', 285.00, 'Grains & Cereals', 'per metric ton', 10, 500, NULL, true, NOW()),
  (gen_random_uuid(), 'Non-GMO Soybeans', 'High-protein non-GMO soybeans. IP certified, protein 34%+. Bulk export available.', 520.00, 'Oilseeds', 'per metric ton', 20, 1200, NULL, true, NOW()),
  (gen_random_uuid(), 'Basmati Rice (Extra Long)', 'Extra long grain basmati rice from Punjab region. Aged 1 year, aromatic, export quality.', 1100.00, 'Rice', 'per metric ton', 5, 800, NULL, true, NOW()),
  (gen_random_uuid(), 'Organic Turmeric Powder', 'Certified organic turmeric powder, curcumin content 3%+. USDA and EU organic certified.', 2200.00, 'Spices', 'per metric ton', 1, 200, NULL, true, NOW()),
  (gen_random_uuid(), 'Yellow Corn (Grade 2)', 'US Grade 2 yellow corn. Moisture 14% max, test weight 54 lbs/bu. Perfect for animal feed and industrial use.', 240.00, 'Grains & Cereals', 'per metric ton', 25, 3000, NULL, true, NOW()),
  (gen_random_uuid(), 'Cold-Pressed Canola Oil', 'Premium cold-pressed canola oil, non-GMO, low erucic acid. Food-grade bulk packaging.', 1450.00, 'Edible Oils', 'per metric ton', 5, 400, NULL, true, NOW()),
  (gen_random_uuid(), 'Organic Black Pepper', 'Malabar organic black pepper, ASTA 600+ color, moisture <11%. Fair-trade certified.', 4800.00, 'Spices', 'per metric ton', 0.5, 80, NULL, true, NOW()),
  (gen_random_uuid(), 'Chickpeas (Kabuli Type)', 'Premium Kabuli chickpeas, 9-10mm size, bright color. Origin: Australia. Protein 18%+.', 680.00, 'Pulses & Legumes', 'per metric ton', 10, 600, NULL, true, NOW()),
  (gen_random_uuid(), 'Freeze-Dried Mango Slices', 'Premium freeze-dried Alphonso mango slices. No additives, 95% nutrition retention.', 8500.00, 'Processed Fruits', 'per metric ton', 0.1, 30, NULL, true, NOW()),
  (gen_random_uuid(), 'Sunflower Seeds (Oilseed)', 'Oilseed sunflower seeds, oil content 42%+, origin Ukraine. Bulk shipping available.', 420.00, 'Oilseeds', 'per metric ton', 20, 2000, NULL, true, NOW()),
  (gen_random_uuid(), 'Lentils (Green)', 'Canadian green lentils, Laird variety, protein 24%, moisture 13.5% max.', 580.00, 'Pulses & Legumes', 'per metric ton', 10, 900, NULL, true, NOW()),
  (gen_random_uuid(), 'Organic Cacao Beans', 'Single-origin organic cacao beans from Ecuador. Fermented and dried, Arriba variety.', 3200.00, 'Specialty Crops', 'per metric ton', 1, 150, NULL, true, NOW())
ON CONFLICT DO NOTHING;

-- ── Admin / Test Users (password = "Password123!") ───────────
-- BCrypt hash of "Password123!"
SET search_path TO auth_service;

INSERT INTO users (id, name, email, phone, password, role, created_at)
VALUES
  (gen_random_uuid(), 'Admin User',    'admin@agroconnect.com',    '+1-555-0001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lHHy', 'ADMIN',    NOW()),
  (gen_random_uuid(), 'CEO User',      'ceo@agroconnect.com',      '+1-555-0002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lHHy', 'CEO',      NOW()),
  (gen_random_uuid(), 'Demo Buyer',    'buyer@agroconnect.com',    '+1-555-0003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lHHy', 'BUYER',    NOW()),
  (gen_random_uuid(), 'Demo Supplier', 'supplier@agroconnect.com', '+1-555-0004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lHHy', 'SUPPLIER', NOW())
ON CONFLICT (email) DO NOTHING;

-- ── Done ─────────────────────────────────────────────────────
\echo '✅ Seed data loaded successfully!'
\echo 'Demo accounts (password = Password123!):'
\echo '  admin@agroconnect.com    → ADMIN'
\echo '  ceo@agroconnect.com      → CEO'
\echo '  buyer@agroconnect.com    → BUYER'
\echo '  supplier@agroconnect.com → SUPPLIER'
