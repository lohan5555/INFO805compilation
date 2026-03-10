package fr.usmb.m1isc.compilation.tp;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java_cup.runtime.Symbol;

public class Main {
    public static void main(String[] args) throws Exception {
        LexicalAnalyzer yy;
        
        if (args.length > 0)
            yy = new LexicalAnalyzer(new FileReader(args[0]));
        else
            yy = new LexicalAnalyzer(new InputStreamReader(System.in));

        parser p = new parser(yy);
        
        // On lance l'analyse lexial (JFlex) et syntaxique (CUP)
        Symbol result = p.parse();
        
        // On récupère la racine de l'arbre (qui est dans result.value)
        Arbre arbre = (Arbre) result.value;

        CodeGen gen = new CodeGen();
        //on génère le code en assembleur
        arbre.genCode(gen);
    
        //on enregistre le résultat dans un fichier
        try (PrintWriter writer = new PrintWriter(new FileWriter("code_compile.asm"))) {
            writer.println(gen.getProgram());
        }
        System.out.println(gen.getProgram());
        
        // On affiche l'arbre
        String ast = arbre.toString();
        System.out.println(ast);

        // Écriture dans le fichier arbre.txt
        try (PrintWriter writer = new PrintWriter(new FileWriter("arbre.txt"))) {
            writer.println(ast);
        }

        
    }
}