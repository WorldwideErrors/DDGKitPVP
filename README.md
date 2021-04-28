# DDGKITPVP

START MET DE PLUGIN
- Let op dat je de config.yml instelt op jouw database.
- spawn set lobby <- hiermee zet je een de spawn voor de lobby, hier spawnen spelers
- /kit create [name]

---------------------------------------------------------------------------------------------------------------
SPAWN COMMANDO'S
- /spawn tp [name]
spawn tp kan je gebruiken om naar een specifieke spawn te teleporteren, zonder de bordjes te gebruiken.

- /spawn set [name]
spawn set wordt gebruikt om een spawnlocatie in te stellen.

- /spawn modify [name]
spawn modify kan de specifieke spawnlocatie aanpassen. 
Dit kan je bijvoorbeeld gebruiken om de lobby te verplaatsen.

- /spawn remove [name]
spawn remove wordt gebruikt om een spawn te verwijderen.

---------------------------------------------------------------------------------------------------------------
KIT COMMANDO'S
- /kit menu
Het kit menu wordt gebruikt om een speler zijn kit te laten selecteren.
/kit menu is alleen beschikbaar voor mensen die de kits kunnen configureren.
Mits je speler een kit wil selecteren moet hij joinen of respawnen.

- /kit create [name]
Kit save wordt gebruikt om een kit toe te voegen, iedere kit heeft een naam, 
standaard icon, description, slot, hoofdkleur en subkleur.
De inventory van je kit wordt opgeslagen vanuit je eigen inventory.

- /kit modify [name]
Kit modify wordt gebruikt om de inventory van de kit aan te passen. 
Hij schrijft de huidige inventory over met jouw eigen inventory.

- /kit remove [name]
Kit remove wordt gebruikt om een kit te verwijderen. 
Let op! Je moet in de database wel de slots aanpassen van de kits die na de specifieke kit gemaakt zijn.

---------------------------------------------------------------------------------------------------------------
PLAYERSTATS

- Als een speler joined wordt hij opgeslagen in de database.
Als de speler al bekend is laad de server de data van de speler in en geeft hij een scoreboard 
waarin weergegeven wordt hoeveel kills en deaths er zijn gemaakt. Ook wordt er een aantal money aangegeven.
Money krijg je door het killen van spelers.

---------------------------------------------------------------------------------------------------------------
WARP BORDJES
- Mits jij wil dat je kan warpen met een bordje moet je eerst spawns hebben ingesteld, zie SPAWN COMMANDO'S
Je kan een bordje plaatsen of aan de muur hangen.
Zet in de tweede regel [WARP] en in de derde regel van het bordje de naam van de spawnlocatie.
Je kan hiermee spelers laten warpen, let op dat ze in survival moeten staan!
