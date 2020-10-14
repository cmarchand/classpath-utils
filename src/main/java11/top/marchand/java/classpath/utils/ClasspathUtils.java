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

import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * An utility class to find jar in classpath
 * @author cmarchand
 */
public class ClasspathUtils {

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
        URI emptyURI=null;
        try {
            emptyURI = new URI("empty://");
        } catch(Throwable t) {}
//        for(ModuleReference mr: ModuleFinder.ofSystem().findAll()) {
//            log("looking in "+mr.location().orElse(emptyURI).toString());
//        }
        
//        ModuleLayer layer = ModuleLayer.boot();
//        for(Module m: layer.modules()) {
//            log("Found module "+m.getName());
//        }
        
        String resource = String.format("META-INF/maven/%s/%s/pom.properties",groupId, artifactId);
//        URL resourceUrl = classloader.getResource(resource);
//        if(resourceUrl==null) {
//            throw new ClasspathException(String.format("Unable to find [%s:%s] jar", groupId, artifactId));
//        } else {
//            log(resourceUrl.toExternalForm());
//            return null;
//        }
        return null;
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
