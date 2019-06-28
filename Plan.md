# brave-clojure-rpg

Text based-rpg with:
- Armor mechanics
- critical hit and stuff
- equipment for attack and defence
- dialog tree system
- text moves based battle mechanic
- multiple weapon choices
- Randomly decided who attacks at the start of a battle
- Enemie's AI

## Armor
Each armor point deflects .1% of damage
## Attacking
You choose weapon with which you'll attack (Dialog tree style)

### Dialog protocol
Consists of functions:
- choose [dialog choice] - to choose between 1 and n choices prints what you choose (returns next dialog)
- display - to print dialog information

### Dialog loop
    (defn start-dialog [dialog]
        (let [next_dialog (choose dialog (input))]
            (if next_dialog
                (display next_dialog)
                (start-dialog next_dialog))
        ))

### Battle dialog
Same functions as protocol,different implementation
Has
- Hero record
- Enemy record
- Battle title
- Battle description
- Winning dialog
- choose - after choosing
    - calculates enemy hp after damage
    - enemy attacks
    - prints enemy attack damage
    - calculates your hp after damage
    - returns battle dialog with updated values
- display - displays Hero hp Enemy hp and list of attacks

### Damage calculation
- calculate-attack [hero enemy attack]
    - calculated:(attack - ((Enemy's armor / 100) * attack))
    - if( random in range 0-5) attack*2 (Critical hit)
    - returns {calculated + attack-type i.e critical hit normal hit}
