/*
 * Copyright 2016-2019 Wave Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.wavesoftware.jmh.junit.utilities;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * @author <a href="mailto:krzysztof.suszynski@coi.gov.pl">Krzysztof Suszynski</a>
 * @since 31.03.16
 */
@ExtendWith(MockitoExtension.class)
class JmhCleanerTest {

    @RegisterExtension
    static TestDirectory testDirectory = new TestDirectory();
    @Mock
    private ExtensionContext extensionContext;

    @Test
    void testConstruct_InvalidClass() {
        // when
        ThrowingCallable throwingCallable = () -> new JmhCleaner(String.class);

        // then
        assertThatCode(throwingCallable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("You need to pass a test class to constructor of JmhCleaner!!");
    }

    @Test
    void testConstruct() {
        // when
        JmhCleaner cleaner = new JmhCleaner(JmhCleanerTest.class);
        // then
        assertThat(cleaner).isNotNull();
    }

    @Test
    void testAfter() throws IOException, URISyntaxException {
        // when
        JmhCleaner cleaner = new JmhCleaner(JmhCleanerTest.class);
        assertThat(testDirectory.getTestFile()).exists()
            .canRead();

        // when
        cleaner.afterEach(extensionContext);
        cleaner.afterEach(extensionContext);

        // then
        assertThat(cleaner).isNotNull();
        assertThat(testDirectory.getTestFile()).doesNotExist();
    }

    private static class TestDirectory implements BeforeEachCallback, AfterEachCallback {

        private static final String TEST_FILE = "test.txt";
        private static final String TEST_CONTENT = "QAZWSX";
        File dir;
        JmhCleaner instance;
        private File testFile;

        @Override
        public void beforeEach(ExtensionContext context) throws IOException, URISyntaxException {
            instance = new JmhCleaner(JmhCleanerTest.class);
            dir = instance.getGeneratedTestAnnotationsDir();
            FileUtils.deleteDirectory(dir);
            assertThat(dir.exists()).isFalse();
            assertThat(dir.mkdirs()).isTrue();
            assertThat(dir.exists()).isTrue();
            testFile = new File(dir.toString() + File.separator + TEST_FILE);
            FileUtils.write(testFile, TEST_CONTENT, StandardCharsets.UTF_8);
            assertThat(testFile).canRead();
        }

        @Override
        public void afterEach(ExtensionContext context) throws IOException {
            FileUtils.deleteDirectory(dir);
            assertThat(dir.exists()).isFalse();
            assertThat(testFile).doesNotExist();
        }

        File getTestFile() {
            return testFile;
        }
    }

}
