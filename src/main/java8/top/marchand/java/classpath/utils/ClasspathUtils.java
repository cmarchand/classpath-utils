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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * An utility class to find jar in classpath
 * @author cmarchand
 */
public class ClasspathUtils {

    private final List<String> classpathElements;
    private final ClassLoader classloader;
    
    private final ThreadLocal<NotFoundCallback> provider = new ThreadLocal<>();
    
    public static final transient String LOG_PROPERTY = "top.marchand.java.classpath.utils.log";
    
    /**
     * Constructs a new ClasspathUtils based on specified ClassLoader
     * @param cl The classloader to use
     * @throws ClasspathException In case of problem
     */
    public ClasspathUtils(ClassLoader cl)  throws ClasspathException {
        super();
        this.classloader = cl;
        classpathElements = getClasspathElements(cl);
    }
    
    /**
     * Computes the location of the jar identified by <tt>(groupId, artifactId)</tt>.
     * The artifactJar must be in classpath entrie.
     * @param groupId Jar's groupId
     * @param artifactId Jar's artifactId
     * @return The jar URI
     * @throws ClasspathException In case of problem
     */
    public String getArtifactJarUri(final String groupId, final String artifactId) throws ClasspathException {
        log("in getArtifactJarURI("+groupId+","+artifactId+")");
        try {
            String thisJar = null;
            
            String marker = createMarker(groupId, artifactId);
            log("marker is "+marker);
            if(marker!=null) {
                for(String s:classpathElements) {
                    if(s.contains(marker)) {
                        thisJar = s;
                        break;
                    }
                }
            }
            if(thisJar==null) {
                if(provider==null) {
                    log("provider is null");
                }
                NotFoundCallback callback = provider.get();
                if(callback!=null) {
                    String ret = callback.getArtifactJarNotFoundUri(groupId, artifactId);
                    if(ret==null) {
                        throw new ClasspathException("Unable to locate location for ("+groupId+","+artifactId+") from callback");
                    } else {
                        return ret;
                    }
                } else {
                    System.err.println("callback is null");
                    throw new ClasspathException("Unable to locate location for ("+groupId+","+artifactId+") from classpath");
                }
            }
            String jarUri = makeJarUri(thisJar);
            return jarUri;
        } catch(IOException | ClasspathException ex) {
            throw new ClasspathException("while getting jar uri of "+groupId+":"+artifactId, ex);
        }
    }

    /**
     * Return all entries in classloader
     * @param cl The classloader to use
     * @return Entries found, if <tt>cl</tt> is a {@link URLClassLoader}
     * @throws ClasspathException In case of problem
     */
    private static List<String> getClasspathElements(ClassLoader cl) throws ClasspathException {
        if(cl instanceof URLClassLoader) {
            URLClassLoader ucl = (URLClassLoader)cl;
            List<String> ret = new ArrayList(ucl.getURLs().length);
            for(URL u:ucl.getURLs()) {
                ret.add(u.toExternalForm());
            }
            if(ucl.getParent()!=null) {
                ret.addAll(getClasspathElements(ucl.getParent()));
            }
            return ret;
        } else {
            throw new ClasspathException("classloader is not a URL classloader : "+cl.getClass().getName());
        }
    }
    /**
     * Returns classpath entries
     * @return All entries found in the current classloader
     * @throws ClasspathException In case of problem
     */
    private static List<String> getClassPathElements() throws ClasspathException {
        ClassLoader cl = ClasspathUtils.class.getClassLoader();
        return getClasspathElements(cl);
    }

    /**
     * Creates a marker of a jar file. This marker is used to identify the jar
     * associated to a <tt>(groupId, artifactId)</tt>
     * @param groupId
     * @param artifactId
     * @return
     * @throws IOException 
     */
    private String createMarker(String groupId, String artifactId) throws IOException {
        Properties props = new Properties();
        try {
            props.load(classloader.getResourceAsStream("META-INF/maven/"+groupId+"/"+artifactId+"/pom.properties"));
            return String.format("%s-%s", props.getProperty("artifactId",""), props.getProperty("version",""));
        } catch(NullPointerException ex) {
            return null;
        }
    }
    /**
     * Transform a jar file to a URI, as to must be declared in catalog entries
     * @param jarFile
     * @return
     * @throws MalformedURLException 
     */
    private String makeJarUri(String jarFile) throws MalformedURLException {
        return "jar:" + jarFile +"!/";
    }
    
    public void setCallback(NotFoundCallback callback) {
        provider.set(callback);
    }
    
    public void removeCallback() {
        provider.remove();
    }
    
    private void log(String msg) {
        if("true".equals(System.getProperty(LOG_PROPERTY))) {
            System.out.println(msg);
        }
    }
}
