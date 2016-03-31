package pl.wavesoftware.jmh.junit.utilities;

import com.google.common.annotations.VisibleForTesting;
import org.junit.Assume;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

/**
 * A rule that can be used to automatically skip test if some javaagent is being used for ex.: debugger,
 * profiler, code coverage or other. It can be useful if you are running performance critical tests like JMH.
 * <p>
 * Example:
 * <pre>
 * &#64;ClassRule
 * public static JavaAgentSkip javaAgentSkip = JavaAgentSkip.ifPresent();
 * </pre>
 * @author <a href="mailto:krzysztof.suszynski@coi.gov.pl">Krzysztof Suszynski</a>
 * @since 29.03.16
 */
public final class JavaAgentSkip implements TestRule {

    public static final String JAVAAGENT = "-javaagent:";
    public static final String DEFAULT_MESSAGE_FORMAT = "Skipping test due to JavaAgentSkip rule set to %s";
    private static final RuntimeMXBean DEFAULT_RUNTIME_MXBEAN = ManagementFactory.getRuntimeMXBean();
    private final boolean isActive;
    private final String messageFormat;
    private final RuntimeMXBean runtimeMxBean;

    private JavaAgentSkip(boolean isActive) {
        this(isActive, DEFAULT_MESSAGE_FORMAT, DEFAULT_RUNTIME_MXBEAN);
    }

    @VisibleForTesting
    protected JavaAgentSkip(boolean isActive, String messageFormat, RuntimeMXBean runtimeMxBean) {
        this.isActive = isActive;
        this.messageFormat = messageFormat;
        this.runtimeMxBean = runtimeMxBean;
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

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                String message = String.format(messageFormat, isActive);
                Assume.assumeTrue(message, assumeValue());
                base.evaluate();
            }
        };
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
