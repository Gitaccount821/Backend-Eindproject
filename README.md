**Installatiehandleiding voor de web-API**

Welkom in de installatiehandleiding voor de web-APi om een backend te vormen voor een niet-bestaand garage bedrijf.
Met deze web-APi kun je belangrijke data opslaan als auto's, reparaties gekoppeld aan deze auto's, en via ID's reparatietypes en gebruikte onderdelen toevoegen.
Ook wordt er gebruik gemaakt van verschillende rollen (klant, medewerker en monteur), welke limitaties hebben tot de verschillende endpoints. De rol Monteur heeft de meeste rechten.

## Table of Contents
1. [Requirements](#requirements)
2. [Installatie](#installatie).
3. [Accounts](#Accounts).
4. [Postman](#Postman).
5. [REST-endpoints](#REST-endpoints).
6. [Tests](#Tests).

## Requirements

Zorg dat de volgende software is geïnstalleerd:


| Software       | Aanbevolen Versie	  | Opmerkingen|
|----------------|---------------------|  -------------|
| IntelliJ IDEA (of een vergelijkbare IDE). | Community/Ultimate  | Het project is ontworpen en getest in IntelliJ IDEA |
| Java JDK      | 21 (Amazon Corretto)           | 	Zorg dat deze in je PATH staat. |
| PostgreSQL       | 	15+            | 	Database-engine voor de backend. |
| pgAdmin 4      | Meest recent           | UI voor databasebeheer. |
| Postman    | Meest recent            | 	Voor het testen van REST-endpoints.|
| Git    | 	N/A            | 		Voor het klonen van de repository.|


## Installatie

1. Clone de applicatie vanaf de github repository: https://github.com/Gitaccount821/Backend-Eindproject. Doe dit met behulp van de volgende command: ```git clone https://github.com/Gitaccount821/Backend-Eindproject.git```
2. Open het project in IntelliJ en selecteer de pom.xml als Maven-project.
3. Download postgresql
4. Installeer pgAdmin
5. download Postman
6. Start de computer opnieuw op
7. Herstart je IDE
8. Open PGAdmin en doe het volgende:
- log in met je admin wachtwoord
- Klik met de rechtermuisknop op "Databases" → klik op "Create" → "Database"
- Geef de database de naam: `garage`
- Klik op "Save"
9. Open: src/main/resources/application.properties en vervang YOUR_DB_USERNAME en YOUR_DB_PASSWORD met je eigen databasegegevens. ````spring.datasource.url=jdbc:postgresql://localhost:5432/garage````
   ```` spring.datasource.username=YOUR_DB_USERNAME````
   ````spring.datasource.password=YOUR_DB_PASSWORD````

Als voorbeeld: als je Postgres-gebruikersnaam bijvoorbeeld `postgres` is en je wachtwoord `admin`, dan zal het er als volgt uit moeten zien:
- spring.datasource.username=postgres
- spring.datasource.password=admin

10. Zorg dat JDK 21 (Amazon Corretto) actief is: ga in IntelliJ naar Project Structure > SDK en selecteer JDK 21.
11. Run "EindprojectBackendApplication" in dan map main > java > nl.novi.eindprojectbackend
12. Als alles goed is ingesteld zal je het volgende zien:
- De Spring Boot app start succesvol op in de console
- De database connectie is succesvol
- Je kan nu navigeren naar `http://localhost:8080` om te starten
13. Test of alles werkt:
- Gebruik Postman om een POST request naar `/authenticate` te sturen met:
  ```
  {
    "username": "monteur1",
    "password": "password"
  }
  ```
- Je ontvangt dan een JWT die je kunt gebruiken voor andere endpoints




## Accounts

Deze web-API beveiligd rollen met behulp van JWT. Je kan zelf nieuwe accounts aanmaken en deze een rol geven voor het opstarten van de web-APi met behulp van de data.sql.
Deze bevindt zich in de map main > resources

Je kan hierbij ook kiezen voor 1 van de eerder aangemaakte test rollen: hier hoef je dan enkel het wachtwoord voor aan te passen bij de users. Om een nieuw beveiligd wachtwoord aan te maken,
ga naar java> nl.novi.eindprojectbackend > util > PasswordEncoderUtil. Vervang "PASSWORD_HERE" met je eigen test password, en kopieer dit versleutelde password uit de terminal in de data.sql


De aangemaakte gebruikers welke je kan aanpassen naar wils om mee te testen:

1. 'monteurtest', rol = Monteur
2. 'medewerker', rol = Medewerker
3. 'klanttest', rol = Klant

De andere 4 users zijn aangemaakt om in de java tests gebruikt te kunnen worden en data te bevatten. Het wachtwoord hierin aanpassen zal de tests negatief beïnvloeden en raad ik dus ten zeerste af als je deze tests nog wilt uitvoeren.
Het wachtwoord van Klant1, Monteur1 en Medewerker1 zijn opgegeven in de PostmanJson collectie die is meegeleverd. Deze zijn niet in de source code gezet wegens veiligheids keuze.


## Postman

Er is een postman collectie meegeleverd in de zip file: deze staat in json formaat. Dit kun je terug importeren in postman via de volgende stappen:

1. start postman op
2. Ga naar de optie 'Importeren', in de linkerbovenhoek van het Postman-venster
3. Zoek en selecteer het bijgeleverde JSON-bestand
4. Postman zal een voorbeeld van de collectie weergeven. Klik op de knop Importeren onder aan het dialoogvenster om het importproces te voltooien.
5. Na het importeren zal de collectie in de linkerzijbalk onder het tabblad Collecties verschijnen. 
6. Je kan hier de endpoints dan testen door eerst een jwt token op te vragen met de /authenticate endpoint en dan op send te drukken
7. gebruik deze jwt vervolgens in de (authorization) tab voor de andere endpoints naar keuze, waar je in de (body) aanpassingen kunt maken voor testdata voor POST/PATCH/PUT requests


## REST-endpoints

Hierbij tevens een lijst van de REST-endpoints

1) POST /api/users/register
* {
  "username": "klant12",
  "password": "Klant",
  "email": "klant12@example.com"
  }
* Registreert nieuwe gebruikers als rol klant. Geen beveiliging, iedereen kan een nieuw account aanmaken.

2) POST /authenticate
* {
  "username": "klant1",
  "password": "password"
  }
* de manier om een jwt te ontvangen. Dit werkt alleen als het wachtwoord overeenkomt met de username zoals deze geregristreerd staat in de database.

3) GET /api/cars
* Dit ontvangt ALLE auto's in de database. Dit is enkel mogelijk met een jwt met de rol medewerker of monteur

4) POST /api/cars
* {
  "carType": "Sedan",
  "ownerUsername": "klant1",
  "repairRequestDate": "20-01-2025"
  }
*  Dit zet een nieuwe auto neer in de database. Dit is enkel mogelijk met een jwt met de rol medewerker of monteur

5) PATCH /api/cars/[carID]
* {
  "carType": "Sedanz",
  }
* een gedeelte van een auto zijn informatie aanpassen. Dit is enkel mogelijk met een jwt met de rol medewerker of monteur

6) DELETE /api/cars/[carID]

* verwijderd een auto naar keuze. Dit is enkel mogelijk met een jwt met de rol medewerker of monteur

7) GET /api/cars/[carID]/repairs

*  Zie alle reperaties van een auto. Dit is enkel mogelijk met een jwt met de rol monteur, en als er al een auto aanwezig is met het ID

8) GET /api/cars/[carID]/repairs/[repairID]

*  Zie specifieke repairinformatie van 1 repair naar keuze van een auto. Dit is enkel mogelijk met een jwt met de rol monteur, en als er al een auto aanwezig is met het ID

9) POST /api/cars/[carID]/repairs/
* {
  "repairTypeId": 1,
  "repairRequestDate": "20-01-2025",
  "repairDate": "25-01-2025",
  "partIds": [1,2]
  }
*  Voeg een reperatie bij een auto naar keuze. Dit is enkel mogelijk met een jwt met de rol monteur, en als er al een auto aanwezig is met het ID

10) PATCH api/cars/[carID]/repairs/[RepairID]
* {
  "repairRequestDate": "20-01-2025"
  }
* een gedeelte van een auto zijn reparatie-informatie aanpassen.  Dit is enkel mogelijk met een jwt met de rol monteur, en als er al een auto aanwezig is met het ID

11) GET /api/parts
* zie alle onderdelen. Dit is enkel mogelijk met een jwt met de rol medewerker of monteur

12) POST /api/parts
* {
  "name": "Steering Wheel",
  "price": 200.0,
  "stock": 5
  }
* voegt een onderdeel toe. Dit is enkel mogelijk met een jwt met de rol medewerker of monteur

13) Patch en PUT /api/parts/[partID]
* {
  "stock": 60
  }
* past informatie van een onderdeel aan. Dit is enkel mogelijk met een jwt met de rol medewerker of monteur.

14) DELETE /api/parts/[partID]
* Verwijderd een Part naar keuze.  Dit is enkel mogelijk met een jwt met de rol medewerker of monteur.

15) GET /api/repair-types
* zie alle repair-types. Dit is enkel mogelijk met een jwt met de rol monteur

16) POST /api/repair-types
* {
  "name": "Engine Repair",
  "cost": 150.0,
  "description": "Repair related to engine issues."
  }
* voegt een repair type toe. Dit is enkel mogelijk met een jwt met de rol monteur

17) PATCH and PUT /api/repair-types/[repairtypeID]
* {
  "cost": 550.0
  }
*  past informatie van een repair type aan. Dit is enkel mogelijk met een jwt met de rol monteur.

18) DELETE api/repair-types/[repairtypeID]
* Delete een repair type

19) /api/pdfs/upload/[pdfID]
* uploadt een pdf naar een auto. De auto moet bestaan. Dit is enkel mogelijk met een jwt met de rol klant.

20) /api/pdfs/download/[pdfID]
* download een pdf. dit is mogelijk met een jwt van klant, medewerker en monteur.

21) /api/users/create-user?role=MONTEUR
* "username": "monteur12",
  "password": "monteur",
  "email": "monteur@example.com"
  }
* Dit creëert een nieuwe monteur rol. Dit is enkel mogelijk met een jwt met de rol medewerker.

Alle endpoints zijn restricted met behulp van de SpringSecurityConfig: de rollen bevinden zich binnen de JWT in iedere ingelogde gebruiker.

## Tests

De map /src/test/java/ bevat:

- Integration tests (API functionaliteit)
- Unit tests (bijv. services)

Voer tests individueel uit via IntelliJ door met de rechtermuisknop op de testklas te drukken in en deze te runnen, of via Maven (mvn test).


