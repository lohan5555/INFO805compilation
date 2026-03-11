#### INFO-805 - Lenny Loy et Lohan Hébert

## Exécution

Pour générer les sources JFlex et CUP à partir du code source :

```bash
./gradlew build -x test
```

Pour exécuter le compilé le code et généré l'arbre sémantique abstrait :

```bash
java -jar build/libs/compilationTp3-4.jar .\fichier_test_exo_2.txt
```
- fichier_test_tp3_exo_1.txt permet de tester le bon fonctionnement des déclaration de variable, des conditionnelles, des opération mathématique, opérateur de comparaison, des ET/OU et de output.
- fichier_test_tp3_exo_2.txt permet de tester le bon fonctionnement des input et des boucle while.


Pour exécuter le code en langage cible : 

```bash
java -jar vm-0.9.jar code_compile.asm
```

## Structure du TP

- Classe Main : appel les différents composants et retourne les résultats dans les fichier arbre.txt et code_compile.asm

- Classe Arbre : une classe abstraite arbre nous permet d'implémenter des sous-classes pour traiter chaque cas. Cette classe possede une méthode toString permetant d'afficher l'arbre sémantique abstrait, ainsi qu'une méthode genCode permettant de générer le code en langage cible.

- Classe CodeGen : cette classe fournit les méthodes permettant à la classe arbre de généré le code en langage cible