package no.rutebanken.baba.organisation.repository;

import org.junit.Assert;
import org.junit.Test;

public class CodeSpaceRepositoryTest extends BaseIntegrationTest {


	@Test
	public void testFindByPublicId() {
		Assert.assertNotNull(codeSpaceRepository.getOneByPublicId(defaultCodeSpace.getId()));
	}

}
