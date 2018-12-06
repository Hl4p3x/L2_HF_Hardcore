package com.l2jserver.script.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.script.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

class JavaCompilerTest {

    @Test
    void simpleTestCompile() throws ScriptException, FileNotFoundException {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension("java");

        File testPath = new File("src/test/java/com/l2jserver/script/java");
        File testFilePath = new File(testPath.getAbsolutePath() + "/SimpleTest.java");
        Assertions.assertTrue(testPath.exists());
        Assertions.assertTrue(testFilePath.exists());
        Assertions.assertTrue(testFilePath.isFile());

        ScriptContext context = new SimpleScriptContext();
        context.setAttribute("mainClass", "script.java.SimpleTest", ScriptContext.ENGINE_SCOPE);
        context.setAttribute(ScriptEngine.FILENAME, "script/java/SimpleTest.java", ScriptContext.ENGINE_SCOPE);
        context.setAttribute("classpath", testPath.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);
        context.setAttribute("sourcepath", testPath.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);

        FileInputStream fileInputStream = new FileInputStream(testFilePath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

        scriptEngine.setContext(context);
        Compilable eng = (Compilable) scriptEngine;
        CompiledScript cs = eng.compile(inputStreamReader);
        Object object = cs.eval(context);

        Assertions.assertTrue(((Class<?>) object).isAssignableFrom(SimpleTest.class));
    }


    @Test
    void classpathDepedencyTestCompile() throws ScriptException, FileNotFoundException {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension("java");

        File testPath = new File("src/test/java/com/l2jserver/script/java");
        File testFilePath = new File(testPath.getAbsolutePath() + "/ClasspathDependencyTest.java");
        Assertions.assertTrue(testPath.exists());
        Assertions.assertTrue(testFilePath.exists());
        Assertions.assertTrue(testFilePath.isFile());

        ScriptContext context = new SimpleScriptContext();
        context.setAttribute("mainClass", "script.java.ClasspathDependencyTest", ScriptContext.ENGINE_SCOPE);
        context.setAttribute(ScriptEngine.FILENAME, "script/java/ClasspathDependencyTest.java", ScriptContext.ENGINE_SCOPE);
        context.setAttribute("classpath", testPath.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);
        context.setAttribute("sourcepath", testPath.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);

        FileInputStream fileInputStream = new FileInputStream(testFilePath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

        scriptEngine.setContext(context);
        Compilable eng = (Compilable) scriptEngine;
        CompiledScript cs = eng.compile(inputStreamReader);
        Object object = cs.eval(context);

        Assertions.assertNotNull(object);
    }

}
