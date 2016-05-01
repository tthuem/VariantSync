package de.ovgu.variantsync.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestDefaultContext.class, TestSingleContextMapping.class,
		TestUpdateCodeAlgorithm.class, TestXMLOutput.class })
public class TestSuiteVariantSync {
}
