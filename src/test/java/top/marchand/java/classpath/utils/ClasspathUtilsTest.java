/*
 * Copyright (c) 2018, Christophe Marchand
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * 
 * * Neither the name of maven-catalogBuilder-plugin nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package top.marchand.java.classpath.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 *
 * @author cmarchand
 */
public class ClasspathUtilsTest {

    @BeforeClass
    public static void setLogOn() {
        System.setProperty(ClasspathUtils.LOG_PROPERTY, "true");
    }
    @Test
    public void testJUnitIsIn() throws ClasspathException {
        ClasspathUtils cu = new ClasspathUtils(this.getClass().getClassLoader());
        String junitUri = cu.getArtifactJarUri("org.apache.maven.surefire", "surefire-junit4");
        
        ModuleLayer layer = getClass().getModule().getLayer();
        for(Module m: layer.modules()) {
            System.out.println("Found module "+m.getName());
        }
        System.out.println("module JUnit is "+(Assert.class.getModule().isNamed() ? Assert.class.getModule().getName() : Assert.class.getModule().toString()));
        for(String key: System.getProperties().stringPropertyNames()) {
            System.out.println("sysProp "+key);
        }
        System.out.println("java.class.path="+System.getProperty("java.class.path"));
        System.out.println("jdk.module.path="+System.getProperty("jdk.module.path"));
        assertNotNull("surefire-junit URI is null", junitUri);
    }
    @Test @Ignore
    public void testDirectoryViaCallback() throws ClasspathException {
        NotFoundCallback callback = new NotFoundCallback() {
            @Override
            public String getArtifactJarNotFoundUri(String groupId, String artifactId) throws ClasspathException {
                System.err.println("callback call");
                String marker = groupId+"+"+artifactId;
                if("pouet+art".equals(marker)) return "file:/usr/local/";
                else return null;
            }
        };
        ClasspathUtils cu = new ClasspathUtils(this.getClass().getClassLoader());
        cu.setCallback(callback);
        String result = cu.getArtifactJarUri("pouet", "art");
        assertEquals("file:/usr/local/", result);
        cu.removeCallback();
    }
}
