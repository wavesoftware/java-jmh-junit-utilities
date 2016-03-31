package pl.wavesoftware.jmh.junit.utilities;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.ExternalResource;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:krzysztof.suszynski@coi.gov.pl">Krzysztof Suszynski</a>
 * @since 31.03.16
 */
public class JmhCleanerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TestDirectoryExternalResource testDirectory = new TestDirectoryExternalResource();

    @Test
    public void testConstruct_InvalidClass() {
        // then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("You need to pass a test class to constructor of JmhCleaner!!");
        // when
        new JmhCleaner(String.class);
    }

    @Test
    public void testConstruct() {
        // when
        JmhCleaner cleaner = new JmhCleaner(JmhCleanerTest.class);
        // then
        assertThat(cleaner).isNotNull();
    }

    @Test
    public void testAfter() {
        // when
        JmhCleaner cleaner = new JmhCleaner(JmhCleanerTest.class);
        assertThat(testDirectory.getTestFile()).exists()
            .canRead();

        // when
        cleaner.after();
        cleaner.after();

        // then
        assertThat(cleaner).isNotNull();
        assertThat(testDirectory.getTestFile()).doesNotExist();
    }

    private static class TestDirectoryExternalResource extends ExternalResource {

        private static final String TEST_FILE = "test.txt";
        private static final String TEST_CONTENT = "QAZWSX";
        public File dir;
        public JmhCleaner instance;
        private File testFile;

        @Override
        protected void before() throws Throwable {
            instance = new JmhCleaner(JmhCleanerTest.class);
            dir = instance.getGeneratedTestAnnotationsDir();
            FileUtils.deleteDirectory(dir);
            assertThat(dir.exists()).isFalse();
            assertThat(dir.mkdirs()).isTrue();
            assertThat(dir.exists()).isTrue();
            testFile = new File(dir.toString() + File.separator + TEST_FILE);
            FileUtils.write(testFile, TEST_CONTENT);
            assertThat(testFile).canRead();
        }

        @Override
        protected void after() {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            assertThat(dir.exists()).isFalse();
            assertThat(testFile).doesNotExist();
        }

        public File getTestFile() {
            return testFile;
        }
    }

}
