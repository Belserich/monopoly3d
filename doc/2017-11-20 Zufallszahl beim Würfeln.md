# \<Einigung über Zufallszahl beim Würfeln\>

**User story:**\<Würfeln / MONOPOLY-43\>

<\Gemeinsame Einigung über Zufallszahlerzeugung beim Würfeln.\>

## Considered Alternatives

* \Math.random()\
* \Random Klasse mit SEED\

## Decision Outcome

* **Chosen Alternative:** \<Random Klasse mit SEED\>
* **Rationale:** \<Damit die FAT-Clients asynchron arbeiten können, ohne Zufallsergebnisse übertragen zu müssen.\>
