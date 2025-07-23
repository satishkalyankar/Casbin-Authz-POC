-- Create the casbin_rule table if it does not already exist
CREATE TABLE IF NOT EXISTS public.casbin_rule (
    id SERIAL PRIMARY KEY,                -- Automatically increments with each new entry
    ptype VARCHAR(50),                    -- Policy type (e.g., p, g)
    v0 VARCHAR(255),                      -- Subject (user or role)
    v1 VARCHAR(255),                      -- Resource (API endpoint)
    v2 VARCHAR(255),                      -- Action (HTTP method)
    v3 VARCHAR(255),                      -- Any additional fields
    v4 VARCHAR(255),
    v5 VARCHAR(255)

);

-- Insert role-based access policies for 'ADMIN' role

-- Add policy for 'SUPER_ADMIN' role to access '/api/policies/add' with POST method
INSERT INTO public.casbin_rule (ptype, v0, v1, v2)
VALUES ('p', 'SUPER_ADMIN', '/api/policies/add', 'POST');
