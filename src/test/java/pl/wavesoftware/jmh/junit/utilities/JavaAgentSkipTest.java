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

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.TestAbortedException;

import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:krzysztof.suszynski@coi.gov.pl">Krzysztof Suszynski</a>
 * @since 31.03.16
 */
class JavaAgentSkipTest {

    @Test
    void testIfPresent() {
        // when
        JavaAgentSkip skip = JavaAgentSkip.ifPresent();
        // then
        assertThat(skip).isNotNull();
    }

    @Test
    void testIfPresent_String() {
        // when
        JavaAgentSkip skip = JavaAgentSkip.ifPresent("A message format");
        // then
        assertThat(skip).isNotNull();
    }

    @Test
    void testIfAbsent() {
        // when
        JavaAgentSkip skip = JavaAgentSkip.ifAbsent();
        // then
        assertThat(skip).isNotNull();
    }

    @Test
    void testIfAbsent_String() {
        // when
        JavaAgentSkip skip = JavaAgentSkip.ifAbsent("A other message format");
        // then
        assertThat(skip).isNotNull();
    }

    @Test
    void testApply() {
        // given
        RuntimeMXBean mxbean = mock(RuntimeMXBean.class);
        List<String> args = Arrays.asList(
            "-ea", "-server", "-Xmx128m", "-javaagent:jacoco", "-Xms32m"
        );
        when(mxbean.getInputArguments()).thenReturn(args);
        JavaAgentSkip skip = new JavaAgentSkip(
            true, JavaAgentSkip.DEFAULT_MESSAGE_FORMAT, mxbean
        );
        ExtensionContext ctx = mock(ExtensionContext.class);

        // when
        ThrowingCallable throwingCallable = () -> skip.beforeEach(ctx);

        // then
        assertThatCode(throwingCallable)
            .isInstanceOf(TestAbortedException.class)
            .hasMessage("Assumption failed: Skipping test due to JavaAgentSkip set to true");
    }

    @Test
    void testApply_Possitive() {
        // given
        List<String> args = Arrays.asList("-ea", "-server", "-Xmx128m", "-Xms32m");
        RuntimeMXBean mxbean = mock(RuntimeMXBean.class);
        when(mxbean.getInputArguments()).thenReturn(args);
        JavaAgentSkip skip = new JavaAgentSkip(
            true, JavaAgentSkip.DEFAULT_MESSAGE_FORMAT, mxbean
        );
        ExtensionContext ctx = mock(ExtensionContext.class);

        // when
        skip.beforeEach(ctx);

        // then
        verify(mxbean).getInputArguments();
    }
}
