# Poliambulatorio - Prenotazione visite mediche

L'applicazione permette al paziente di effettuare il login, consultare le prestazioni, scegliere un medico e un orario disponibile, prenotare una visita, visualizzare le proprie prenotazioni e annullare quelle future.

## Tecnologie utilizzate

- **Backend:** Java 11, Maven, API REST con HttpServer, JDBC e BCrypt.
- **Database:** MariaDB.
- **Frontend:** HTML, CSS e JavaScript.
- **Documentazione API:** OpenAPI e Swagger UI.

Il frontend è incluso nel progetto Maven e viene servito direttamente dal server Java. Non è necessario avviare un server frontend separato.

## Requisiti

- JDK 11.
- IntelliJ IDEA con supporto Maven.
- Un server MariaDB, ad esempio quello utilizzato tramite XAMPP.

## 1. Importazione del database

1. Avviare il server MariaDB.
2. Aprire phpMyAdmin oppure un altro client SQL collegato al proprio server.
3. Eseguire il file **`TesiAmri.sql`**, presente nel progetto.

Lo script crea il database `TesiAmri`, le relative tabelle e i dati iniziali necessari per provare l'applicazione.

**Attenzione:** lo script contiene `DROP DATABASE IF EXISTS TesiAmri`. Se il database esiste già, viene eliminato e ricreato, perdendo le eventuali prenotazioni precedenti.

Le date delle disponibilità di prova vengono calcolate in base al giorno di importazione dello script.

## 2. Configurazione della connessione al database

Aprire il file:

`src/main/java/infrastruttura/persistenza/jdbc/ConnessioneDatabase.java`

La configurazione utilizzata durante lo sviluppo è:

```java
private static final String URL = "jdbc:mariadb://localhost:3306/TesiAmri";
private static final String UTENTE = "root";
private static final String PASSWORD = "";
```

Modificare i tre valori in base al proprio ambiente:

- **`URL`**: indirizzo del server MariaDB, porta e nome del database.
- **`UTENTE`**: nome dell'utente MariaDB.
- **`PASSWORD`**: password dell'utente MariaDB.

Non è necessario modificare le query JDBC o le altre classi del backend.

## 3. Avvio del progetto

1. Aprire il progetto in **IntelliJ IDEA** tramite il file `pom.xml`.
2. Verificare che il progetto utilizzi **Java 11**.
3. Dal pannello **Maven → Lifecycle**, eseguire `clean` e poi `package` (la compilazione deve terminare con `BUILD SUCCESS`).
4. Avviare la classe **`Main.java`**, presente in `src/main/java`.
5. Aprire il browser agli indirizzi indicati sotto.

Con la configurazione attuale:
1. Applicazione web: `http://localhost:8080/`
2. Swagger UI: `http://localhost:8080/swagger`
3. Specifica OpenAPI: `http://localhost:8080/openapi.yaml`


## 4. Account di prova

Lo script SQL inserisce due utenti di prova:
1. Email: `mario.rossi@test.it` | Password: `password123`
2. Email: `sara.neri@test.it` | Password: `password456`


## 5. Modificare la porta del server web

Il server HTTP Java utilizza attualmente la porta **8080**. È diversa dalla porta del database MariaDB, che nella configurazione iniziale è **3306**.

Per cambiare la porta del server web, ad esempio da `8080` a `9090`, effettuare queste modifiche:

**1. In `Main.java`**, modificare la porta con cui viene creato il server:

```java
HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);
```

**2. Sempre in `Main.java`**, aggiornare il messaggio stampato nella console:

```java
System.out.println("Server avviato su http://localhost:9090");
```


**3. In `src/main/resources/openapi.yaml`**, aggiornare l'indirizzo del server utilizzato da Swagger:

```yaml
servers:
  - url: http://localhost:9090
```

Riavviare quindi `Main.java`. L'applicazione sarà raggiungibile su `http://localhost:9090/` e Swagger su `http://localhost:9090/swagger`.