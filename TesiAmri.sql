-- ============================================================================
-- 1. CREAZIONE TABELLE
-- ============================================================================

DROP DATABASE IF EXISTS `TesiAmri`;
CREATE DATABASE `TesiAmri`;
USE `TesiAmri`;

CREATE TABLE tipi_utente (
    id_tipo_utente INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    descrizione VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE utenti (
    id_utente INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    hash_password VARCHAR(255) NOT NULL,
    id_tipo_utente INT UNSIGNED NOT NULL,
    CONSTRAINT fk_utenti_tipo_utente FOREIGN KEY (id_tipo_utente)
        REFERENCES tipi_utente(id_tipo_utente)
);

CREATE TABLE specializzazioni (
    id_specializzazione INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    descrizione VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE medici (
    id_medico INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    id_specializzazione INT UNSIGNED NOT NULL,
    CONSTRAINT fk_medici_specializzazione FOREIGN KEY (id_specializzazione)
        REFERENCES specializzazioni(id_specializzazione)
);

CREATE TABLE prestazioni_mediche (
    id_prestazione INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL UNIQUE,
    descrizione VARCHAR(500) NULL
);

CREATE TABLE medici_prestazioni (
    id_medico INT UNSIGNED NOT NULL,
    id_prestazione INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_medico, id_prestazione),
    CONSTRAINT fk_medici_prestazioni_medico FOREIGN KEY (id_medico)
        REFERENCES medici(id_medico) ON DELETE CASCADE,
    CONSTRAINT fk_medici_prestazioni_prestazione FOREIGN KEY (id_prestazione)
        REFERENCES prestazioni_mediche(id_prestazione) ON DELETE CASCADE
);

CREATE TABLE disponibilita (
    id_disponibilita INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_medico INT UNSIGNED NOT NULL,
    data_disponibilita DATE NOT NULL,
    ora_inizio TIME NOT NULL,
    disponibile BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_disponibilita_medico FOREIGN KEY (id_medico)
        REFERENCES medici(id_medico),
    CONSTRAINT uq_medico_disponibilita UNIQUE (id_medico, data_disponibilita, ora_inizio)
);

CREATE TABLE stati_prenotazione (
    id_stato_prenotazione INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    descrizione VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE prenotazioni (
    id_prenotazione INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_paziente INT UNSIGNED NOT NULL,
    id_prestazione INT UNSIGNED NOT NULL,
    id_disponibilita INT UNSIGNED NOT NULL,
    id_stato_prenotazione INT UNSIGNED NOT NULL,
    data_creazione TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_annullamento TIMESTAMP NULL DEFAULT NULL,
    CONSTRAINT fk_prenotazioni_paziente FOREIGN KEY (id_paziente)
        REFERENCES utenti(id_utente),
    CONSTRAINT fk_prenotazioni_prestazione FOREIGN KEY (id_prestazione)
        REFERENCES prestazioni_mediche(id_prestazione),
    CONSTRAINT fk_prenotazioni_disponibilita FOREIGN KEY (id_disponibilita)
        REFERENCES disponibilita(id_disponibilita),
    CONSTRAINT fk_prenotazioni_stato FOREIGN KEY (id_stato_prenotazione)
        REFERENCES stati_prenotazione(id_stato_prenotazione)
);

-- ============================================================================
-- 2. DATI TIPOLOGICI
-- ============================================================================

INSERT INTO tipi_utente (id_tipo_utente, descrizione) VALUES
    (1, 'PAZIENTE'),
    (2, 'AMMINISTRATORE');

INSERT INTO stati_prenotazione (id_stato_prenotazione, descrizione) VALUES
    (1, 'PRENOTATA'),
    (2, 'ANNULLATA');

INSERT INTO specializzazioni (id_specializzazione, descrizione) VALUES
    (1, 'Cardiologia'),
    (2, 'Dermatologia'),
    (3, 'Ortopedia');

-- ============================================================================
-- 3. UTENTI PAZIENTI DI PROVA
-- ============================================================================

INSERT INTO utenti (id_utente, nome, cognome, email, hash_password, id_tipo_utente) VALUES
    (1, 'Mario', 'Rossi', 'mario.rossi@test.it', '$2a$12$T7IFKzJGONSSRZ72FS/RyOYZz14Fi1Ei.6cdcru.H0SpXQusSqyfC', 1),
    (2, 'Sara', 'Neri', 'sara.neri@test.it', '$2a$12$RyIiMmHjUmtAHLMzTzGxx.Pr0NojVeeVC3mpRal/ULTXDTmXOdV3W', 1);

-- ============================================================================
-- 4. MEDICI E PRESTAZIONI DISPONIBILI
-- ============================================================================

INSERT INTO medici (id_medico, nome, cognome, id_specializzazione) VALUES
    (1, 'Luca', 'Bianchi', 1),
    (2, 'Anna', 'Verdi', 1),
    (3, 'Marco', 'Riva', 2),
    (4, 'Elena', 'Moretti', 3);

INSERT INTO prestazioni_mediche (id_prestazione, nome, descrizione) VALUES
    (1, 'Visita cardiologica', 'Valutazione specialistica cardiologica.'),
    (2, 'Elettrocardiogramma', 'Esame diagnostico dell''attività elettrica cardiaca.'),
    (3, 'Visita dermatologica', 'Valutazione specialistica dermatologica.'),
    (4, 'Visita ortopedica', 'Valutazione specialistica dell''apparato muscolo-scheletrico.'),
    (5, 'Visita neurologica', 'Valutazione specialistica del sistema nervoso.');

INSERT INTO medici_prestazioni (id_medico, id_prestazione) VALUES
    (1, 1), (1, 2),
    (2, 1), (2, 2),
    (3, 3),
    (4, 4);

-- ============================================================================
-- 5. DISPONIBILITA DI PROVA
-- ============================================================================

INSERT INTO disponibilita (id_disponibilita, id_medico, data_disponibilita, ora_inizio, disponibile) VALUES
    (1,  1, DATE_ADD(CURDATE(), INTERVAL 7 DAY),  '09:00:00', TRUE),
    (2,  1, DATE_ADD(CURDATE(), INTERVAL 7 DAY),  '09:30:00', TRUE),
    (3,  1, DATE_ADD(CURDATE(), INTERVAL 8 DAY),  '11:00:00', TRUE),
    (4,  1, DATE_ADD(CURDATE(), INTERVAL 8 DAY),  '11:30:00', TRUE),
    (5,  2, DATE_ADD(CURDATE(), INTERVAL 9 DAY),  '09:00:00', TRUE),
    (6,  2, DATE_ADD(CURDATE(), INTERVAL 9 DAY),  '09:30:00', TRUE),
    (7,  3, DATE_ADD(CURDATE(), INTERVAL 10 DAY), '10:00:00', TRUE),
    (8,  3, DATE_ADD(CURDATE(), INTERVAL 10 DAY), '10:30:00', TRUE),
    (9,  4, DATE_ADD(CURDATE(), INTERVAL 11 DAY), '16:00:00', TRUE),
    (10, 4, DATE_ADD(CURDATE(), INTERVAL 11 DAY), '16:30:00', TRUE),
    (11, 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY),  '14:00:00', TRUE),
    (12, 1, DATE_ADD(CURDATE(), INTERVAL 12 DAY), '12:00:00', FALSE);
