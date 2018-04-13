CREATE TABLE Pasazerowie(
ID_Pasazera NUMBER NOT NULL PRIMARY KEY,
Imie VARCHAR2(50) NOT NULL ,
Nazwisko VARCHAR2(50) NOT NULL ,
Pesel NUMBER(11) NOT NULL UNIQUE
); 

commit;

CREATE TABLE Zaloga(
ID_Pracownika NUMBER NOT NULL PRIMARY KEY,
ID_Przewoznika NUMBER NOT NULL,
Imie VARCHAR2(50) NOT NULL ,
Nazwisko VARCHAR2(50) NOT NULL ,
Pesel NUMBER(11) NOT NULL UNIQUE,
Rola VARCHAR2(11) NOT NULL
); 

commit;

CREATE TABLE Kierunki(
ID_Kierunku NUMBER NOT NULL PRIMARY KEY,
Skad VARCHAR2(50) NOT NULL ,
Dokad VARCHAR2(50) NOT NULL
); 

commit;

CREATE TABLE Samoloty(
ID_Samolotu NUMBER NOT NULL PRIMARY KEY,
ID_Przewoznika NUMBER NOT NULL,
Model VARCHAR2(50) NOT NULL ,
Rok_produkcji NUMBER(4) NOT NULL ,
Liczba_pasazerow NUMBER NOT NULL
); 

commit;

CREATE TABLE Loty(
ID_Lotu NUMBER NOT NULL PRIMARY KEY,
ID_Przewoznika NUMBER NOT NULL,
ID_Kierunku NUMBER NOT NULL ,
ID_Samolotu NUMBER NOT NULL ,
Data_lotu DATE NOT NULL
); 

commit;

CREATE TABLE Pasazerowie_lotu(
ID_Pasazera NUMBER NOT NULL ,
ID_Lotu NUMBER NOT NULL
); 

commit;

CREATE TABLE Zaloga_lotu(
ID_Pracownika NUMBER NOT NULL ,
ID_Lotu NUMBER NOT NULL
); 

commit;

CREATE TABLE Przewoznicy(
ID_Przewoznika NUMBER NOT NULL PRIMARY KEY,
Nazwa_Przewoznika VARCHAR2(50) NOT NULL
); 

commit;

ALTER TABLE LOTY ADD CONSTRAINT fk_idkierunku FOREIGN KEY (ID_Kierunku) REFERENCES KIERUNKI(ID_Kierunku);
ALTER TABLE LOTY ADD CONSTRAINT fk_idsamolotu FOREIGN KEY (ID_Samolotu) REFERENCES SAMOLOTY(ID_Samolotu);
ALTER TABLE LOTY ADD CONSTRAINT fk_idprzewoznika FOREIGN KEY (ID_Przewoznika) REFERENCES PRZEWOZNICY(ID_Przewoznika);
ALTER TABLE Zaloga ADD CONSTRAINT fk_idprzewoznika2 FOREIGN KEY (ID_Przewoznika) REFERENCES PRZEWOZNICY(ID_Przewoznika);
ALTER TABLE SAMOLOTY ADD CONSTRAINT fk_idprzewoznika3 FOREIGN KEY (ID_Przewoznika) REFERENCES PRZEWOZNICY(ID_Przewoznika);
ALTER TABLE Pasazerowie_lotu ADD CONSTRAINT fk_idpasazazera FOREIGN KEY (ID_Pasazera) REFERENCES PASAZEROWIE(ID_Pasazera);
ALTER TABLE Pasazerowie_lotu ADD CONSTRAINT fk_idlotu FOREIGN KEY (ID_Lotu) REFERENCES LOTY(ID_Lotu);
ALTER TABLE Zaloga_lotu ADD CONSTRAINT fk_idlotu2 FOREIGN KEY (ID_Lotu) REFERENCES LOTY(ID_Lotu);
ALTER TABLE Zaloga_lotu ADD CONSTRAINT fk_idpracownika FOREIGN KEY (ID_Pracownika) REFERENCES Zaloga(ID_Pracownika);


ALTER TABLE Pasazerowie ADD CHECK (Pesel > 9999999999);
ALTER TABLE Zaloga ADD CHECK (Pesel > 9999999999);
ALTER TABLE Samoloty ADD CHECK (Rok_produkcji > 1900);
ALTER TABLE Samoloty ADD CHECK (ID_Samolotu > 0);
ALTER TABLE Przewoznicy ADD CHECK (ID_Przewoznika > 0);
ALTER TABLE Samoloty ADD CHECK (Liczba_pasazerow > 0);
ALTER TABLE Pasazerowie ADD CHECK (ID_Pasazera > 0);
ALTER TABLE Loty ADD CHECK (ID_Lotu > 0);
ALTER TABLE Kierunki ADD CHECK (ID_Kierunku > 0);
ALTER TABLE Zaloga ADD CHECK (ID_Pracownika > 0);

ALTER TABLE Pasazerowie_lotu ADD CONSTRAINT uq_Pasazerowie_lotu UNIQUE(ID_Pasazera, ID_Lotu);
ALTER TABLE Przewoznicy ADD CONSTRAINT uq_Przewoznicy UNIQUE(ID_Przewoznika, Nazwa_Przewoznika);
ALTER TABLE Zaloga_lotu ADD CONSTRAINT uq_Zaloga_lotu UNIQUE(ID_Pracownika, ID_Lotu);
ALTER TABLE Kierunki ADD CONSTRAINT uq_Kierunki UNIQUE(Skad, Dokad, ID_Kierunku);

commit;

CREATE OR REPLACE TRIGGER ID_Pasazera_update
 AFTER UPDATE OF ID_Pasazera ON Pasazerowie FOR EACH ROW
BEGIN
    UPDATE Pasazerowie_lotu
       SET ID_Pasazera = :new.ID_Pasazera
     WHERE ID_Pasazera = :old.ID_Pasazera;
END;
/

CREATE OR REPLACE TRIGGER ID_Lotu_update
 AFTER UPDATE OF ID_Lotu ON Loty FOR EACH ROW
BEGIN

    UPDATE Pasazerowie_lotu
       SET ID_Lotu = :new.ID_Lotu
     WHERE ID_Lotu = :old.ID_Lotu;

    UPDATE Zaloga_lotu
       SET ID_Lotu = :new.ID_Lotu
     WHERE ID_Lotu = :old.ID_Lotu;

END;
/

CREATE OR REPLACE TRIGGER ID_Pracownika_update
BEFORE UPDATE OF ID_Pracownika ON Zaloga FOR EACH ROW
BEGIN
    UPDATE Zaloga_lotu
       SET ID_Pracownika = :new.ID_Pracownika
     WHERE ID_Pracownika = :old.ID_Pracownika;
END;
/

CREATE OR REPLACE TRIGGER ID_Samolotu_update
 AFTER UPDATE OF ID_Samolotu ON Samoloty FOR EACH ROW
BEGIN
    UPDATE Loty
       SET ID_Samolotu = :new.ID_Samolotu
     WHERE ID_Samolotu = :old.ID_Samolotu;
END;
/

CREATE OR REPLACE TRIGGER ID_Kierunku_update
 AFTER UPDATE OF ID_Kierunku ON Kierunki FOR EACH ROW
BEGIN
    UPDATE Loty
       SET ID_Kierunku = :new.ID_Kierunku
     WHERE ID_Kierunku = :old.ID_Kierunku;
END;
/

CREATE OR REPLACE TRIGGER ID_Przewoznika_update
 AFTER UPDATE OF ID_Przewoznika ON Przewoznicy FOR EACH ROW
BEGIN

    UPDATE Loty
       SET ID_Przewoznika= :new.ID_Przewoznika
     WHERE ID_Przewoznika = :old.ID_Przewoznika;
	 
	    UPDATE Zaloga
       SET ID_Przewoznika= :new.ID_Przewoznika
     WHERE ID_Przewoznika = :old.ID_Przewoznika;


    UPDATE Samoloty
       SET ID_Przewoznika= :new.ID_Przewoznika
     WHERE ID_Przewoznika = :old.ID_Przewoznika;	 
	 
END;
/

CREATE OR REPLACE TRIGGER loty_check_insert
AFTER INSERT ON Loty
FOR EACH ROW
DECLARE 
  ID_Przewoznika1 NUMBER;
BEGIN
  SELECT ID_Przewoznika
    INTO ID_Przewoznika1 
    FROM Samoloty
   WHERE ID_Samolotu = :new.ID_Samolotu; 
  IF ID_Przewoznika1 != :new.ID_Przewoznika THEN
    RAISE_APPLICATION_ERROR (-20000, 'Samolot nie naleøy do tego przewoünika');
  END IF;
  
    IF :new.Data_lotu < TRUNC(sysdate) THEN
      RAISE_APPLICATION_ERROR (-20000, 'Niepoprawna data lotu');
  END IF;
  
END;
/


CREATE OR REPLACE TRIGGER loty_check_update
AFTER UPDATE ON Loty
FOR EACH ROW
BEGIN

    IF :new.Data_lotu < '1900-01-01' THEN
      RAISE_APPLICATION_ERROR (-20000, 'Niepoprawna data lotu');
  END IF;
  
END;
/


CREATE OR REPLACE TRIGGER samoloty_check_insertupdate
AFTER INSERT OR UPDATE ON Samoloty
FOR EACH ROW
BEGIN

    IF :new.Rok_produkcji < 1900 OR :new.Rok_produkcji > EXTRACT(YEAR FROM sysdate)  THEN
      RAISE_APPLICATION_ERROR (-20000, 'Niepoprawny rok produkcji');
  END IF;
  
END;
/



CREATE OR REPLACE TRIGGER zaloga_check_insert
AFTER INSERT ON Zaloga
FOR EACH ROW
BEGIN
    IF :new.Rola != 'Kapitan' AND :new.Rola != 'Drugi pilot' AND :new.Rola != 'Stewardessa' AND :new.Rola != 'Steward' THEN
      RAISE_APPLICATION_ERROR (-20000, 'Niepoprawna rola cz≥onka za≥ogi');
  END IF;
  
END;
/




CREATE OR REPLACE TRIGGER Zaloga_lotu_check_insert
BEFORE INSERT ON Zaloga_lotu
FOR EACH ROW
DECLARE 
  ID_Przewoznika1 NUMBER;
  ID_Przewoznika2 NUMBER;
  Rola1 VARCHAR2(11);
  Rola2 VARCHAR2(11);
  Rola3 VARCHAR2(11);
  Ilosc NUMBER;
BEGIN

  SELECT ID_Przewoznika
    INTO ID_Przewoznika1 
    FROM Loty
   WHERE ID_Lotu = :new.ID_Lotu; 
   
     SELECT ID_Przewoznika
    INTO ID_Przewoznika2
    FROM Zaloga
   WHERE ID_Pracownika = :new.ID_Pracownika; 
   
  IF ID_Przewoznika2 != ID_Przewoznika1 THEN
    RAISE_APPLICATION_ERROR (-20000, 'Pracownik nie jest zatrudniony u tego przewoünika');
  END IF;
  
       SELECT Rola
    INTO Rola1
    FROM Zaloga
   WHERE ID_Pracownika = :new.ID_Pracownika; 
   
   IF Rola1 = 'Kapitan' OR Rola1 = 'Drugi pilot' THEN
   
   
   
       FOR rec IN (SELECT ID_Pracownika FROM Zaloga_lotu WHERE ID_Lotu = :new.ID_Lotu)
    LOOP
        SELECT Rola INTO Rola2 FROM Zaloga WHERE ID_Pracownika = rec.ID_Pracownika;
		
		IF Rola2 = Rola1 THEN
		    RAISE_APPLICATION_ERROR (-20000, 'Istnieje juz wybrany kapitan/drugi pilot dla tego lotu');
  END IF;
		
		
    END LOOP;
     END IF;
   

  
END;
/