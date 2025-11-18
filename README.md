# SipAgri-back
Backend Java/Spring Boot de l'application SipAgri (gestion, assistance et suivi des planteurs de maïs de la SIPRA, de leurs champs et de leur production).

---

## Sommaire
- Aperçu du projet
- Pile technique (stack)
- Prérequis
- Installation & configuration
- Lancer l'application (dev/test/prod)
- Scripts Maven utiles
- Variables d'environnement
- Base de données & migrations (Liquibase)
- Tests
- Structure du projet
- Sécurité (JWT)
- Dépannage (Troubleshooting)
- Licence
- TODO

---

## Aperçu du projet
Application Spring Boot exposant une API REST (port 9002 par défaut) avec authentification JWT, persistance Oracle, migrations de schéma avec Liquibase, et envoi d'e-mails (SMTP). Quelques endpoints d'authentification sont disponibles (ex. `POST /api/v1/auth/login`, `POST /api/v1/auth/register`, `POST /api/v1/auth/forgot-password`, `POST /api/v1/auth/reset-password`).

## Pile technique
- Langage: Java 21
- Framework: Spring Boot 3.5.x (web, security, data JPA, mail, thymeleaf)
- Build/Packaging: Maven (wrapper fourni: `mvnw`/`mvnw.cmd`)
- SGBD: Oracle (driver `ojdbc11`), Liquibase pour la gestion de schéma
- Authentification: JWT (`jjwt` 0.11.5)
- Tests: JUnit/Mockito via `spring-boot-starter-test`

## Prérequis
- Java 21 (JDK)
- Maven 3.9+ (ou utiliser le wrapper `mvnw`/`mvnw.cmd`)
- Accès à une base Oracle et tablespaces appropriés (voir section Liquibase)
- Variables d'environnement configurées (voir section « Variables d'environnement »)

## Installation & configuration
1. Cloner le dépôt.
2. Configurer les variables d'environnement requises (minima: datasource + secret JWT + SMTP si nécessaire).
3. Vérifier/ajuster le profil actif Maven selon le contexte (`dev`, `test` ou `prod`). Par défaut, le profil `test` est actif (voir `pom.xml`).

Fichier de configuration principal: `src/main/resources/application.properties`.
- Port/API: `server.port=9002`
- Liquibase activé: `spring.liquibase.enabled=true`
- `spring.jpa.hibernate.ddl-auto=none` (schéma géré par Liquibase)

Profils supplémentaires: `application-prod.properties` contient des niveaux de logs et exemples SSL.

## Lancer l'application
- Développement (profil dev):
  - Linux/macOS: `./mvnw spring-boot:run -Pdev`
  - Windows: `mvnw.cmd spring-boot:run -Pdev`

- Test (profil par défaut):
  - Linux/macOS: `./mvnw spring-boot:run -Ptest`
  - Windows: `mvnw.cmd spring-boot:run -Ptest`

- Production (profil prod):
  - Linux/macOS: `./mvnw spring-boot:run -Pprod`
  - Windows: `mvnw.cmd spring-boot:run -Pprod`

- Construire un jar exécutable:
  - Linux/macOS: `./mvnw clean package -DskipTests`
  - Windows: `mvnw.cmd clean package -DskipTests`
  - Exécuter: `java -jar target/SipAgri-0.0.1-SNAPSHOT.jar` (ajoutez `--spring.profiles.active=prod` si besoin)

## Scripts Maven utiles
- D démarrer rapidement: `mvnw spring-boot:run -Pdev`
- Lancer les tests: `mvnw test`
- Formater/Nettoyer/Construire: `mvnw clean package`
- Exclure les tests: `mvnw clean package -DskipTests`
- Vérifier le statut Liquibase: `mvnw liquibase:status`
- Générer le SQL Liquibase sans appliquer: `mvnw liquibase:updateSQL`

Sur Linux/macOS, remplacez `mvnw` par `./mvnw`. Sur Windows, utilisez `mvnw.cmd`.

## Variables d'environnement
Configurer ces variables avant de lancer l'application (par profil ou globalement):
- Base de données Oracle:
  - `SPRING_DATASOURCE_URL` (ex: `jdbc:oracle:thin:@//host:1521/ORCLPDB1`)
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
- Sécurité/JWT:
  - `SECURITY_CLIENT_SECRET` (clé secrète HMAC utilisée par `JwtUtil`; longueur suffisante requise)
- SMTP (OVH ou autre):
  - `SPRING_SMTP_HOST`
  - `SPRING_SMTP_PORT`
  - `SPRING_SMTP_USERNAME`
  - `SPRING_SMTP_PASSWORD`
- Divers:
  - `app.frontend.url` (URL du front pour certains liens, voir `application.properties`)
  
Note: Le projet lit la clé JWT via `spring.security.oauth2.resourceserver.opaquetoken.client-secret` (voir `JwtUtil`), mappée depuis `SECURITY_CLIENT_SECRET` dans `application.properties`.

## Base de données & Migrations (Liquibase)
Liquibase est activé et pilote l'intégralité du schéma. Assurez-vous que `spring.jpa.hibernate.ddl-auto=none`.

### Tablespaces Oracle (option DBA)
Avant le premier démarrage en prod, créer les tablespaces recommandés:

```sql
-- Tablespace données
CREATE TABLESPACE DATA_TBS
DATAFILE '/u01/app/oracle/oradata/ORCL/data_tbs01.dbf'
SIZE 500M AUTOEXTEND ON NEXT 100M MAXSIZE UNLIMITED;

-- Tablespace index
CREATE TABLESPACE INDEX_TBS
DATAFILE '/u01/app/oracle/oradata/ORCL/index_tbs01.dbf'
SIZE 500M AUTOEXTEND ON NEXT 100M MAXSIZE UNLIMITED;

-- Droits utilisateur
GRANT UNLIMITED TABLESPACE TO your_username;
ALTER USER your_username DEFAULT TABLESPACE DATA_TBS;
```

### Changelog
- Fichier maître: `src/main/resources/db/changelog/db.changelog-master.yaml`
- Inclusions: `001-create-sequences.yaml`, `002-create-tables.yaml`, `003-create-indexes.yaml`, `004-create-foreign-keys.yaml`, `005-initialize-database.yaml`

Au premier démarrage, Liquibase appliquera:
1. Création des séquences
2. Création des tables (DATA_TBS)
3. Création des index (INDEX_TBS)
4. Contraintes (FK)
5. Initialisation des données (si définie dans `005-initialize-database.yaml`)

### Vérifications utiles
```sql
-- Tables et tablespace
SELECT table_name, tablespace_name FROM user_tables
WHERE table_name IN ('KITS','PRODUCTS','PLANTERS','SUPERVISORS')
ORDER BY table_name;

-- Index et tablespace
SELECT index_name, table_name, tablespace_name FROM user_indexes
WHERE table_name IN ('KITS','PRODUCTS','PLANTERS','SUPERVISORS')
ORDER BY table_name, index_name;
```

### Rollback exemple
```bash
mvnw liquibase:rollback -Dliquibase.rollbackCount=1
```

### Bonnes pratiques
1. Ne jamais modifier un changeset déjà appliqué; créer un nouveau fichier sous `db/changelog/changes/`
2. Toujours tester d'abord en dev/test
3. Index dans `INDEX_TBS`, tables dans `DATA_TBS`
4. Noms explicites pour les `changeSet`
5. Documenter les changements complexes

## Tests
- Lancer tous les tests: `mvnw test`
- Exécuter une classe de test: `mvnw -Dtest=FullyQualifiedClassName test`
- Rapports: générés sous `target/surefire-reports`

## Structure du projet
```
SipAgri-back/
├─ pom.xml
├─ src/
│  ├─ main/
│  │  ├─ java/com/avos/sipra/sipagri/
│  │  │  ├─ SipAgriApplication.java  (point d'entrée)
│  │  │  ├─ controllers/             (ex: AuthController)
│  │  │  ├─ security/                (ex: SecurityConfig, JwtUtil, JwtRequestFilter)
│  │  │  ├─ services/                (cores, impl, dtos, mappers)
│  │  │  ├─ repositories/
│  │  │  ├─ entities/
│  │  │  ├─ enums/, types/, exceptions/
│  │  └─ resources/
│  │     ├─ application.properties
│  │     ├─ application-prod.properties
│  │     └─ db/changelog/ (Liquibase)
│  └─ test/java/com/avos/sipra/sipagri/ (tests unitaires)
├─ mvnw, mvnw.cmd
├─ qodana.yaml
└─ logs/
```

## Sécurité (JWT)
- Génération et validation de JWT via `JwtUtil` (lib `jjwt` 0.11.5)
- Secret chargé depuis `SECURITY_CLIENT_SECRET` (`spring.security.oauth2.resourceserver.opaquetoken.client-secret`)
- Filtre `JwtRequestFilter` pour authentifier les requêtes entrantes
- Endpoints d'auth: `POST /api/v1/auth/login`, `POST /api/v1/auth/register`, `POST /api/v1/auth/forgot-password`, `POST /api/v1/auth/reset-password`

## Dépannage
- Liquibase ne s'exécute pas: vérifier `spring.liquibase.enabled=true`
- Erreur tablespace: vérifier existence via `SELECT tablespace_name FROM dba_tablespaces;`
- Problème de clé JWT: s'assurer que `SECURITY_CLIENT_SECRET` est suffisamment long pour HS256
- SMTP OVH: activer SSL (`spring.mail.properties.mail.smtp.ssl.enable=true`) et définir `SPRING_SMTP_*`
- SSL (prod): voir exemples commentés dans `application-prod.properties` (keystore PKCS12)

## Licence
- TODO: préciser la licence du projet et ajouter un fichier LICENSE.

## TODO
- [ ] Renseigner la politique de versionnage et de déploiement (CI/CD)
- [ ] Ajouter la documentation des endpoints (OpenAPI/Swagger) si disponible
- [ ] Fournir un profil `dev` prêt à l'emploi (H2 ou Oracle de dev) si souhaité
- [ ] Ajouter instructions Docker/Container si une image doit être fournie