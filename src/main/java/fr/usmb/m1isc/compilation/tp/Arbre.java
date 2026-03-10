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
    public void genCode(CodeGen gen) {
        if (nom.equalsIgnoreCase("input")) {
            gen.emit("in eax");
        } else {
            gen.emit("mov eax, " + nom);
        }
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

        if (operateur.equals("WHILE")) {
            String start = gen.newLabel("debut_while");
            String end = gen.newLabel("fin_while");
            gen.emitWithoutTab(start + ":");
            gauche.genCode(gen); // La condition met 1 ou 0 dans eax
            gen.emit("jz " + end);
            droite.genCode(gen);
            gen.emit("jmp " + start);
            gen.emitWithoutTab(end + ":");
            return;
        }

        if (operateur.equals("IF")) {
            String elseLbl = gen.newLabel("else");
            String finLbl = gen.newLabel("fin_if");
            gauche.genCode(gen);
            gen.emit("jz " + elseLbl);
            ((ArbreBinaire) droite).gauche.genCode(gen);
            gen.emit("jmp " + finLbl);
            gen.emitWithoutTab(elseLbl + ":");
            ((ArbreBinaire) droite).droite.genCode(gen);
            gen.emitWithoutTab(finLbl + ":");
            return;
        }

        gauche.genCode(gen);     // eax = gauche
        gen.emit("push eax");

        droite.genCode(gen);     // eax = droite
        gen.emit("pop ebx");     // ebx = gauche

        String vrai;
        String fin;

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
            case "%":
                gen.emit("mov ecx, eax");
                gen.emit("mov eax, ebx");
                gen.emit("div ebx, ecx");
                gen.emit("mul ebx, ecx");
                gen.emit("sub eax, ebx");
                break;
            case "<":
                vrai = gen.newLabel("vrai");
                fin  = gen.newLabel("fin");

                gen.emit("sub ebx, eax");
                gen.emit("jl " + vrai);  // jump if less
                gen.emit("mov eax, 0");
                gen.emit("jmp " + fin);
                gen.emitWithoutTab(vrai + ":");
                gen.emit("mov eax, 1");
                gen.emitWithoutTab(fin + ":");
                break;
            case ">": {
                vrai = gen.newLabel("vrai_sup");
                fin  = gen.newLabel("fin_sup");

                gen.emit("sub ebx, eax");
                gen.emit("jg " + vrai);   // jump if greater
                gen.emit("mov eax, 0");
                gen.emit("jmp " + fin);
                gen.emitWithoutTab(vrai + ":");
                gen.emit("mov eax, 1");
                gen.emitWithoutTab(fin + ":");
                break;
            }
            case ">=": {
                vrai = gen.newLabel("vrai_sup_egal");
                fin  = gen.newLabel("fin_sup_egal");

                gen.emit("sub ebx, eax");
                gen.emit("jge " + vrai);  // jump if greater or equal
                gen.emit("mov eax, 0");
                gen.emit("jmp " + fin);
                gen.emitWithoutTab(vrai + ":");
                gen.emit("mov eax, 1");
                gen.emitWithoutTab(fin + ":");
                break;
            }
            case "<=": {
                vrai = gen.newLabel("vrai_inf_egal");
                fin  = gen.newLabel("fin_inf_egal");

                gen.emit("sub ebx, eax");
                gen.emit("jle " + vrai);  // jump if less or equal
                gen.emit("mov eax, 0");
                gen.emit("jmp " + fin);
                gen.emitWithoutTab(vrai + ":");
                gen.emit("mov eax, 1");
                gen.emitWithoutTab(fin + ":");
                break;
            }
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

    public void genCode(CodeGen gen) {
    if (operateur.equals("OUTPUT")) {
        expression.genCode(gen);
        gen.emit("out eax");
    } else if (operateur.equals("INPUT")) {
        gen.emit("in eax");
    }
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

    public void genCode(CodeGen gen) {
        gen.declareVar(nomVariable);   // DATA SEGMENT
        valeur.genCode(gen);           // eax = valeur
        gen.emit("mov " + nomVariable + ", eax");
    }
}