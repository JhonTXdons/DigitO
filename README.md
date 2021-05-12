# DigitO

![](https://github.com/JhonTXdons/DigitO/blob/master/Senza%20titolo-1.png)

App sviluppata per la tesi in Ing Informatica e della Automazione

Lo scopo è quello di implementare una applicazione che sia in grado di gestire richieste di auth di accesso a diverse piattafomre informatiche della pubblica amministrazione,
realizzata principalmente come metodo più snello e agile allo Spid o al Choesion

l'app si compone di 3 principali Activity:

# Login

![](https://github.com/JhonTXdons/DigitO/blob/master/WhatsApp%20Image%202021-04-14%20at%2000.51.59.jpeg)

Semplice activity per il login all'applicazione, al suo interno contiene la verifica dei campi e il tasto Accedi richiama una funzione al web service di Firebase per il controllo dell'email e password forniti in fase di registrazione. Ritorna un errore nel caso in cui non siano presenti i campi forniti o se questi sono invalidi nell'inserimento

# Signup

![](https://github.com/JhonTXdons/DigitO/blob/master/WhatsApp%20Image%202021-04-14%20at%2000.51.59%20(1).jpeg)

Semplice activity per il signup (registrazione) all'applicazione, al suo interno contiene la verifica dei campi e il tasto Registrati richiama una funzione al web service di Firebase per l'inserimento di un nuovo utente nel database, l'associazione tra email e password fornite e la creazione di una notifca di benvenuto visibile nella Main Activity. A registrazione avvenuta con successo si accede automaticamente alla Main activity

# Main

![](https://github.com/JhonTXdons/DigitO/blob/master/WhatsApp%20Image%202021-04-14%20at%2014.04.11.jpeg)

piu altre sub activity come Profile e NotifyDisplay
