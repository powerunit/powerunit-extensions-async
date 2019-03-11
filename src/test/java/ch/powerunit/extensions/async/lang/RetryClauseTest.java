package ch.powerunit.extensions.async.lang;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class RetryClauseTest implements TestSuite {
	@Test
	public void testRetryClauseOperation() {
		RetryClause c1 = RetryClause.of(0, 0);
		assertThat(c1).isNotNull();
		assertThat(c1.getCount()).is(0);
		assertThat(c1.getWaitInMs()).is(0L);
		c1 = c1.withCount(2);
		assertThat(c1).isNotNull();
		assertThat(c1.getCount()).is(2);
		assertThat(c1.getWaitInMs()).is(0L);
		c1 = c1.withMs(1000);
		assertThat(c1).isNotNull();
		assertThat(c1.getCount()).is(2);
		assertThat(c1.getWaitInMs()).is(1000L);
	}
}
