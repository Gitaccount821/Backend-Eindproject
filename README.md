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

- IntelliJ IDEA (of een vergelijkbare IDE). Het project is ontworpen en getest in IntelliJ IDEA
- pgAdmin
- postgresql
- Postman


## Installatie

1. Open je IDE
2. Clone de applicatie vanaf de github repository: https://github.com/Gitaccount821/Backend-Eindproject
3. Download postgresql
4. Installeer pgAdmin
5. download Postman
6. Start de computer opnieuw op
7. Herstart je IDE
8. Start je localhost op met PgAdmin om een lokale server te draaien
9. load het Maven build script
10. download een JDK: deze web-API is getest en gemaakt binnen Amazon Corretto v21. Deze raad ik dan ook aan.
11. Pas het wachtwoord en spring.datasource.url aan in main > application.properties. Deze moeten verwijzen naar je eigen instellingen van postgresql en pgAdmin
12. Run "EindprojectBackendApplication" in dan map main > java > nl.novi.eindprojectbackend


## Accounts

Deze web-API beveiligd rollen met behulp van JWT. Je kan zelf nieuwe accounts aanmaken en deze een rol geven voor het opstarten van de web-APi met behulp van de data.sql.
Deze bevind zich in de map main > resources

Je kan hierbij ook kiezen voor 1 van de eerder aangemaakte rollen: hier hoef je dan enkel het wachtwoord voor aan te passen bij de users. Om een nieuw beveiligd wachtwoord aan te maken,
ga naar java> nl.novi.eindprojectbackend > util > PasswordEncoderUtil. Vervang "PASSWORD_HERE" met je eigen test password, en kopieer dit versleutelde password uit de terminal in de data.sql

De aangemaakte rollen:

1. 'monteur1', rol = Monteur
2. 'medewerker1', rol = Medewerker
3. 'klant1', rol = Klant
4. 'klant2', rol = Klant


## Postman

Er is een postman collectie meegeleverd in de zip file: deze staat in json formaat. Dit kun je terug importeren in postman via de volgende stappen:

1. start postman op
2. Ga naar de optie 'Importeren', in de linkerbovenhoek van het Postman-venster
3. Zoek en selecteer het bijgeleverde JSON-bestand
4. Postman zal een voorbeeld van de collectie weergeven.Klik op de knop Importeren onder aan het dialoogvenster om het importproces te voltooien.
5. Na het importeren zal de collectie in de linkerzijbalk onder het tabblad Collecties verschijnen.

Je kunt hier bij de drie aangemaakte POST's van Klant1, Monteur1 en Medewerker 1 je eigen wachtwoord invullen.
Hieruit ontvang je een jwt.
Deze JWT kan je bij iedere Authorization van iedere andere POST/GET/PATCH gebruiken

## REST-endpoints

Hierbij tevens een lijst van de REST-endpoints

1) Register User: /api/users/register
* {
  "username": "klant12",
  "password": "Klant",
  "email": "klant12@example.com"
  }
* Regristreert nieuwe gebruikers als rol klant. Geen beveiliging, iedereen kan een nieuw account aanmaken.

2) /authenticate
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

6) POST /api/cars/[carID]/repairs
* {
  "repairTypeId": 1,
  "repairRequestDate": "20-01-2025",
  "repairDate": "25-01-2025",
  "partIds": [1,2]
  }
*  Een reperatie bij een auto toevoegen. Dit is enkel mogelijk met een jwt met de rol monteur, en als er al een auto aanwezig is met het ID

7) PATCH /api/cars/1/repairs/[repairID]
* {
  "repairRequestDate": "20-01-2025"
  }
* een gedeelte van een auto zijn reperatie informatie aanpassen.  Dit is enkel mogelijk met een jwt met de rol monteur, en als er al een auto aanwezig is met het ID

8) DELETE /api/cars/[repairID]
* Delete een auto. Dit is enkel mogelijk met een jwt met de rol medewerker of monteur

9) POST /api/parts
* {
  "name": "Steering Wheel",
  "price": 200.0,
  "stock": 5
  }
* voegt een onderdeel toe. Dit is enkel mogelijk met een jwt met de rol medewerker of monteur

10) Patch en PUT /api/parts/[partID]
* {
  "stock": 60
  }
* past informatie van een onderdeel aan. Dit is enkel mogelijk met een jwt met de rol medewerker of monteur.

11) GET /api/parts
* zie alle onderdelen. Dit is enkel mogelijk met een jwt met de rol medewerker of monteur

12) POST /api/repair-types
* {
  "name": "Engine Repair",
  "cost": 150.0,
  "description": "Repair related to engine issues."
  }
* voegt een repair type toe. Dit is enkel mogelijk met een jwt met de rol monteur

13) GET /api/repair-types
* zie alle repair-types. Dit is enkel mogelijk met een jwt met de rol monteur

14) PATCH /api/repair-types/[repairtypeID]
* {
  "cost": 550.0
  }
*  past informatie van een repair type aan. Dit is enkel mogelijk met een jwt met de rol monteur.

15) /api/pdfs/upload/[pdfID]
* uploadt een pdf naar een atuo. De auto moet bestaan. Dit is enkel mogelijk met een jwt met de rol klant.

16) /api/pdfs/download/[pdfID]
* download een pdf. dit is mogelijk met een jwt van klant, medewerker en monteur.

17) /api/users/create-user?role=MONTEUR
* "username": "monteur12",
  "password": "monteur",
  "email": "monteur@example.com"
  }
* Dit creert een nieuwe monteur rol. Dit is enkel mogelijk met een jwt met de rol medewerker.

Alle endpoints zijn restricted met behulp van de SpringSecurityConfig: de rollen bevinden zich binnen de JWT in iedere ingelogde gebruiker.

## Tests

Er zijn integration en service tests aanwezig in de Test map. Deze zijn ieder apart te runnen.



