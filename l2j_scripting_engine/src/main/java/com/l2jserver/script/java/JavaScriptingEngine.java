package com.l2jserver.script.java;

import javax.script.*;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JavaScriptingEngine extends AbstractScriptEngine implements Compilable {

    private final JavaCompiler compiler = new JavaCompiler();
    private ScriptEngineFactory factory;

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String ARGUMENTS = "arguments";
    private static final String SOURCEPATH = "sourcepath";
    private static final String CLASSPATH = "classpath";
    private static final String MAINCLASS = "mainClass";
    private static final String PARENTLOADER = "parentLoader";

    public JavaScriptingEngine() {
    }

    public CompiledScript compile(String script) throws ScriptException {
        return this.compile(script, this.context);
    }

    public CompiledScript compile(Reader reader) throws ScriptException {
        return this.compile(this.readFully(reader));
    }

    public Object eval(String str, ScriptContext ctx) throws ScriptException {
        Class<?> clazz = this.parse(str, ctx);
        return evalClass(clazz, ctx);
    }

    public Object eval(Reader reader, ScriptContext ctx) throws ScriptException {
        return this.eval(this.readFully(reader), ctx);
    }

    public ScriptEngineFactory getFactory() {
        synchronized (this) {
            if (this.factory == null) {
                this.factory = new JavaScriptingEngineFactory();
            }
        }

        return this.factory;
    }

    public Bindings createBindings() {
        return new SimpleBindings();
    }

    void setFactory(ScriptEngineFactory factory) {
        this.factory = factory;
    }

    private Class<?> parse(String str, ScriptContext ctx) throws ScriptException {
        String fileName = getFileName(ctx);
        String sourcePath = getSourcePath(ctx);
        String classPath = getClassPath(ctx);

        Writer err = ctx.getErrorWriter();
        if (err == null) {
            err = new StringWriter();
        }

        Map<String, byte[]> classBytes = this.compiler.compile(fileName, str, err, sourcePath, classPath);
        if (classBytes == null) {
            if (err instanceof StringWriter) {
                throw new ScriptException(err.toString());
            } else {
                throw new ScriptException("compilation failed");
            }
        } else {
            MemoryClassLoader loader = new MemoryClassLoader(classBytes, classPath, getParentLoader(ctx));
            return parseMain(loader, ctx);
        }
    }

    protected static Class<?> parseMain(MemoryClassLoader loader, ScriptContext ctx) throws ScriptException {
        String mainClassName = getMainClassName(ctx);
        if (mainClassName != null) {
            try {
                Class<?> clazz = loader.load(mainClassName);
                Method mainMethod = findMainMethod(clazz);
                if (mainMethod == null) {
                    throw new ScriptException("no main method in " + mainClassName);
                } else {
                    return clazz;
                }
            } catch (ClassNotFoundException var6) {
                var6.printStackTrace();
                throw new ScriptException(var6);
            }
        } else {
            Iterable<Class<?>> classes;
            try {
                classes = loader.loadAll();
            } catch (ClassNotFoundException var7) {
                throw new ScriptException(var7);
            }

            Class<?> c = findMainClass(classes);
            if (c != null) {
                return c;
            } else {
                Iterator<Class<?>> itr = classes.iterator();
                return itr.hasNext() ? itr.next() : null;
            }
        }
    }

    private JavaScriptingEngine.JavaCompiledScript compile(String str, ScriptContext ctx) throws ScriptException {
        String fileName = getFileName(ctx);
        String sourcePath = getSourcePath(ctx);
        String classPath = getClassPath(ctx);

        Writer err = ctx.getErrorWriter();
        if (err == null) {
            err = new StringWriter();
        }

        Map<String, byte[]> classBytes = this.compiler.compile(fileName, str, err, sourcePath, classPath);
        if (classBytes == null) {
            if (err instanceof StringWriter) {
                throw new ScriptException((err).toString());
            } else {
                throw new ScriptException("compilation failed");
            }
        } else {
            return new JavaScriptingEngine.JavaCompiledScript(this, classBytes, classPath);
        }
    }

    private static Class<?> findMainClass(Iterable<Class<?>> classes) {
        Iterator var1 = classes.iterator();

        Class clazz;
        while (var1.hasNext()) {
            clazz = (Class) var1.next();
            int modifiers = clazz.getModifiers();
            if (Modifier.isPublic(modifiers)) {
                Method mainMethod = findMainMethod(clazz);
                if (mainMethod != null) {
                    return clazz;
                }
            }
        }

        var1 = classes.iterator();

        Method mainMethod;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            clazz = (Class) var1.next();
            mainMethod = findMainMethod(clazz);
        } while (mainMethod == null);

        return clazz;
    }

    private static Method findMainMethod(Class<?> clazz) {
        try {
            Method mainMethod = clazz.getMethod("main", String[].class);
            int modifiers = mainMethod.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                return mainMethod;
            }
        } catch (NoSuchMethodException var3) {
        }

        return null;
    }

    private static Method findSetScriptContextMethod(Class<?> clazz) {
        try {
            Method setCtxMethod = clazz.getMethod("setScriptContext", ScriptContext.class);
            int modifiers = setCtxMethod.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                return setCtxMethod;
            }
        } catch (NoSuchMethodException var3) {
        }

        return null;
    }

    private static String getFileName(ScriptContext ctx) {
        int scope = ctx.getAttributesScope("javax.script.filename");
        return scope != -1 ? ctx.getAttribute("javax.script.filename", scope).toString() : "$unnamed.java";
    }

    private static String[] getArguments(ScriptContext ctx) {
        int scope = ctx.getAttributesScope(ARGUMENTS);
        if (scope != -1) {
            Object obj = ctx.getAttribute(ARGUMENTS, scope);
            if (obj instanceof String[]) {
                return (String[]) obj;
            }
        }

        return EMPTY_STRING_ARRAY;
    }

    private static String getSourcePath(ScriptContext ctx) {
        int scope = ctx.getAttributesScope(SOURCEPATH);
        return scope != -1 ? ctx.getAttribute(SOURCEPATH).toString() : System.getProperty("com.sun.script.java.sourcepath");
    }

    private static String getSystemClassPath() {
        String res = System.getProperty("com.sun.script.java.classpath");
        if (res == null) {
            return System.getProperty("java.class.path");
        }
        return res;
    }

    private static String getClassPath(ScriptContext ctx) {
        int scope = ctx.getAttributesScope(CLASSPATH);
        String systemClassPath = getSystemClassPath();
        if (scope != -1) {
            return ctx.getAttribute(CLASSPATH).toString() + File.pathSeparator + systemClassPath;
        } else {
            return systemClassPath;
        }
    }

    private static String getMainClassName(ScriptContext ctx) {
        int scope = ctx.getAttributesScope(MAINCLASS);
        return scope != -1 ? ctx.getAttribute(MAINCLASS).toString() : System.getProperty("com.sun.script.java.mainClass");
    }

    private static ClassLoader getParentLoader(ScriptContext ctx) {
        int scope = ctx.getAttributesScope(PARENTLOADER);
        if (scope != -1) {
            Object loader = ctx.getAttribute(PARENTLOADER);
            if (loader instanceof ClassLoader) {
                return (ClassLoader) loader;
            }
        }

        return ClassLoader.getSystemClassLoader();
    }

    protected static Object evalClass(Class<?> clazz, ScriptContext ctx) throws ScriptException {
        ctx.setAttribute("context", ctx, 100);
        if (clazz == null) {
            return null;
        } else {
            try {
                boolean isPublicClazz = Modifier.isPublic(clazz.getModifiers());
                Method setCtxMethod = findSetScriptContextMethod(clazz);
                if (setCtxMethod != null) {
                    if (!isPublicClazz) {
                        setCtxMethod.setAccessible(true);
                    }

                    setCtxMethod.invoke(null, ctx);
                }

                Method mainMethod = findMainMethod(clazz);
                if (mainMethod != null) {
                    if (!isPublicClazz) {
                        mainMethod.setAccessible(true);
                    }

                    String[] args = getArguments(ctx);
                    mainMethod.invoke(null, (Object) args);
                }

                return clazz;
            } catch (Exception e) {
                e.printStackTrace();
                throw new ScriptException(e);
            }
        }
    }

    private String readFully(Reader reader) throws ScriptException {
        char[] arr = new char[8192];
        StringBuilder buf = new StringBuilder();

        int numChars;
        try {
            while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
                buf.append(arr, 0, numChars);
            }
        } catch (IOException var6) {
            throw new ScriptException(var6);
        }

        return buf.toString();
    }

    private static class JavaCompiledScript extends CompiledScript implements Serializable {

        private static final long serialVersionUID = -1187041062323875051L;

        private final transient JavaScriptingEngine _engine;
        private transient Class<?> _class;
        private final Map<String, byte[]> _classBytes;
        private final String _classPath;

        JavaCompiledScript(JavaScriptingEngine engine, Map<String, byte[]> classBytes, String classPath) {
            this._engine = engine;
            this._classBytes = classBytes;
            this._classPath = classPath;
        }

        public ScriptEngine getEngine() {
            return this._engine;
        }

        public Object eval(ScriptContext ctx) throws ScriptException {
            if (this._class == null) {
                Map<String, byte[]> classBytesCopy = new HashMap<>(this._classBytes);
                MemoryClassLoader loader = new MemoryClassLoader(classBytesCopy, this._classPath, JavaScriptingEngine.getParentLoader(ctx));
                this._class = JavaScriptingEngine.parseMain(loader, ctx);
            }

            return JavaScriptingEngine.evalClass(this._class, ctx);
        }

    }

}
