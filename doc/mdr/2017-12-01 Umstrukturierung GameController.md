# \<Vorschläge Umstrukturierung GameController\>

**User story:**\<Netzwerkunterstützung implementieren / MONOPOLY-94\>

<\Gemeinsame Umstrukturierung GameController.\>

## Considered Alternatives

* \keine Umstrukturierung (eine große Steuerklasse)\
* \Auslagerung verschiedenster Methoden\
* \Aufteilung in verschiedene Services die Spieler und Felddaten verwalten\

## Decision Outcome

* **Chosen Alternative:** \<Aufteilung in verschiedene Services die Spieler und Felddaten verwalten\>
* **Rationale:** \<Aufteilung in mehrere Klassen, da eine bessere Übersicht über das Programm möglich ist und somit mehr Features implementiert werden können. Außerdem können Fehler schneller gefunden und behoben werden.\>
