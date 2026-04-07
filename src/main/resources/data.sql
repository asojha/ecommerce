-- ============================================================
-- Seed Products
-- ============================================================

-- Electronics
INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('iPhone 15 Pro', 'Latest Apple smartphone with A17 chip', 999.99, 'ELECTRONICS', 18, NULL, 'ALL', 'MEDIUM', 'https://example.com/iphone15pro.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Budget Android Phone', 'Affordable smartphone for everyday use', 199.99, 'ELECTRONICS', 13, NULL, 'ALL', 'LOW', 'https://example.com/budget-android.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Gaming Laptop', 'High-performance laptop for gaming and work', 1499.99, 'ELECTRONICS', 16, NULL, 'ALL', 'HIGH', 'https://example.com/gaming-laptop.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Kids Tablet', 'Safe and educational tablet for children', 149.99, 'ELECTRONICS', NULL, 12, 'ALL', 'LOW', 'https://example.com/kids-tablet.jpg', true);

-- Fashion
INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Designer Handbag', 'Premium leather handbag', 350.00, 'FASHION', 18, NULL, 'FEMALE', 'HIGH', 'https://example.com/handbag.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Men''s Casual T-Shirt', 'Comfortable everyday t-shirt', 29.99, 'FASHION', 16, NULL, 'MALE', 'LOW', 'https://example.com/tshirt.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Kids Summer Dress', 'Lightweight summer dress for girls', 24.99, 'FASHION', NULL, 12, 'FEMALE', 'LOW', 'https://example.com/kids-dress.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Luxury Watch', 'Swiss-made luxury timepiece', 2500.00, 'FASHION', 25, NULL, 'ALL', 'PREMIUM', 'https://example.com/luxury-watch.jpg', true);

-- Sports
INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Yoga Mat', 'Non-slip premium yoga mat', 45.00, 'SPORTS', 16, NULL, 'ALL', 'LOW', 'https://example.com/yoga-mat.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Mountain Bike', 'Trail-ready 27-speed mountain bike', 799.00, 'SPORTS', 14, NULL, 'ALL', 'MEDIUM', 'https://example.com/bike.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Kids Football Set', 'Football and goal net for kids', 39.99, 'SPORTS', NULL, 14, 'ALL', 'LOW', 'https://example.com/football-set.jpg', true);

-- Home & Kitchen
INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Air Fryer', 'Digital 5.8L air fryer for healthy cooking', 89.99, 'HOME_AND_KITCHEN', 18, NULL, 'ALL', 'LOW', 'https://example.com/airfryer.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Smart Home Hub', 'Central smart home controller', 249.99, 'HOME_AND_KITCHEN', 21, NULL, 'ALL', 'MEDIUM', 'https://example.com/smart-home.jpg', true);

-- Beauty
INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Skincare Starter Kit', 'All-in-one facial care set', 59.99, 'BEAUTY', 16, NULL, 'FEMALE', 'LOW', 'https://example.com/skincare.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Men''s Grooming Kit', 'Complete beard and skin care set', 49.99, 'BEAUTY', 18, NULL, 'MALE', 'LOW', 'https://example.com/grooming.jpg', true);

-- Books
INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Atomic Habits', 'Bestselling personal development book', 14.99, 'BOOKS', 16, NULL, 'ALL', 'LOW', 'https://example.com/atomic-habits.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Children''s Encyclopedia', 'Illustrated encyclopedia for curious kids', 34.99, 'BOOKS', NULL, 14, 'ALL', 'LOW', 'https://example.com/encyclopedia.jpg', true);

-- Health
INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Fitness Tracker', 'Smartwatch with heart rate and sleep monitoring', 129.99, 'HEALTH', 13, NULL, 'ALL', 'LOW', 'https://example.com/fitness-tracker.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Vitamin D Supplements', 'High-strength vitamin D3 capsules', 19.99, 'HEALTH', 18, NULL, 'ALL', 'LOW', 'https://example.com/vitamin-d.jpg', true);

-- Travel
INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Travel Insurance Premium', '12-month worldwide travel cover', 199.99, 'TRAVEL', 18, 70, 'ALL', 'MEDIUM', 'https://example.com/travel-insurance.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Luxury Resort Package', '5-star Maldives holiday package', 3999.00, 'TRAVEL', 21, NULL, 'ALL', 'PREMIUM', 'https://example.com/resort.jpg', true);

-- Finance
INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Premium Credit Card', 'Zero-fee international credit card with rewards', 0.00, 'FINANCE', 21, NULL, 'ALL', 'MEDIUM', 'https://example.com/credit-card.jpg', true);

INSERT INTO products (name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('Investment Portfolio Starter', 'Managed investment portfolio from $500', 0.00, 'FINANCE', 18, NULL, 'ALL', 'MEDIUM', 'https://example.com/investment.jpg', true);

-- ============================================================
-- Product Tags
-- ============================================================
INSERT INTO product_tags (product_id, tag) VALUES (1, 'ELECTRONICS');
INSERT INTO product_tags (product_id, tag) VALUES (1, 'tech');
INSERT INTO product_tags (product_id, tag) VALUES (1, 'premium');

INSERT INTO product_tags (product_id, tag) VALUES (2, 'ELECTRONICS');
INSERT INTO product_tags (product_id, tag) VALUES (2, 'budget');

INSERT INTO product_tags (product_id, tag) VALUES (3, 'ELECTRONICS');
INSERT INTO product_tags (product_id, tag) VALUES (3, 'gaming');

INSERT INTO product_tags (product_id, tag) VALUES (4, 'ELECTRONICS');
INSERT INTO product_tags (product_id, tag) VALUES (4, 'TOYS');
INSERT INTO product_tags (product_id, tag) VALUES (4, 'kids');

INSERT INTO product_tags (product_id, tag) VALUES (5, 'FASHION');
INSERT INTO product_tags (product_id, tag) VALUES (5, 'luxury');

INSERT INTO product_tags (product_id, tag) VALUES (6, 'FASHION');
INSERT INTO product_tags (product_id, tag) VALUES (6, 'casual');

INSERT INTO product_tags (product_id, tag) VALUES (7, 'FASHION');
INSERT INTO product_tags (product_id, tag) VALUES (7, 'kids');

INSERT INTO product_tags (product_id, tag) VALUES (8, 'FASHION');
INSERT INTO product_tags (product_id, tag) VALUES (8, 'luxury');

INSERT INTO product_tags (product_id, tag) VALUES (9, 'SPORTS');
INSERT INTO product_tags (product_id, tag) VALUES (9, 'fitness');

INSERT INTO product_tags (product_id, tag) VALUES (10, 'SPORTS');
INSERT INTO product_tags (product_id, tag) VALUES (10, 'outdoor');

INSERT INTO product_tags (product_id, tag) VALUES (11, 'SPORTS');
INSERT INTO product_tags (product_id, tag) VALUES (11, 'TOYS');
INSERT INTO product_tags (product_id, tag) VALUES (11, 'kids');

INSERT INTO product_tags (product_id, tag) VALUES (12, 'HOME_AND_KITCHEN');
INSERT INTO product_tags (product_id, tag) VALUES (12, 'cooking');

INSERT INTO product_tags (product_id, tag) VALUES (13, 'HOME_AND_KITCHEN');
INSERT INTO product_tags (product_id, tag) VALUES (13, 'smart');

INSERT INTO product_tags (product_id, tag) VALUES (14, 'BEAUTY');
INSERT INTO product_tags (product_id, tag) VALUES (14, 'skincare');

INSERT INTO product_tags (product_id, tag) VALUES (15, 'BEAUTY');
INSERT INTO product_tags (product_id, tag) VALUES (15, 'grooming');

INSERT INTO product_tags (product_id, tag) VALUES (16, 'BOOKS');
INSERT INTO product_tags (product_id, tag) VALUES (16, 'self-help');

INSERT INTO product_tags (product_id, tag) VALUES (17, 'BOOKS');
INSERT INTO product_tags (product_id, tag) VALUES (17, 'kids');

INSERT INTO product_tags (product_id, tag) VALUES (18, 'HEALTH');
INSERT INTO product_tags (product_id, tag) VALUES (18, 'fitness');

INSERT INTO product_tags (product_id, tag) VALUES (19, 'HEALTH');
INSERT INTO product_tags (product_id, tag) VALUES (19, 'wellness');

INSERT INTO product_tags (product_id, tag) VALUES (20, 'TRAVEL');
INSERT INTO product_tags (product_id, tag) VALUES (20, 'insurance');

INSERT INTO product_tags (product_id, tag) VALUES (21, 'TRAVEL');
INSERT INTO product_tags (product_id, tag) VALUES (21, 'luxury');

INSERT INTO product_tags (product_id, tag) VALUES (22, 'FINANCE');
INSERT INTO product_tags (product_id, tag) VALUES (22, 'credit');

INSERT INTO product_tags (product_id, tag) VALUES (23, 'FINANCE');
INSERT INTO product_tags (product_id, tag) VALUES (23, 'investment');
