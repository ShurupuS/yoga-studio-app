-- Add username column to users table
ALTER TABLE users ADD COLUMN username VARCHAR(255) UNIQUE NOT NULL DEFAULT '';

-- Update existing users with username based on email
UPDATE users SET username = split_part(email, '@', 1) WHERE username = '';

-- Make username NOT NULL after updating existing records
ALTER TABLE users ALTER COLUMN username SET NOT NULL;
