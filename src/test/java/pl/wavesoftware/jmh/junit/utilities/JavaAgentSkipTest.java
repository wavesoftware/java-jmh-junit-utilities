package pl.wavesoftware.jmh.junit.utilities;

import org.junit.AssumptionViolatedException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author <a href="mailto:krzysztof.suszynski@coi.gov.pl">Krzysztof Suszynski</a>
 * @since 31.03.16
 */
public class JavaAgentSkipTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testIfPresent() {
        // when
        JavaAgentSkip skip = JavaAgentSkip.ifPresent();
        // then
        assertThat(skip).isNotNull();
    }

    @Test
    public void testIfPresent1() {
        // when
        JavaAgentSkip skip = JavaAgentSkip.ifPresent("A message format");
        // then
        assertThat(skip).isNotNull();
    }

    @Test
    public void testIfNotPresent() {
        // when
        JavaAgentSkip skip = JavaAgentSkip.ifNotPresent();
        // then
        assertThat(skip).isNotNull();
    }

    @Test
    public void testIfNotPresent1() {
        // when
        JavaAgentSkip skip = JavaAgentSkip.ifNotPresent("A other message format");
        // then
        assertThat(skip).isNotNull();
    }

    @Test
    public void testApply() throws Throwable {
        // given
        RuntimeMXBean mxbean = mock(RuntimeMXBean.class);
        List<String> args = Arrays.asList("-ea", "-server", "-Xmx128m", "-javaagent:jacoco", "-Xms32m");
        when(mxbean.getInputArguments()).thenReturn(args);
        Statement statement = mock(Statement.class);
        Description description = mock(Description.class);
        JavaAgentSkip skip = new JavaAgentSkip(true, JavaAgentSkip.DEFAULT_MESSAGE_FORMAT, mxbean);

        // then
        thrown.expect(AssumptionViolatedException.class);
        thrown.expectMessage("Skipping test due to JavaAgentSkip rule set to true");

        // when
        Statement newStatement = skip.apply(statement, description);
        assertThat(newStatement).isNotNull();
        newStatement.evaluate();
    }

    @Test
    public void testApply_Possitive() throws Throwable {
        // given
        List<String> args = Arrays.asList("-ea", "-server", "-Xmx128m", "-Xms32m");
        RuntimeMXBean mxbean = mock(RuntimeMXBean.class);
        when(mxbean.getInputArguments()).thenReturn(args);
        Statement statement = mock(Statement.class);
        Description description = mock(Description.class);
        JavaAgentSkip skip = new JavaAgentSkip(true, JavaAgentSkip.DEFAULT_MESSAGE_FORMAT, mxbean);

        // when
        Statement newStatement = skip.apply(statement, description);
        assertThat(newStatement).isNotNull();
        newStatement.evaluate();

        // then
        verify(statement).evaluate();
    }
}
