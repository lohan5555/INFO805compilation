package fr.usmb.m1isc.compilation.tp;

import java.io.FileReader;
import java.io.InputStreamReader;
import java_cup.runtime.Symbol;

public class Main {
    public static void main(String[] args) throws Exception {
        LexicalAnalyzer yy;
        
        if (args.length > 0)
            yy = new LexicalAnalyzer(new FileReader(args[0]));
        else
            yy = new LexicalAnalyzer(new InputStreamReader(System.in));

        parser p = new parser(yy);
        
        // On lance l'analyse
        Symbol result = p.parse();
        
        // On récupère la racine de l'arbre (qui est dans result.value)
        Arbre arbre = (Arbre) result.value;
        
        // On affiche l'arbre
        if (arbre != null) {
            System.out.println(arbre.toString());
        } else {
            System.out.println("Arbre vide ou erreur d'analyse.");
        }
    }
}