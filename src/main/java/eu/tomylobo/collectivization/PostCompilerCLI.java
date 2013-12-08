package eu.tomylobo.collectivization;

import java.io.File;

import org.apache.maven.plugin.logging.SystemStreamLog;

/**
 * CLI (Command Line Interface) to apply Collectivizer on every files (*.class)
 * into a directory (and sub-directory).
 *
 * @author dwayneb
 * @see Collectivizer
 */
public class PostCompilerCLI {
    
    /**
     * CLI entry point.
     *
     * @param args[0] a directory path with *.class or the path of one file to transfom.
     */
    public static void main(String[] args) throws Exception {
        SystemStreamLog logger = new SystemStreamLog();
        try {
            File target = new File(args[0]);
            if (!target.exists()) {
                logger.warn("target dir doesn't exist :" + target);
                return;
            }
            PostCompilerMojo pc = new PostCompilerMojo();
            pc.setDirectory(target);
            pc.setLog(logger);
            pc.execute();
        } catch(Exception exc) {
            logger.error(exc);
            throw exc;
        }
    }
    
    
}
