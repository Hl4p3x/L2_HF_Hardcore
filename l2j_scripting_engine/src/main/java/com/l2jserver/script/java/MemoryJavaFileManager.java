package com.l2jserver.script.java;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

public final class MemoryJavaFileManager extends JavacFileManager {

    private static final String EXT = ".java";
    private Map<String, byte[]> classBytes = new HashMap<>();

    public MemoryJavaFileManager() {
        super(new Context(), true, null);
    }

    public Map<String, byte[]> getClassBytes() {
        return this.classBytes;
    }

    public void close() {
        this.classBytes = new HashMap<>();
    }

    public void flush() {
    }

    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, Kind kind, FileObject sibling) throws IOException {
        return kind == Kind.CLASS ? new MemoryJavaFileManager.ClassOutputBuffer(className.replace('/', '.')) : super.getJavaFileForOutput(location, className, kind, sibling);
    }

    static JavaFileObject makeStringSource(String name, String code) {
        return new MemoryJavaFileManager.StringInputBuffer(name, code);
    }

    private static URI toURI(String name) {
        File file = new File(name);
        if (file.exists()) {
            return file.toURI();
        } else {
            try {
                StringBuilder newUri = new StringBuilder();
                newUri.append("file:///");
                newUri.append(name.replace('.', '/'));
                if (name.endsWith(".java")) {
                    newUri.replace(newUri.length() - ".java".length(), newUri.length(), ".java");
                }

                return URI.create(newUri.toString());
            } catch (Exception var3) {
                return URI.create("file:///com/sun/script/java/java_source");
            }
        }
    }

    private class ClassOutputBuffer extends SimpleJavaFileObject {
        protected final String name;

        ClassOutputBuffer(String name) {
            super(MemoryJavaFileManager.toURI(name), Kind.CLASS);
            this.name = name;
        }

        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                public void close() throws IOException {
                    this.out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) this.out;
                    MemoryJavaFileManager.this.classBytes.put(ClassOutputBuffer.this.name, bos.toByteArray());
                }
            };
        }
    }

    private static class StringInputBuffer extends SimpleJavaFileObject {
        final String code;

        StringInputBuffer(String name, String code) {
            super(MemoryJavaFileManager.toURI(name), Kind.SOURCE);
            this.code = code;
        }

        public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
            return CharBuffer.wrap(this.code);
        }

        public Reader openReader() {
            return new StringReader(this.code);
        }
    }

}
