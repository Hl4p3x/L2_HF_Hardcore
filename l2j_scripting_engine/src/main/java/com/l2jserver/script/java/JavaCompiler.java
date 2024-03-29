//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.l2jserver.script.java;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JavaCompiler {

    private final javax.tools.JavaCompiler tool = ToolProvider.getSystemJavaCompiler();

    public Map<String, byte[]> compile(String fileName, String source, Writer err, String sourcePath, String classPath) {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        MemoryJavaFileManager memoryJavaFileManager = new MemoryJavaFileManager();

        List<JavaFileObject> compUnits = new ArrayList<>();
        compUnits.add(MemoryJavaFileManager.makeStringSource(fileName, source));

        List<String> options = new ArrayList<>();
        if (sourcePath != null) {
            options.add("-sourcepath");
            options.add(sourcePath);
        }

        if (classPath != null) {
            options.add("-classpath");
            options.add(classPath);
        }

        CompilationTask task = tool.getTask(err, memoryJavaFileManager, diagnostics, options, null, compUnits);
        if (task.call()) {
            return memoryJavaFileManager.getClassBytes();
        } else {
            PrintWriter errorWriter = new PrintWriter(err);

            for (Diagnostic<? extends JavaFileObject> diagnostic1 : diagnostics.getDiagnostics()) {
                errorWriter.println(diagnostic1.getMessage(Locale.getDefault()));
            }

            errorWriter.flush();
            return null;
        }
    }

}
