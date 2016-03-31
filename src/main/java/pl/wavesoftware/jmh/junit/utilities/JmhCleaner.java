package pl.wavesoftware.jmh.junit.utilities;

import com.google.common.annotations.VisibleForTesting;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import pl.wavesoftware.eid.utils.EidPreconditions;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;

import static pl.wavesoftware.eid.utils.EidPreconditions.checkState;
import static pl.wavesoftware.eid.utils.EidPreconditions.tryToExecute;

/**
 * This class handles cleaning JMH test annotation that are being produced by JMH library.
 * <p>
 * Use it as a standard JUnit test rule or class rule. Pass a test class in constructor.
 * <p>
 * Example:
 * <pre>
 * &#64;ClassRule
 * public static JmhCleaner cleaner = new JmhCleaner(MyClassTest.class);
 * </pre>
 * @author <a href="mailto:krzysztof.suszynski@coi.gov.pl">Krzysztof Suszynski</a>
 * @since 25.03.16
 */
public final class JmhCleaner extends ExternalResource {
    private static final String GENERATED_TEST_SOURCES = "generated-test-sources";
    private static final String TEST_ANNOTATIONS = "test-annotations";
    private static final File[] EMPTY_FILES = new File[0];
    private final Class<?> testClass;

    /**
     * Default constructor for JMH cleaner class
     * @param testClass a test class
     */
    public JmhCleaner(Class<?> testClass) {
        this.testClass = validateTestClass(testClass);
    }

    @Override
    protected void after() {
        tryToExecute(new EidPreconditions.UnsafeProcedure() {
            @Override
            public void execute() throws IOException, URISyntaxException {
                cleanup();
            }
        }, "20160331:151210");
    }

    @VisibleForTesting
    File getGeneratedTestAnnotationsDir() throws URISyntaxException, IOException {
        String location = testClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        File file = new File(location).getCanonicalFile().getParentFile();
        return resolve(file, GENERATED_TEST_SOURCES, TEST_ANNOTATIONS);
    }

    private static Class<?> validateTestClass(Class<?> testClass) {
        boolean hasTests = false;
        for (Method method : testClass.getDeclaredMethods()) {
            Test annot = method.getAnnotation(Test.class);
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()) && annot != null) {
                hasTests = true;
                break;
            }
        }
        if (!hasTests) {
            throw new IllegalArgumentException("You need to pass a test class to constructor of JmhCleaner!!");
        }
        return testClass;
    }

    private static void deleteRecursive(final File file) throws IOException {
        //to end the recursive loop
        if (!file.exists()) {
            return;
        }

        //if directory, go inside and call recursively
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : ensureFileArray(files)) {
                //call recursively
                deleteRecursive(f);
            }
        }
        //call delete to delete files and empty directory
        boolean deleted = file.delete();
        checkState(deleted, "20160331:151306", "Couldn't remove file: %s", file);
    }

    private static File[] ensureFileArray(@Nullable File[] files) {
        return (files == null) ? EMPTY_FILES : files;
    }

    private void cleanup() throws IOException, URISyntaxException {
        File testAnnotationsPath = getGeneratedTestAnnotationsDir();
        deleteRecursive(testAnnotationsPath);
    }

    private File resolve(File parent, String... paths) {
        StringBuilder sb = new StringBuilder(parent.getPath());
        for (String path : paths) {
            sb.append(File.separator).append(path);
        }
        return new File(sb.toString());
    }
}
