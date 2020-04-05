package com.l2jserver.script.java;

import java.util.Collections;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

public class JavaEngineFactory implements ScriptEngineFactory {

    private static long nextClassNum = 0L;

    private static List<String> names = List.of("java");
    private static List<String> extensions = names;
    private static List<String> mimeTypes = Collections.emptyList();

    public JavaEngineFactory() {
    }

    public String getEngineName() {
        return "java";
    }

    public String getEngineVersion() {
        return "1.11";
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public String getLanguageName() {
        return "java";
    }

    public String getLanguageVersion() {
        return "1.11";
    }

    public String getMethodCallSyntax(String obj, String m, String... args) {
        StringBuilder buf = new StringBuilder();
        buf.append(obj);
        buf.append('.');
        buf.append(m);
        buf.append('(');
        if (args.length != 0) {
            int i;
            for (i = 0; i < args.length - 1; ++i) {
                buf.append(args[i]).append(", ");
            }

            buf.append(args[i]);
        }

        buf.append(')');
        return buf.toString();
    }

    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    public List<String> getNames() {
        return names;
    }

    public String getOutputStatement(String toDisplay) {
        StringBuilder buf = new StringBuilder();
        buf.append("System.out.print(\"");
        int len = toDisplay.length();

        for (int i = 0; i < len; ++i) {
            char ch = toDisplay.charAt(i);
            switch (ch) {
                case '"':
                    buf.append("\\\"");
                    break;
                case '\\':
                    buf.append("\\\\");
                    break;
                default:
                    buf.append(ch);
            }
        }

        buf.append("\");");
        return buf.toString();
    }

    public String getParameter(String key) {
        switch (key) {
            case "javax.script.engine":
                return this.getEngineName();
            case "javax.script.engine_version":
                return this.getEngineVersion();
            case "javax.script.name":
                return this.getEngineName();
            case "javax.script.language":
                return this.getLanguageName();
            case "javax.script.language_version":
                return this.getLanguageVersion();
            default:
                return key.equals("THREADING") ? "MULTITHREADED" : null;
        }
    }

    public String getProgram(String... statements) {
        StringBuilder buf = new StringBuilder();
        buf.append("class ");
        buf.append(getClassName());
        buf.append(" {\n");
        buf.append("    public static void main(String[] args) {\n");
        if (statements.length != 0) {
            for (String statement : statements) {
                buf.append("        ");
                buf.append(statement);
                buf.append(";\n");
            }
        }

        buf.append("    }\n");
        buf.append("}\n");
        return buf.toString();
    }

    public ScriptEngine getScriptEngine() {
        JavaEngine engine = new JavaEngine();
        engine.setFactory(this);
        return engine;
    }

    private String getClassName() {
        return "com_sun_script_java_Main$" + getNextClassNumber();
    }

    private static synchronized long getNextClassNumber() {
        return nextClassNum++;
    }

}
