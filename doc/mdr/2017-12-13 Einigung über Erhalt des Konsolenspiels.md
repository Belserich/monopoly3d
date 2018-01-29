# \<Einigung über Erhalt des Konsolenspiels\>

**User story:**\<Grafische Oberfläche bereitstellen / MONOPOLY-102\>

<\Entscheidung, ob bei der GUI Implementierung beachtet werden soll, dass über die Global.java die alte Konsolenversion, anstelle der GUI, gestartet werden kann.\>

## Considered Alternatives

* \Konsole wird entfernt\
* \Konsole wird beibehalten\

## Decision Outcome

* **Chosen Alternative:** \<Konsole wird beibehalten\>
* **Rationale:** \<Die Konsolenversion bleibt erhalten, da das Fehlerlösen teils einfacher ist, wenn man die GUI als Fehlerquelle ausschließen kann. Außerdem können so unsere JUnit Test erhalten bleiben.\>
