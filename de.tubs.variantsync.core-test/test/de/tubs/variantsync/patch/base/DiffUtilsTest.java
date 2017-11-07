package de.tubs.variantsync.patch.base;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Patch;

public class DiffUtilsTest {

	private List<String> lines1, lines2, lines3, lines4;

	@Before
	public void setUp() throws Exception {
		lines1 = new ArrayList<>();
		lines1.add("package de.test;");
		lines1.add("public int k;");
		lines1.add("public class Test {");
		lines1.add("	int i;");
		lines1.add("}");

		lines2 = new ArrayList<>();
		lines2.add("package de.test;");
		lines2.add("public String str;");
		lines2.add("public int k;");
		lines2.add("public class Team {");
		lines2.add("	int i;");
		lines2.add("}");

		lines3 = new ArrayList<>();
		lines3.add("package de.test;");
		lines3.add("public class Test {");
		lines3.add("public int k;");
		lines3.add("	int i;");
		lines3.add("	String str;");
		lines3.add("}");

		lines4 = new ArrayList<>();
		lines4.add("package de.test;");
		lines4.add("public int k;");
		lines4.add("public class Test {");
		lines4.add("	int i;");
		lines4.add("}");
		lines4.add("	int j;");
	}

	@Test
	public void testPatch1() throws DiffException {
		Patch<String> patch = DiffUtils.diff(lines1, lines3, 1);
		assertTrue(patch.getDeltas().size() == 3);
	}

	@Test
	public void testPatch2() throws DiffException {
		Patch<String> patch = DiffUtils.diff(lines1, lines4, 1);
		assertTrue(patch.getDeltas().size() == 1);
	}

}
