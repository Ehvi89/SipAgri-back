INSERT INTO params (name, description, value, code_params, encrypted)
SELECT 'PRIX_ACHAT_MAIS', 'PRIX ACHAT MAIS', '100', 'BACKEND', false
WHERE NOT EXISTS (
    SELECT 1 FROM params WHERE name = 'PRIX_ACHAT_MAIS'
);