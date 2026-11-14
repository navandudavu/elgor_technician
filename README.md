# ELGOR Technician — Android aplikacija

Nativna Android aplikacija (Kotlin + Jetpack Compose) za ELGOR servisere, administratore i menadžere, koja radi sa **ServiceHub Java serverom** (repozitorijum `servicehub`): radni nalozi, statusi, izvršeni radovi, konstatacije, fotografije, zaduživanje materijala i obaveštenja o novim nalozima. Odvojena je od ELGOR aplikacije za korisnike (elgor1999), koja služi klijentima za pregled i kupovinu; ova je alat za internu upotrebu.

## Status: verzija 1.0 (`versionCode 1`)

Aplikacija se kompajlira i radi na uređaju (potvrdio razvojni tim). Poslednja serija izmena — izvršeni radovi koji se menjaju na ekranu naloga, zakazivanje samo datumom i štampa zaduženja — još **nije isprobana u Android Studiju**. Prva stvar posle `git pull`: **Build → Make Project**, pa prođite kroz tok iz odeljka **Šta proveriti posle izmena**.

## Funkcionalnosti

Prijava (ServiceHub nalog: serviser, menadžer ili administrator), pa lista **radnih naloga** (`/api/nalozi`). Postoji samo jedan server (Java); stari Node server više ne postoji. Ako je na telefonu sačuvana adresa koja se završava na `:4000`, aplikacija je preusmerava na port `8081` (`NetworkConfig.nalogBaseUrl`).

**Uloge u aplikaciji:** serviser vidi samo naloge dodeljene njemu i ne pravi nove; **administrator** i **menadžer** imaju ista prava (menadžer nema karticu *Tim*, a ona ni nije u aplikaciji).

- **Lista i detalj naloga:** status, istorija statusa, klijent (poziv i navigacija), uređaj, dijagnoza, **izvršeni radovi**, zakazani datum, delovi, sati rada, cena i način plaćanja.
- **Statusi:** *Dodeljen* → radni statusi *U toku*, *Čeka delove*, *Preuzet u servis*, *Testira se*, *Vozi se*, *Potreban elektroničar* (između njih se prelazi slobodno) → *Završen*. Otkazivanje samo administrator.
- **Zatvaranje naloga:** server ne dozvoljava *Završen* bez bar jedne fotografije „Pre“, bar jedne „Posle“ i opisa izvršenih radova; aplikacija prikazuje poruku servera.
- **Izvršeni radovi:** prikazani na svakom ekranu naloga (detalj i izmena), a **menjaju se samo na ekranu naloga** dugmetom „Izmeni“ (dijalog; `PATCH /api/nalozi/{id}/work-done`). Dodeljeni serviser i administrator mogu; na završenom nalogu samo administrator; na otkazanom niko. Na ekranu „Izmeni nalog“ su samo prikazani.
- **Konstatacije** (ranije beleške): lista i dodavanje (svi koji vide nalog); brisanje samo administrator. Na **završenom** nalogu su samo za čitanje (ni dodavanje ni brisanje, ni administratoru).
- **Fotografije:** snimanje ili izbor iz galerije, kategorije *Pre*, *Posle*, *Nalepnica / SN*, *Radni nalog*, *Ostalo*. Pre slanja se slika smanjuje (najviše 1920 px, JPEG; `util/ImagePrep.kt`). Brisanje: administrator ili ko je poslao.
- **Novi nalog** (samo administrator i menadžer): klijent (pretraga postojećeg ili novi), uređaj, opis kvara, delovi i rad, **cena** (jedan ručno upisan iznos), plaćanje, planirani dolazak (**samo datum**, bez sata), izbor servisera i početnog statusa. Vreme rada se bira dugmadima Sati i Minuti (korak 15 minuta), telefon samo pretvara u „1.50“ za server. Jedan `Idempotency-Key` po formi, pa ponovni pokušaj posle prekida ne pravi dupli nalog.
- **Izmena naloga** (nezavršeni nalozi; administrator bilo koji, serviser samo svoj): uređaj, dijagnoza, delovi (dodavanje iz magacina, količina, uklanjanje), sati, cena, plaćanje. Ako je neko drugi u međuvremenu izmenio nalog, server vraća 409 i aplikacija traži ponovno otvaranje. Podaci o klijentu se menjaju samo na vebu.
- **Brisanje naloga:** samo administrator, ne za završene (server vraća delove na stanje).
- **Zaduživanje** (dugme „Zaduživanje“ na listi): lista materijala koji serviser nosi (šifra nije obavezna, naziv, količina; najviše 50 stavki). Serviser vodi svoju listu, administrator i menadžer biraju servisera. Stavku možete izabrati iz **magacina** (pretraga po nazivu ili šifri; kopiraju se šifra i naziv, zaliha se **ne menja**) ili upisati slobodno. Ikonica štampača otvara **sistemski dijalog za štampu** (A4 ili „Sačuvaj kao PDF“).
- **Novac** se prikazuje isključivo kao string koji pošalje server — telefon nikada ne računa.

**Još nema:** štampa naloga i reversa sa telefona i Bluetooth A4 štampa (radi se poslednja, kad se zna model štampača).

**Zaostatak:** dugme „Stari poslovi“ na listi i ekrani za stare poslove (`JobListScreen`, `JobDetailScreen`, Room baza u `data/local`) ostali su u kodu, ali njihovi endpointi (`/api/jobs`) više ne postoje na Java serveru, pa ne rade. Mogu se ukloniti.

## Obaveštenja (push)

Kada administrator dodeli nalog serviseru, telefon dobija obaveštenje „Novi radni nalog“; dodir otvara taj nalog. Server šalje samo „data“ poruke, pa aplikacija sama prikazuje obaveštenje (i u pozadini). Server svakog punog sata od 08:00 do 19:00 (pon–sub) šalje i podsetnik serviserima koji imaju otvorene naloge: „Izaberite trenutni status svakog naloga“.

Potrebno je da `app/google-services.json` pripada istom Firebase projektu kao i `firebase-service-account.json` na serveru (pogledajte `deploy/DEPLOY.md` u repozitorijumu `servicehub`).

## Istek sesije

Svaki odgovor 401 (istekao token, promenjena ili resetovana lozinka, deaktiviran nalog) briše sačuvani token i aplikacija se vraća na ekran za prijavu uz poruku „Sesija je istekla. Prijavite se ponovo.“ Adresa servera se pamti.

## Pre upotrebe: adresa servera

Aplikacija nema unapred upisanu adresu servera. Pri prvom pokretanju traži se unos — tokom razvoja to je IP adresa računara u lokalnoj mreži i ServiceHub port, npr. `http://192.168.1.100:8081` (nemojte koristiti `localhost` — to bi usmerilo telefon na samog sebe; na emulatoru koristite `10.0.2.2`). Kada se ServiceHub postavi na server, to je njegova stalna adresa (preko Caddy-ja nema porta). Adresa se čuva na telefonu (DataStore), pa je ovaj korak potreban samo jednom po instalaciji.

## Dozvoljen je HTTP bez enkripcije (za sada)

`app/src/main/res/xml/network_security_config.xml` dozvoljava obične `http://` veze, za razliku od ELGOR aplikacije za korisnike koja ih blokira. To je namerno dok ServiceHub radi preko lokalnog HTTP-a bez sertifikata; kad pređe na pravi HTTPS, pravila treba pooštriti. U fajlu postoji komentar koji tačno objašnjava šta treba izmeniti.

## Tehnološki stek

Kotlin, Jetpack Compose + Material3 (`compose-bom 2024.06.00`), Navigation Compose, Retrofit + Moshi, korutine + ViewModel, DataStore (JWT i adresa servera između pokretanja), Firebase Cloud Messaging (push), Room (samo za stare poslove, vidi **Zaostatak**), Coil (dostupan; fotografije naloga se učitavaju ručno, vidi dole). `compileSdk` 34, `minSdk` 24.

`compose-bom` je fiksiran na `2024.06.00`. `PullToRefreshBox` traži Material3 1.3.0+ i nije dostupan u toj verziji, pa se lista osvežava dugmetom umesto da se menja BOM (što bi tražilo i veći `compileSdk` zbog jedne poželjne funkcije).

## Zašto se za fotografije ne koristi standardno učitavanje putem Coil-a

Endpoint za fotografije (`GET /api/nalozi/{id}/photos/{photoId}/file`) traži JWT u `Authorization` zaglavlju, a običan Coil `AsyncImage` ne može da ga doda, pa bi dobio 401. Zato `NalogPhotoImage` (u `ui/screens/NalogDetailScreen.kt`) preuzima sirove bajtove preko istog autentifikovanog Retrofit klijenta kao i svi ostali zahtevi i ručno ih dekodira u bitmapu. Veb aplikacija radi na isti način.

## Podešavanje u Android Studiju

1. Otvorite ovu fasciklu kao projekat. Android Studio sam pravi *Gradle wrapper* JAR pri sinhronizaciji (binarna datoteka nije u repozitorijumu).
2. Sačekajte da Gradle završi sinhronizaciju.
3. **Build → Make Project**.
4. Pokrenite na uređaju ili emulatoru koji vidi ServiceHub server.
5. Unesite adresu servera i prijavite se nalogom koji postoji u ServiceHub-u. Prvog administratora pravi server pri prvom pokretanju (lozinka se jednom ispisuje u logu, vidi README u `servicehub`); servisere i menadžere dodaje administrator na kartici *Tim* u veb aplikaciji.

Unit test: `HoursPickerTest` (`./gradlew test`).

## Šta proveriti posle izmena

Na pravom uređaju, sa serverom na grani koja ima novi endpoint `work-done` (`servicehub`, grana `claude/closing-and-small` ili `master` posle spajanja):

1. Nalog → „Izmeni“ pod *Izvršeni radovi* → upis, čuvanje; na ekranu „Izmeni nalog“ polje je samo prikazano.
2. Završetak naloga bez fotografije „Posle“ mora biti odbijen porukom servera.
3. Novi nalog (administrator): izbor datuma bez sata; na detalju piše samo datum.
4. Zaduživanje → štampač → dijalog za štampu / PDF.
5. Cena i novi statusi na detalju i izmeni naloga.

## Izdanje za produkciju

Potpisan APK sa stalnim ključem (keystore se čuva i pravi rezervna kopija), uvećan `versionCode` pri svakom izdanju, pooštrena mrežna pravila (bez `http://`) i prava adresa servera umesto razvojne IP adrese (`NetworkConfig`).
