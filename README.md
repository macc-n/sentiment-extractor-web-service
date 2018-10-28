# Sentiment Extractor Web Service #

Web service per l'estrazione di entità e del relativo sentiment associato presenti in frasi testuali.


### Importare il progetto in Eclipse ###

* Da console, eseguire il comando
	
	```
		git clone https://github.com/macc-n/sentiment-extractor-web-service
	```
	
* Aprire Eclipse

* File > Open Projects from File System...

* Cliccare sul pulsante Directory e selezionare la cartella della repository sentimentextractorwebservice

* In Project Explorer, cliccare con il tasto destro sul file pom.xml > Run As > Maven install

* Attendere che vengano scaricate tutte le dipendenze

* Sempre in Project Explorer, aprire Java Resources > src > sentimentextractor

* Cliccare con il tasto destro su SentimentExtractorService > Run As > Run on Server

* Solo per la prima volta che viene eseguito è necessario selezionare il web server che si desidera utilizzare e si dovrà specificare la directory di installazione

* Terminata la fase di configurazione del web server, cliccare sul pulsante Finish

* Per verificare che tutto sia stato installato correttamente, si può effettuare un test visitando la seguente [url](http://localhost:8080/SentimentExtractorWebService/sentiment?text=I%20love%20Matrix%20but%20I%20hate%20Keanu%20Reeves)

### Richiesta al web service ###

Per poter effettuare la richiesta al servizio si deve utilizzare l'URL secondo il seguente formato:

```
http://{dominio}:{porta}/SentimentExtractorWebService/sentiment?text={testo}
```

Inserendo la frase desiderata al posto di {testo}. Viene restituito un JSON, descritto nella sezione successiva.

### Esempio di richiesta ###

* Chiamata API

```
http://localhost:8080/SentimentExtractorWebService/sentiment?text=I%20love%20Matrix%20but%20I%20hate%20Keanu%20Reeves
```

* JSON restituito

```json
	[
	   {
	      "start":2,
	      "end":2,
	      "uriDBpedia":"http://dbpedia.org/resource/The_Matrix",
	      "label":"The Matrix",
	      "sentiment":4
	   },
	   {
	      "start":6,
	      "end":7,
	      "uriDBpedia":"http://dbpedia.org/resource/Keanu_Reeves",
	      "label":"Keanu Reeves",
	      "sentiment":0
	   }
	]
```

Gli attributi del JSON sono:

* start: il numero di token iniziale dell'entità;
	
* end: il numero di token finale dell'entità;
	
* uriDBpedia: l'URI di riferimento su DBpedia dell'entità estratta;
	
* label: l'etichetta testuale assegnata all'entità;
	
* sentiment: numero intero che va da -1 a 4. -1 indica che il sentiment non è stato calcolato, 0 indica un sentiment molto negativo, 1 indica un sentiment negativo, 2 indica un sentiment neutro, 3 indica un sentiment positivo e 4 indica un sentiment molto positivo.

### Altre informazioni ###
Presentazione Slideshare: https://www.slideshare.net/NicolaMacchiarulo/sentiment-extractor-web-service
