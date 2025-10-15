-- Insert default trainer user
INSERT INTO users (email, password, name, role) VALUES 
('trainer@yoga.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Yoga Trainer', 'TRAINER');

-- Insert sample subscription types
INSERT INTO subscription_types (name, description, price, duration_days, max_classes) VALUES 
('Single Class', 'One-time class access', 15.00, 1, 1),
('Monthly Unlimited', 'Unlimited classes for one month', 80.00, 30, NULL),
('10 Classes Pack', '10 classes valid for 3 months', 120.00, 90, 10),
('3 Months Unlimited', 'Unlimited classes for 3 months', 200.00, 90, NULL),
('6 Months Unlimited', 'Unlimited classes for 6 months', 350.00, 180, NULL),
('Annual Pass', 'Unlimited classes for one year', 600.00, 365, NULL);

-- Insert sample clients
INSERT INTO clients (name, phone, email, birth_date, notes) VALUES 
('Anna Smith', '+1234567890', 'anna.smith@email.com', '1990-05-15', 'Prefers morning classes'),
('John Doe', '+1234567891', 'john.doe@email.com', '1985-08-22', 'Beginner level'),
('Maria Garcia', '+1234567892', 'maria.garcia@email.com', '1992-12-10', 'Advanced practitioner'),
('David Wilson', '+1234567893', 'david.wilson@email.com', '1988-03-25', 'Injury recovery'),
('Sarah Johnson', '+1234567894', 'sarah.johnson@email.com', '1995-07-18', 'Prenatal yoga');

-- Insert sample classes for the next week
INSERT INTO classes (name, description, start_time, end_time, max_capacity, status) VALUES 
('Morning Flow', 'Gentle morning yoga flow', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '8 hours', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '9 hours', 15, 'SCHEDULED'),
('Power Yoga', 'Dynamic and challenging yoga practice', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '18 hours', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '19 hours', 12, 'SCHEDULED'),
('Yin Yoga', 'Slow and meditative practice', CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '10 hours', CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '11 hours', 20, 'SCHEDULED'),
('Vinyasa Flow', 'Flowing sequence of poses', CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '19 hours', CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '20 hours', 15, 'SCHEDULED'),
('Beginner Yoga', 'Perfect for newcomers', CURRENT_TIMESTAMP + INTERVAL '3 days' + INTERVAL '9 hours', CURRENT_TIMESTAMP + INTERVAL '3 days' + INTERVAL '10 hours', 25, 'SCHEDULED'),
('Advanced Practice', 'For experienced practitioners', CURRENT_TIMESTAMP + INTERVAL '3 days' + INTERVAL '18 hours', CURRENT_TIMESTAMP + INTERVAL '3 days' + INTERVAL '19 hours', 10, 'SCHEDULED'),
('Restorative Yoga', 'Relaxing and healing practice', CURRENT_TIMESTAMP + INTERVAL '4 days' + INTERVAL '11 hours', CURRENT_TIMESTAMP + INTERVAL '4 days' + INTERVAL '12 hours', 18, 'SCHEDULED'),
('Hot Yoga', 'Yoga in heated room', CURRENT_TIMESTAMP + INTERVAL '4 days' + INTERVAL '17 hours', CURRENT_TIMESTAMP + INTERVAL '4 days' + INTERVAL '18 hours', 12, 'SCHEDULED');


