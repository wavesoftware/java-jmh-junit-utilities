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

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * A rule that can be used to automatically skip test if some javaagent is being used
 * for ex.: debugger, profiler, code coverage or other. It can be useful if you are
 * running performance critical tests like JMH.
 * <p>
 * Example:
 * <pre>
 * &#64;RegisterExtension
 * static JavaAgentSkip javaAgentSkip = JavaAgentSkip.ifPresent();
 * </pre>
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 29.03.16
 */
public final class JavaAgentSkip implements BeforeEachCallback {

    private static final String JAVAAGENT = "-javaagent:";
    static final String DEFAULT_MESSAGE_FORMAT =
        "Skipping test due to JavaAgentSkip set to %s";

    private static final RuntimeMXBean DEFAULT_RUNTIME_MXBEAN =
        ManagementFactory.getRuntimeMXBean();
    private final boolean isActive;
    private final String messageFormat;
    private final RuntimeMXBean runtimeMxBean;

    private JavaAgentSkip(boolean isActive) {
        this(isActive, DEFAULT_MESSAGE_FORMAT, DEFAULT_RUNTIME_MXBEAN);
    }

    protected JavaAgentSkip(boolean isActive, String messageFormat, RuntimeMXBean runtimeMxBean) {
        this.isActive = isActive;
        this.messageFormat = messageFormat;
        this.runtimeMxBean = runtimeMxBean;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        assumeTrue(assumeValue(), () -> String.format(messageFormat, isActive));
    }

    /**
     * Creates a JavaAgentSkip rule that is set to skip if agent is present
     * @return a JavaAgentSkip rule
     */
    public static JavaAgentSkip ifPresent() {
        return new JavaAgentSkip(true);
    }

    /**
     * Creates a JavaAgentSkip rule that is set to skip if agent is present. User can pass an extra message
     * format to be used as a skip message.
     *
     * @param messageFormat an extra message format to be used as a skip message
     * @return a JavaAgentSkip rule
     */
    public static JavaAgentSkip ifPresent(String messageFormat) {
        return new JavaAgentSkip(true, messageFormat, DEFAULT_RUNTIME_MXBEAN);
    }

    /**
     * Creates a JavaAgentSkip rule that is set to skip if agent is absent
     * @return a JavaAgentSkip rule
     */
    public static JavaAgentSkip ifAbsent() {
        return new JavaAgentSkip(false);
    }

    /**
     * Creates a JavaAgentSkip rule that is set to skip if agent is absent. User can pass an extra message
     * format to be used as a skip message.
     *
     * @param messageFormat an extra message format to be used as a skip message
     * @return a JavaAgentSkip rule
     */
    public static JavaAgentSkip ifAbsent(String messageFormat) {
        return new JavaAgentSkip(false, messageFormat, DEFAULT_RUNTIME_MXBEAN);
    }

    private boolean assumeValue() {
        return isAgentThere() != isActive;
    }

    private boolean isAgentThere() {
        List<String> arguments = runtimeMxBean.getInputArguments();
        boolean agent = false;
        for (String argument : arguments) {
            if (argument.startsWith(JAVAAGENT)) {
                agent = true;
                break;
            }
        }
        return agent;
    }
}
