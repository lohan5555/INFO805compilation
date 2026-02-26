package fr.usmb.m1isc.compilation.tp;

// Classe racine abstraite
public abstract class Arbre {
    public abstract String toString();
}

// Pour les nombres (ex: 12)
class ArbreEntier extends Arbre {
    int valeur;
    public ArbreEntier(int v) { this.valeur = v; }
    public String toString() { return String.valueOf(valeur); }
}

// Pour les variables (ex: prixHt)
class ArbreIdent extends Arbre {
    String nom;
    public ArbreIdent(String n) { this.nom = n; }
    public String toString() { return nom; }
}

// Pour les opérations binaires (+, -, *, /, ;, AND, OR...)
class ArbreBinaire extends Arbre {
    String operateur;
    Arbre gauche, droite;
    
    public ArbreBinaire(String op, Arbre g, Arbre d) {
        this.operateur = op;
        this.gauche = g;
        this.droite = d;
    }
    
    public String toString() {
        // Format: (OPERATEUR gauche droite)
        return "(" + operateur + " " + gauche.toString() + " " + droite.toString() + ")";
    }
}

// Pour les opérations unaires (NOT, MOINS unaires, OUTPUT...)
class ArbreUnaire extends Arbre {
    String operateur;
    Arbre expression;
    
    public ArbreUnaire(String op, Arbre e) {
        this.operateur = op;
        this.expression = e;
    }
    
    public String toString() {
        return "(" + operateur + " " + expression.toString() + ")";
    }
}

// Pour la déclaration LET (spécifique car identifiant à gauche)
class ArbreLet extends Arbre {
    String nomVariable;
    Arbre valeur;
    
    public ArbreLet(String nom, Arbre v) {
        this.nomVariable = nom;
        this.valeur = v;
    }
    
    public String toString() {
        return "(LET " + nomVariable + " " + valeur.toString() + ")";
    }
}