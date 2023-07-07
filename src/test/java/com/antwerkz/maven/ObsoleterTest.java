package com.antwerkz.maven;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ObsoleterTest {
    Path root;

    public ObsoleterTest() {
        root = new File("target/test-classes").toPath();
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldHideMethod() throws IOException, ReflectiveOperationException {
        check(false, "dummy", "yo!");
        check(false, "forever", "forever");
        Obsoleter.rewriteAllFiles(root);
        check(true, "dummy", "yo!");
        check(false, "forever", "forever");
    }

    private void check(boolean synthetic, String methodName, String returnValue) throws MalformedURLException, ReflectiveOperationException {
        Class<?> klass = loadClass();
        Method method = klass.getMethod(methodName);
        assertEquals(method.isSynthetic(), synthetic);
        assertEquals(returnValue, method.invoke(klass.newInstance()));
    }

    private Class<?> loadClass() throws MalformedURLException, ClassNotFoundException {
        URLClassLoader loader = new URLClassLoader(new URL[] {root.toUri().toURL()}, null);
        Class<?> klass = loader.loadClass(ObsoletedMethods.class.getName());
        return klass;
    }
}
