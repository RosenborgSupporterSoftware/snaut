# snaut - RBKSupporterSoftwares Sol og Nedbør-Automat

Automatisk generering av Yr.no vær-melding for RBKs kamptidspunkt.

# bygging

Installer "sbt" (simple build tool)

- last ned alle dependencies
 > $ sbt update

- bygg src/main/
 > $ sbt compile

- kjør testene under src/test/
 > $ sbt test

- start sbt i interaktiv mode
 > $ sbt

- sett opp sbt til å monitorere filendringer og bygge/kjøre testsuiten etter hver endring
 > sbt> ~ test

- exit sbt
 > sbt> exit
