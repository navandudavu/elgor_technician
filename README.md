# ELGOR Technician — Android aplikacija

Nativna Android aplikacija namenjena ELGOR tehničarima za rad na dodeljenim
ServiceHub zadacima: pregled zadataka, promena statusa, evidentiranje sati rada,
dodavanje beleški i otpremanje fotografija. Ovo je aplikacija odvojena od
ELGOR aplikacije za korisnike (elgor1999) — ona je namenjena klijentima za
pregledanje i kupovinu, dok je ova alat za internu upotrebu servisera i
komunicira sa istom ServiceHub pozadinom koju koristi i veb-kontrolna tabla.

## Status: verzija v1.0

Važi ista napomena kao i za originalnu ELGOR aplikaciju za korisnike:
u okruženju u kojem je ova aplikacija kreirana nisu bili dostupni Android SDK
ni emulator, tako da ona **nikada nije kompajlirana**. Svaki fajl je ručno
proveren — deklaracije paketa odgovaraju fasciklama, zagrade su pravilno
uparene, nema oštećenih karaktera (jedan takav je otkriven i ispravljen tokom
razvoja, videti ispod), a Material3 API-ji su provereni u odnosu na
specifičnu verziju `compose-bom`-a koju ovaj projekat koristi — ali ništa
od toga ne može zameniti stvarno izvršavanje komande `./gradlew assembleDebug`.
To je prva stvar koju treba uraditi nakon otvaranja projekta u Android Studio-u.

## Funkcionalnosti

- Prijavljivanje pomoću ServiceHub naloga (prihvataju se akreditivi i
  tehničara i administratora, ali je aplikacija ograničena isključivo na
  aktivnosti tehničara)
- Pregled dodeljenih zadataka
- Rad na zadatku: promena statusa (na čekanju → dodeljeno → u toku →
  završeno), evidentiranje sati rada, dodavanje beleški, snimanje ili izbor
  fotografije i njeno otpremanje, brisanje otpremljenih fotografija
- Ekran za prvo pokretanje radi unosa adrese ServiceHub servera, s obzirom na
  to da je aplikacija usmerena ka serveru koji još uvek nije trajno
  deplojvan (pogledajte ispod)

## Pre upotrebe: adresa servera

Ova aplikacija nema unapred upisanu URL adresu pozadinskog servera (backend-a).
Prilikom prvog pokretanja traži se unos adrese — tokom razvoja to je IP adresa
vašeg računara u lokalnoj mreži i ServiceHub port, npr. `http://192.168.1.100:4000`
(pronađite LAN IP adresu svog računara; nemojte koristiti `localhost` — to bi
usmerilo telefon na samog sebe, a ne na vaš računar). Kada se ServiceHub postavi u stvarno okruženje, ta adresa postaje
stalna javna URL adresa.

Adresa se čuva na telefonu (u DataStore-u), tako da je ovaj korak potreban samo jednom
prilikom instalacije.

## Dozvoljen je HTTP bez enkripcije (za sada)

Fajl `app/src/main/res/xml/network_security_config.xml` dozvoljava obične `http://`
veze, za razliku od ELGOR aplikacije za korisnike koja ih blokira. Ovo je
namerno i neophodno dok ServiceHub radi preko lokalnog HTTP-a bez
sertifikata — ali bi bezbednosna pravila trebalo pooštriti kada ServiceHub
pređe na pravi HTTPS. U fajlu postoji komentar koji tačno objašnjava šta treba izmeniti.

## Tehnološki stek

Prati izbore napravljene za postojeću ELGOR aplikaciju za korisnike tamo gde je
imalo smisla ponovo ih upotrebiti: Kotlin, Jetpack Compose + Material3, Navigation Compose,
Retrofit + Moshi za API, Coil (dostupan, mada se ovde za učitavanje fotografija
koristi ručni pristup sa autentifikacijom — videti ispod),
korutine (coroutines) + ViewModel za upravljanje stanjem. DataStore je dodat radi
čuvanja JWT-a i URL-a servera između pokretanja aplikacije (aplikaciji za korisnike
to nije bilo potrebno jer nema prijavljivanje).

Verzija `compose-bom` je fiksirana na `2024.06.00` kako bi se poklopila sa verzijom
u aplikaciji za korisnike. Jedan Material3 API (`PullToRefreshBox`) zahtevao je
Material3 1.3.0+ i nije dostupan u ovoj BOM verziji — stoga lista poslova
koristi dugme za ručno osvežavanje umesto da se menja BOM verzija (što bi
zahtevalo i povećanje `compileSdk` verzije, a to nije vredno truda zbog jedne
funkcije koja je samo poželjna, a ne i neophodna).

## Zašto se za fotografije ne koristi standardno učitavanje putem Coil-a

Endpoint za fotografije u ServiceHub-u zahteva JWT u Authorization zaglavlju
(`GET /api/jobs/photos/:filename`, videti `requireAuth`
midlver na bekendu) — standardni mehanizam za učitavanje slika nema način da
doda to zaglavlje, tako da bi običan Coil `AsyncImage` usmeren ka tom URL-u
jednostavno dobio grešku 401. To je isti osnovni problem sa kojim se suočila
i veb aplikacija i rešila ga na isti način: `JobPhoto.kt`
preuzima sirove bajtove putem istog Retrofit klijenta sa autentifikacijom
koji se koristi za sve ostale zahteve, a zatim ih ručno dekodira u bitmapu. ## Podešavanje u Android Studio-u

1. Otvorite ovu fasciklu kao projekat u Android Studio-u (automatski će se ponovo generisati *Gradle wrapper* JAR datoteka prilikom sinhronizacije – ta binarna datoteka se ne može generisati u ovom okruženju jer ne postoji mrežna veza ka *Gradle* serverima; to je isto ograničenje koje je postojalo prilikom izgradnje aplikacije za klijenta).
2. Sačekajte da *Gradle* završi sinhronizaciju i razreši zavisnosti.
3. Izaberite **Build → Make Project**. Ovo je pravi prvi test – do sada ništa nije kompajlirano.
4. Pokrenite aplikaciju na uređaju ili emulatoru koji se nalazi na istoj mreži kao i vaš *ServiceHub* bekend (ili koristite `10.0.2.2` umesto `localhost` ako testirate bekend koji radi na istom računaru kao i *Android* emulator – to je poseban alijas emulatora za računar domaćina).
5. Unesite adresu servera i prijavite se pomoću naloga tehničara koji je unet u *ServiceHub* (pogledajte skriptu za inicijalno popunjavanje podataka u *ServiceHub* README datoteci).

## Poznati nedostaci / stvari koje prvo treba proveriti

- **Nije kompajlirano** – pogledajte gore, ovo je najvažnija stavka.
- **Nedostaju slojevi za adaptivne ikonice ili slojevi prednjeg plana (foreground)** – koriste se obične PNG ikonice za pokretanje (generisane na osnovu pravog ELGOR logotipa za svih 5 kategorija gustine piksela), a ne XML format za adaptivne ikonice koji koristi aplikacija za klijenta. Jednostavnije je i radi bez problema, samo izgleda manje atraktivno (nema odvojenih slojeva pozadine/prednjeg plana).
