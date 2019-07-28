# Die drei Fragezeichen Einschlafhilfe
Quellcode meiner App: "Die drei Fragezeichen - Einschlafhilfe"

Link: https://play.google.com/store/apps/details?id=de.msdevs.einschlafhilfe

# Server Parameter:

Beispiel: Folge vorschlagen lassen

https://marvin-stelter.de/ddf/server.php?key=1&extra_eins=1000&extra_zwei=1000&device_id=767750

Parameter &key= (Benötigt)
1: Folgen von 1 - 50
2: Folgen von 1 - 100
3: Folgen von 1 - 150
4: Die dr3i Folgen
5: Benutzerdefiniert (&extra_eins= und &extra_zwei=)
6: Favoriten des Nutzers dazu wird &device_id= benötigt, falls diese nicht mitgesendet wird ist sie automatisch 1

Parameter &extra_eins= und &extra_zwei= (Benötigt)
Wird nur benutzt wenn &key=5 ist, muss trotzdem immer mitgesendet werden.
Diese  Parameter geben die Reichweite an, z.b. &extra_eins=1 und &extra_zwei=100, das Skript gibt nun alle Folgen von 1 - 100 zufällig aus.

Parameter &device_id= (Wird nur bei &key=6 gebraucht, muss aber trotzdem nicht mitgesendet werden.) 
Die device id wird benötigt um die Favoriten Folgen dem Nutzer zu senden. (Damit jeder Nutzer nur seine Favoriten angezeigt bekommt.)
Wenn dieser Parameter nicht mitgesendet wird ist er automatisch 1.

