-- ============================================================
-- Seed Products  (product_type: STANDARD | SUBSCRIPTION)
-- ============================================================

-- Electronics
INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'ELEC-IP15P', 'iPhone 15 Pro', 'Latest Apple smartphone with A17 chip', 999.99, 'ELECTRONICS', 18, NULL, 'ALL', 'MEDIUM', 'https://example.com/iphone15pro.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'ELEC-BNDRD', 'Budget Android Phone', 'Affordable smartphone for everyday use', 199.99, 'ELECTRONICS', 13, NULL, 'ALL', 'LOW', 'https://example.com/budget-android.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'ELEC-GMLAP', 'Gaming Laptop', 'High-performance laptop for gaming and work', 1499.99, 'ELECTRONICS', 16, NULL, 'ALL', 'HIGH', 'https://example.com/gaming-laptop.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'ELEC-KDTAB', 'Kids Tablet', 'Safe and educational tablet for children', 149.99, 'ELECTRONICS', NULL, 12, 'ALL', 'LOW', 'https://example.com/kids-tablet.jpg', true);

-- Fashion
INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'FASH-DHNBG', 'Designer Handbag', 'Premium leather handbag', 350.00, 'FASHION', 18, NULL, 'FEMALE', 'HIGH', 'https://example.com/handbag.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'FASH-MCTSH', 'Men''s Casual T-Shirt', 'Comfortable everyday t-shirt', 29.99, 'FASHION', 16, NULL, 'MALE', 'LOW', 'https://example.com/tshirt.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'FASH-KDRSS', 'Kids Summer Dress', 'Lightweight summer dress for girls', 24.99, 'FASHION', NULL, 12, 'FEMALE', 'LOW', 'https://example.com/kids-dress.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'FASH-LXWTC', 'Luxury Watch', 'Swiss-made luxury timepiece', 2500.00, 'FASHION', 25, NULL, 'ALL', 'PREMIUM', 'https://example.com/luxury-watch.jpg', true);

-- Sports
INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'SPRT-YGMAT', 'Yoga Mat', 'Non-slip premium yoga mat', 45.00, 'SPORTS', 16, NULL, 'ALL', 'LOW', 'https://example.com/yoga-mat.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'SPRT-MTBKE', 'Mountain Bike', 'Trail-ready 27-speed mountain bike', 799.00, 'SPORTS', 14, NULL, 'ALL', 'MEDIUM', 'https://example.com/bike.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'SPRT-KDFBL', 'Kids Football Set', 'Football and goal net for kids', 39.99, 'SPORTS', NULL, 14, 'ALL', 'LOW', 'https://example.com/football-set.jpg', true);

-- Home & Kitchen
INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'HOME-ARFRY', 'Air Fryer', 'Digital 5.8L air fryer for healthy cooking', 89.99, 'HOME_AND_KITCHEN', 18, NULL, 'ALL', 'LOW', 'https://example.com/airfryer.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'HOME-SMHUB', 'Smart Home Hub', 'Central smart home controller', 249.99, 'HOME_AND_KITCHEN', 21, NULL, 'ALL', 'MEDIUM', 'https://example.com/smart-home.jpg', true);

-- Beauty
INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'BEAU-SKCKT', 'Skincare Starter Kit', 'All-in-one facial care set', 59.99, 'BEAUTY', 16, NULL, 'FEMALE', 'LOW', 'https://example.com/skincare.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'BEAU-MNGKT', 'Men''s Grooming Kit', 'Complete beard and skin care set', 49.99, 'BEAUTY', 18, NULL, 'MALE', 'LOW', 'https://example.com/grooming.jpg', true);

-- Books
INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'BOOK-ATHBT', 'Atomic Habits', 'Bestselling personal development book', 14.99, 'BOOKS', 16, NULL, 'ALL', 'LOW', 'https://example.com/atomic-habits.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'BOOK-CHENC', 'Children''s Encyclopedia', 'Illustrated encyclopedia for curious kids', 34.99, 'BOOKS', NULL, 14, 'ALL', 'LOW', 'https://example.com/encyclopedia.jpg', true);

-- Health
INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'HLTH-FTRCK', 'Fitness Tracker', 'Smartwatch with heart rate and sleep monitoring', 129.99, 'HEALTH', 13, NULL, 'ALL', 'LOW', 'https://example.com/fitness-tracker.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'HLTH-VTMD3', 'Vitamin D Supplements', 'High-strength vitamin D3 capsules', 19.99, 'HEALTH', 18, NULL, 'ALL', 'LOW', 'https://example.com/vitamin-d.jpg', true);

-- Travel
INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'TRVL-INSUR', 'Travel Insurance Premium', '12-month worldwide travel cover', 199.99, 'TRAVEL', 18, 70, 'ALL', 'MEDIUM', 'https://example.com/travel-insurance.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'TRVL-MLDVS', 'Luxury Resort Package', '5-star Maldives holiday package', 3999.00, 'TRAVEL', 21, NULL, 'ALL', 'PREMIUM', 'https://example.com/resort.jpg', true);

-- Finance
INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'FINC-CCARD', 'Premium Credit Card', 'Zero-fee international credit card with rewards', 0.00, 'FINANCE', 21, NULL, 'ALL', 'MEDIUM', 'https://example.com/credit-card.jpg', true);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active)
VALUES ('STANDARD', 'FINC-INVST', 'Investment Portfolio Starter', 'Managed investment portfolio from $500', 0.00, 'FINANCE', 18, NULL, 'ALL', 'MEDIUM', 'https://example.com/investment.jpg', true);

-- ============================================================
-- Subscription Products
-- ============================================================
INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active, billing_cycle, trial_days)
VALUES ('SUBSCRIPTION', 'ELEC-MUSP', 'Music Streaming Service', 'Unlimited ad-free music streaming', 9.99, 'ELECTRONICS', 13, NULL, 'ALL', 'LOW', 'https://example.com/music-stream.jpg', true, 'MONTHLY', 30);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active, billing_cycle, trial_days)
VALUES ('SUBSCRIPTION', 'HLTH-GYMM', 'Gym Membership', 'Unlimited access to all gym facilities and classes', 49.99, 'HEALTH', 16, NULL, 'ALL', 'LOW', 'https://example.com/gym.jpg', true, 'MONTHLY', 7);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active, billing_cycle, trial_days)
VALUES ('SUBSCRIPTION', 'BOOK-KLIM', 'eBook Library', 'Unlimited access to over 1 million eBooks', 14.99, 'BOOKS', 13, NULL, 'ALL', 'LOW', 'https://example.com/ebook-library.jpg', true, 'MONTHLY', 30);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active, billing_cycle, trial_days)
VALUES ('SUBSCRIPTION', 'FINC-INVP', 'Managed Investment Plan', 'Professionally managed investment portfolio with monthly reporting', 29.99, 'FINANCE', 21, NULL, 'ALL', 'MEDIUM', 'https://example.com/invest-plan.jpg', true, 'MONTHLY', NULL);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active, billing_cycle, trial_days)
VALUES ('SUBSCRIPTION', 'HLTH-NUTR', 'Nutrition & Meal Plan', 'Weekly personalised meal plans with macro tracking', 19.99, 'HEALTH', 18, NULL, 'ALL', 'LOW', 'https://example.com/meal-plan.jpg', true, 'WEEKLY', 14);

INSERT INTO products (product_type, sku, name, description, price, category, min_age, max_age, target_gender, min_income_level, image_url, active, billing_cycle, trial_days)
VALUES ('SUBSCRIPTION', 'TRVL-LNGP', 'Airport Lounge Pass', 'Unlimited worldwide airport lounge access', 199.99, 'TRAVEL', 18, NULL, 'ALL', 'HIGH', 'https://example.com/lounge.jpg', true, 'ANNUAL', NULL);

-- ============================================================
-- Product Tags  (sku FK)
-- ============================================================
INSERT INTO product_tags (sku, tag) VALUES ('ELEC-IP15P', 'ELECTRONICS');
INSERT INTO product_tags (sku, tag) VALUES ('ELEC-IP15P', 'tech');
INSERT INTO product_tags (sku, tag) VALUES ('ELEC-IP15P', 'premium');

INSERT INTO product_tags (sku, tag) VALUES ('ELEC-BNDRD', 'ELECTRONICS');
INSERT INTO product_tags (sku, tag) VALUES ('ELEC-BNDRD', 'budget');

INSERT INTO product_tags (sku, tag) VALUES ('ELEC-GMLAP', 'ELECTRONICS');
INSERT INTO product_tags (sku, tag) VALUES ('ELEC-GMLAP', 'gaming');

INSERT INTO product_tags (sku, tag) VALUES ('ELEC-KDTAB', 'ELECTRONICS');
INSERT INTO product_tags (sku, tag) VALUES ('ELEC-KDTAB', 'TOYS');
INSERT INTO product_tags (sku, tag) VALUES ('ELEC-KDTAB', 'kids');

INSERT INTO product_tags (sku, tag) VALUES ('FASH-DHNBG', 'FASHION');
INSERT INTO product_tags (sku, tag) VALUES ('FASH-DHNBG', 'luxury');

INSERT INTO product_tags (sku, tag) VALUES ('FASH-MCTSH', 'FASHION');
INSERT INTO product_tags (sku, tag) VALUES ('FASH-MCTSH', 'casual');

INSERT INTO product_tags (sku, tag) VALUES ('FASH-KDRSS', 'FASHION');
INSERT INTO product_tags (sku, tag) VALUES ('FASH-KDRSS', 'kids');

INSERT INTO product_tags (sku, tag) VALUES ('FASH-LXWTC', 'FASHION');
INSERT INTO product_tags (sku, tag) VALUES ('FASH-LXWTC', 'luxury');

INSERT INTO product_tags (sku, tag) VALUES ('SPRT-YGMAT', 'SPORTS');
INSERT INTO product_tags (sku, tag) VALUES ('SPRT-YGMAT', 'fitness');

INSERT INTO product_tags (sku, tag) VALUES ('SPRT-MTBKE', 'SPORTS');
INSERT INTO product_tags (sku, tag) VALUES ('SPRT-MTBKE', 'outdoor');

INSERT INTO product_tags (sku, tag) VALUES ('SPRT-KDFBL', 'SPORTS');
INSERT INTO product_tags (sku, tag) VALUES ('SPRT-KDFBL', 'TOYS');
INSERT INTO product_tags (sku, tag) VALUES ('SPRT-KDFBL', 'kids');

INSERT INTO product_tags (sku, tag) VALUES ('HOME-ARFRY', 'HOME_AND_KITCHEN');
INSERT INTO product_tags (sku, tag) VALUES ('HOME-ARFRY', 'cooking');

INSERT INTO product_tags (sku, tag) VALUES ('HOME-SMHUB', 'HOME_AND_KITCHEN');
INSERT INTO product_tags (sku, tag) VALUES ('HOME-SMHUB', 'smart');

INSERT INTO product_tags (sku, tag) VALUES ('BEAU-SKCKT', 'BEAUTY');
INSERT INTO product_tags (sku, tag) VALUES ('BEAU-SKCKT', 'skincare');

INSERT INTO product_tags (sku, tag) VALUES ('BEAU-MNGKT', 'BEAUTY');
INSERT INTO product_tags (sku, tag) VALUES ('BEAU-MNGKT', 'grooming');

INSERT INTO product_tags (sku, tag) VALUES ('BOOK-ATHBT', 'BOOKS');
INSERT INTO product_tags (sku, tag) VALUES ('BOOK-ATHBT', 'self-help');

INSERT INTO product_tags (sku, tag) VALUES ('BOOK-CHENC', 'BOOKS');
INSERT INTO product_tags (sku, tag) VALUES ('BOOK-CHENC', 'kids');

INSERT INTO product_tags (sku, tag) VALUES ('HLTH-FTRCK', 'HEALTH');
INSERT INTO product_tags (sku, tag) VALUES ('HLTH-FTRCK', 'fitness');

INSERT INTO product_tags (sku, tag) VALUES ('HLTH-VTMD3', 'HEALTH');
INSERT INTO product_tags (sku, tag) VALUES ('HLTH-VTMD3', 'wellness');

INSERT INTO product_tags (sku, tag) VALUES ('TRVL-INSUR', 'TRAVEL');
INSERT INTO product_tags (sku, tag) VALUES ('TRVL-INSUR', 'insurance');

INSERT INTO product_tags (sku, tag) VALUES ('TRVL-MLDVS', 'TRAVEL');
INSERT INTO product_tags (sku, tag) VALUES ('TRVL-MLDVS', 'luxury');

INSERT INTO product_tags (sku, tag) VALUES ('FINC-CCARD', 'FINANCE');
INSERT INTO product_tags (sku, tag) VALUES ('FINC-CCARD', 'credit');

INSERT INTO product_tags (sku, tag) VALUES ('FINC-INVST', 'FINANCE');
INSERT INTO product_tags (sku, tag) VALUES ('FINC-INVST', 'investment');

-- Subscription product tags
INSERT INTO product_tags (sku, tag) VALUES ('ELEC-MUSP', 'ELECTRONICS');
INSERT INTO product_tags (sku, tag) VALUES ('ELEC-MUSP', 'streaming');
INSERT INTO product_tags (sku, tag) VALUES ('ELEC-MUSP', 'subscription');

INSERT INTO product_tags (sku, tag) VALUES ('HLTH-GYMM', 'HEALTH');
INSERT INTO product_tags (sku, tag) VALUES ('HLTH-GYMM', 'fitness');
INSERT INTO product_tags (sku, tag) VALUES ('HLTH-GYMM', 'subscription');

INSERT INTO product_tags (sku, tag) VALUES ('BOOK-KLIM', 'BOOKS');
INSERT INTO product_tags (sku, tag) VALUES ('BOOK-KLIM', 'reading');
INSERT INTO product_tags (sku, tag) VALUES ('BOOK-KLIM', 'subscription');

INSERT INTO product_tags (sku, tag) VALUES ('FINC-INVP', 'FINANCE');
INSERT INTO product_tags (sku, tag) VALUES ('FINC-INVP', 'investment');
INSERT INTO product_tags (sku, tag) VALUES ('FINC-INVP', 'subscription');

INSERT INTO product_tags (sku, tag) VALUES ('HLTH-NUTR', 'HEALTH');
INSERT INTO product_tags (sku, tag) VALUES ('HLTH-NUTR', 'wellness');
INSERT INTO product_tags (sku, tag) VALUES ('HLTH-NUTR', 'subscription');

INSERT INTO product_tags (sku, tag) VALUES ('TRVL-LNGP', 'TRAVEL');
INSERT INTO product_tags (sku, tag) VALUES ('TRVL-LNGP', 'luxury');
INSERT INTO product_tags (sku, tag) VALUES ('TRVL-LNGP', 'subscription');
