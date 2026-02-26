package fr.usmb.m1isc.compilation.tp;

import java.util.HashSet;

public class CodeGen {
    private StringBuilder data = new StringBuilder();
    private StringBuilder code = new StringBuilder();
    private HashSet<String> declaredVars = new HashSet<>();
    private int labelCounter = 0;

    public String newLabel(String base) {
        return base + "_" + (labelCounter++);
    }

    public void emit(String instr) {
        code.append("\t").append(instr).append("\n");
    }

    public void emitWithoutTab(String instr) {
        code.append(instr).append("\n");
    }

    public void declareVar(String name) {
        if (!declaredVars.contains(name)) {
            declaredVars.add(name);
            data.append("\t").append(name).append(" DD\n");
        }
    }

    public String getProgram() {
        return "DATA SEGMENT\n" +
               data +
               "DATA ENDS\n\n" +
               "CODE SEGMENT\n" +
               code +
               "CODE ENDS\n";
    }
}