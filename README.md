# snaut - RBKSupporterSoftwares Sol og Nedbør-Automat

Automatisk generering av Yr.no vær-melding for RBKs kamptidspunkt.

# bygging

Installer "sbt" (simple build tool)

 > $ sbt update
- laster ned alle dependencies
 > $ sbt compile
- bygger src/main/
 > $ sbt test
- kjører testene under src/test/
 > $ sbt
- starter sbt i interaktiv mode
 > > ~ test
- sette ropp sbt til å monitorere filendringer og bygge/kjøre testsuiten etter hver endring
