package net.thecodemaster.evd.test;

import java.util.List;
import java.util.Map;

import net.thecodemaster.evd.graph.flow.DataFlow;
import net.thecodemaster.evd.helper.Creator;

import org.eclipse.core.resources.IResource;
import org.junit.Assert;
import org.junit.Test;

public class SQLInjection extends AbstractTestVerifier {

	@Override
	protected List<IResource> getResources() {
		Map<String, List<String>> resourceNames = Creator.newMap();

		resourceNames.put(AbstractTestVerifier.PACKAGE_SERVLET, newList("SQLInjection.java"));

		return getRersources(resourceNames);
	}

	@Test
	public void test() {
		Assert.assertEquals(3, allVulnerablePaths.size());

		List<DataFlow> vulnerablePaths01 = allVulnerablePaths.get(0);
		Assert.assertEquals(2, vulnerablePaths01.size());

		List<DataFlow> vulnerablePaths02 = allVulnerablePaths.get(1);
		Assert.assertEquals(2, vulnerablePaths02.size());

		List<DataFlow> vulnerablePaths03 = allVulnerablePaths.get(2);
		Assert.assertEquals(32, vulnerablePaths03.size());
	}

}
