# SipAgri-back
Backend de l'application SipAgri

# Migration Liquibase - SIPAGRI

## Structure du projet

```
src/
├── main/
│   ├── java/
│   │   └── com/avos/sipra/sipagri/
│   │       └── entities/
│   │           ├── Kit.java
│   │           ├── KitProduct.java
│   │           ├── Params.java
│   │           ├── Plantation.java
│   │           ├── PasswordResetToken.java
│   │           ├── Planter.java
│   │           ├── Product.java
│   │           ├── Production.java
│   │           └── Supervisor.java
│   └── resources/
│       ├── application.yml
│       └── db/
│           └── changelog/
│               ├── db.changelog-master.yaml
│               └── changes/
│                   ├── 001-create-sequences.yaml
│                   ├── 002-create-tables.yaml
│                   ├── 003-create-indexes.yaml
│                   └── 004-create-foreign-keys.yaml
```

## Prérequis

1. **Créer les tablespaces sur Oracle** (à exécuter en tant que DBA) :

```sql
-- Créer le tablespace pour les données
CREATE TABLESPACE DATA_TBS
DATAFILE '/u01/app/oracle/oradata/ORCL/data_tbs01.dbf' 
SIZE 500M 
AUTOEXTEND ON 
NEXT 100M 
MAXSIZE UNLIMITED;

-- Créer le tablespace pour les index
CREATE TABLESPACE INDEX_TBS
DATAFILE '/u01/app/oracle/oradata/ORCL/index_tbs01.dbf' 
SIZE 500M 
AUTOEXTEND ON 
NEXT 100M 
MAXSIZE UNLIMITED;

-- Donner les droits à votre utilisateur
GRANT UNLIMITED TABLESPACE TO your_username;
ALTER USER your_username DEFAULT TABLESPACE DATA_TBS;
```

2. **Configurer application.yml** :

Modifiez le fichier `application.yml` avec vos paramètres de connexion :
- URL de connexion Oracle
- Username et password
- Nom du schéma

3. **Dépendances Maven** :

Assurez-vous que votre `pom.xml` contient les dépendances fournies.

## Configuration importante

### Dans application.yml

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: none  # CRITIQUE: Ne pas laisser Hibernate générer le schéma
```

**⚠️ TRÈS IMPORTANT** : `ddl-auto` doit être sur `none` pour que Liquibase gère entièrement le schéma.

## Démarrage

### 1. Premier démarrage (base vide)

Au démarrage de l'application, Liquibase va automatiquement :
1. Créer les séquences
2. Créer les tables dans le tablespace **DATA_TBS**
3. Créer les index dans le tablespace **INDEX_TBS**
4. Créer les contraintes de clés étrangères

### 2. Vérification

Après le démarrage, vous pouvez vérifier la séparation avec ces requêtes :

```sql
-- Vérifier les tables et leur tablespace
SELECT table_name, tablespace_name 
FROM user_tables 
WHERE table_name IN ('Kits', 'Products', 'Planters', 'supervisors')
ORDER BY table_name;

-- Vérifier les index et leur tablespace
SELECT index_name, table_name, tablespace_name 
FROM user_indexes 
WHERE table_name IN ('Kits', 'Products', 'Planters', 'supervisors')
ORDER BY table_name, index_name;

-- Résultat attendu : 
-- Tables -> DATA_TBS
-- Index -> INDEX_TBS
```

## Gestion des changements de schéma

### Ajouter une nouvelle colonne

Créez un nouveau fichier : `005-add-column-example.yaml`

```yaml
databaseChangeLog:
  - changeSet:
      id: 005-add-email-to-planter
      author: Ehvi
      changes:
        - addColumn:
            tableName: Planters
            columns:
              - column:
                  name: email
                  type: VARCHAR2(100)
```

Puis ajoutez-le au master :

```yaml
# Dans db.changelog-master.yaml
  - include:
      file: db/changelog/changes/005-add-column-example.yaml
```

### Créer un nouvel index

```yaml
databaseChangeLog:
  - changeSet:
      id: 006-create-new-index
      author: Ehvi
      changes:
        - sql:
            dbms: oracle
            sql: |
              CREATE INDEX idx_planters_email 
              ON Planters(email) 
              TABLESPACE INDEX_TBS
```

## Rollback

Pour annuler le dernier changeset :

```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

## Bonnes pratiques

1. ✅ **Ne jamais modifier un changeset déjà appliqué** - Créer un nouveau changeset
2. ✅ **Toujours tester sur un environnement de développement d'abord**
3. ✅ **Tous les index doivent être créés dans INDEX_TBS**
4. ✅ **Toutes les tables doivent être créées dans DATA_TBS**
5. ✅ **Utiliser des noms explicites pour les changesets**
6. ✅ **Documenter les changements complexes**

## Commandes utiles

### Maven

```bash
# Voir le statut Liquibase
mvn liquibase:status

# Générer le SQL sans l'appliquer
mvn liquibase:updateSQL

# Appliquer les changements
mvn spring-boot:run
```

### Liquibase CLI (optionnel)

```bash
# Voir les changements non appliqués
liquibase status

# Appliquer les changements
liquibase update

# Rollback
liquibase rollback-count 1
```

## Avantages de cette approche

### Séparation Données/Index
- ✅ **Performance** : Les lectures/écritures sont optimisées
- ✅ **Maintenance** : Possibilité de sauvegarder séparément
- ✅ **Scalabilité** : Possibilité de placer sur des disques différents

### Liquibase
- ✅ **Versionning** : Historique complet des changements
- ✅ **Rollback** : Retour arrière facile
- ✅ **Multi-environnements** : Dev, Test, Prod cohérents
- ✅ **Collaboration** : Changements trackés dans Git

## Troubleshooting

### Erreur "Tablespace not found"

```sql
-- Vérifier que les tablespaces existent
SELECT tablespace_name FROM dba_tablespaces;
```

### Liquibase ne s'exécute pas

Vérifier que :
```yaml
spring:
  liquibase:
    enabled: true  # Doit être true
```

### Erreur de séquence

Si vous avez déjà des données, ajustez les séquences :
```sql
-- Trouver le max ID
SELECT MAX(id) FROM Kits;

-- Ajuster la séquence
ALTER SEQUENCE kit_seq RESTART WITH 100;
```

## Support

Pour toute question sur cette configuration, consultez :
- [Documentation Liquibase](https://docs.liquibase.com)
- [Oracle Tablespaces](https://docs.oracle.com/en/database/)
- [Spring Boot + Liquibase](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.liquibase)