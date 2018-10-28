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

import java.net.URL;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cmarchand
 */
public class ClasspathUtilsTest {
    
    @Test
    public void testClasspathNotEmpty() throws ClasspathException {
        List<String> ret = ClasspathUtils.getClassPathElements();
        assertFalse("not classpath elements found", ret.isEmpty());
        for(String s:ret) {
            System.out.println("Found: "+s);
        }
        ret = ClasspathUtils.getClasspathElements(Assert.class.getClassLoader());
        assertFalse("not classpath elements found", ret.isEmpty());
        ClassLoader assertCL = Assert.class.getClassLoader();
        System.out.println("Assert classloader is "+assertCL+" "+assertCL.getClass().getName());
        for(String s:ret) {
            System.out.println("Found: "+s);
        }
        URL url = Assert.class.getClassLoader().getResource("org/junit/Assert.class");
        System.out.println("Assert.class is at "+url.toExternalForm());
        assertSame("AssertClassLoader is not same a current classloader", assertCL, ClasspathUtils.class.getClassLoader());
    }
    @Test
    public void testJUnitIsIn() throws ClasspathException {
        ClasspathUtils cu = new ClasspathUtils(this.getClass().getClassLoader());
        String junitUri = cu.getArtifactJarUri("junit", "junit");
        assertNotNull("JUnit URI is null", junitUri);
    }
}
