package fr.usmb.m1isc.compilation.tp;

// Classe racine abstraite
public abstract class Arbre {
    public abstract String toString();
    public abstract void genCode(CodeGen gen);
}

// Pour les nombres (ex: 12)
class ArbreEntier extends Arbre {
    int valeur;
    public ArbreEntier(int v) { this.valeur = v; }
    public String toString() { return String.valueOf(valeur); }
    public void genCode(CodeGen gen){
        gen.emit("mov eax, " + valeur);
    }
}

// Pour les variables (ex: prixHt)
class ArbreIdent extends Arbre {
    String nom;
    public ArbreIdent(String n) { this.nom = n; }
    public String toString() { return nom; }
    public void genCode(CodeGen gen){
        gen.emit("mov eax, " + nom);
    }
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

    public void genCode(CodeGen gen) {
        if (operateur.equals(";")) {
            gauche.genCode(gen);
            droite.genCode(gen);
            return;
        }

        gauche.genCode(gen);     // eax = gauche
        gen.emit("push eax");

        droite.genCode(gen);     // eax = droite
        gen.emit("pop ebx");     // ebx = gauche

        switch (operateur) {
            case "+":
                gen.emit("add eax, ebx");
                break;
            case "-":
                gen.emit("sub ebx, eax");
                gen.emit("mov eax, ebx");
                break;
            case "*":
                gen.emit("mul eax, ebx");
                break;
            case "/":
                gen.emit("div ebx, eax");
                gen.emit("mov eax, ebx");
                break;
        }
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

    public void genCode(CodeGen gen) {}
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

    public void genCode(CodeGen gen) {
        gen.declareVar(nomVariable);   // DATA SEGMENT
        valeur.genCode(gen);           // eax = valeur
        gen.emit("mov " + nomVariable + ", eax");
    }
}