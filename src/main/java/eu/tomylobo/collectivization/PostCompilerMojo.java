/*
 * PostCompilerMojo.java
 *
 * Created on September 15, 2006, 10:23 AM
 */
package eu.tomylobo.collectivization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

/**
 * Add getter and setter for every public fields, on every files (*.class)
 * into a directory (and sub-directories).
 *
 * @goal post-compile
 * @phase process-classes
 * @requiresDependencyResolution runtime
 *
 * @author dwayne
 * @version September 15, 2006
 */
public class PostCompilerMojo extends AbstractMojo {

    private int nbFailure_;

    /**
     * The root directory with classes to transform.
     *
     * @parameter property="project.build.outputDirectory"
     * @required
     * @readonly
     */
    private File directory;

    public void setDirectory(File v) throws MojoExecutionException {
        directory = v;
    }

    public void execute() throws MojoExecutionException {
        nbFailure_ = 0;
        try {
            if (directory.isFile()) {
                transform(directory);
            } else {
                Iterator it = FileUtils.iterateFiles(directory, new String[]{"class"}, true);
                while(it.hasNext()){
                    File f = (File)it.next();
                    transform(f);
                }
            }
            if (nbFailure_ > 0) {
                throw new MojoFailureException(nbFailure_ + " failure(s) during post-compile of " + directory);
            }
        } catch(Exception exc) {
            throw new MojoExecutionException("unexpected exception when post-compile", exc);
        }
    }

    private void transform(File classFile) {
        try {
            getLog().debug("post-compile :" + classFile);
            final byte[] originalClass;
            try (final FileInputStream input = new FileInputStream(classFile)) {
                originalClass = IOUtils.toByteArray(input);
            }
            try (final FileOutputStream output = new FileOutputStream(classFile)) {
                IOUtils.write(transform(originalClass), output);
            }
        } catch (Exception exc) {
            nbFailure_++;
            getLog().warn("failed to post-compile :" + classFile, exc);
        }
    }

    private byte[] transform(byte[] in) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cc = new CheckClassAdapter(cw);
        Collectivizer cv = new Collectivizer(cc, getLog());
        ClassReader cr = new ClassReader(in);
        cr.accept(cv, 0);
        return cw.toByteArray();
    }
}

