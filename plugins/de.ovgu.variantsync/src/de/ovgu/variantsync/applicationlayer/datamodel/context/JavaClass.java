package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import de.ovgu.variantsync.applicationlayer.context.ContextProvider;
import de.ovgu.variantsync.applicationlayer.features.mapping.UtilOperations;
import de.ovgu.variantsync.presentationlayer.controller.data.JavaElements;

public class JavaClass extends JavaElement {

	private List<CodeLine> codeLines;
	private List<CodeLine> wholeClass;
	private List<CodeChange> changes;
	private CodeChange actualChange;

	public JavaClass() {
		super();
		init();
	}

	private void init() {
		if (codeLines == null)
			codeLines = new ArrayList<CodeLine>();
		if (changes == null)
			changes = new LinkedList<CodeChange>();
		if (wholeClass == null)
			wholeClass = new ArrayList<CodeLine>();
	}

	// TODO: für syntaktischen Merge müssen korrekte JavaKlassen gebildet werden
	// aus den Codezeilen, die per Diff ermittelt wurden als Code, der zum
	// Context/Feature gehört
	// TODO: use JavaParser to create AST (see CuPrinter example)

	// 1) Änderung an Klasse wird gespeichert im Editor
	// 2) VSync bemerkt Änderung, berechnet Diff, pflegt Code-Basis im Plugin
	// (aktueller Stand der Klasse im Context)
	// 3) VSync legt in Klasse einen CodeChange-Eintrag, bestehend aus
	// Timestamp, baseVersion und newVersion,in Queue an
	// baseVersion sind die Codezeilen, die vor der Änderung dem Feature aus
	// dieser Klasse zugeordnet waren
	// new Version sind die Codezeilen, die nach der Änderung dem Feature aus
	// dieser Klasse zugeordnet waren
	// base und newVersion müssen syntaktisch korrete Klassen darstellen für
	// syntaktischen Merge mit JDime
	// => Problem: bisher nur Codezeilen ohne Semantic gespeichert
	// => Lösung: AST erstellen mit javaparser. Dieser enthält den Code mit
	// Zeilennummern zugeordnet zu Methoden
	// AST ist Schablone, die auf den gespeicherten Code der Versions gelegt
	// wird. Damit wird eine abgespeckte, syntaktisch korrekte Datei erstellt
	// (wenn der Entwickler den Code syntaktisch korrekt mappt mit dem Context)
	// wenn Code syntaktisch nicht korrekt ist, dann wird kein structured merge
	// (synktatischer merge) sondern unstructured merge (line-based)
	// durchgeführt.
	/*
	 * 
	 * ====================================== === Synchronisieren von Änderungen
	 * === ======================================
	 * 
	 * Beim Speichern Vorgang: - Sichern der bisherigen Code-Lines der Klasse,
	 * die sich im Context befinden - Einfügen der Änderungen und Sichern der
	 * aktuellen Code-Lines der Klasse, die sich im Context befinden -
	 * Sicherungen erfolgen im Typ CodeChange, der für jede Klasse im Context
	 * als Queue gespeichert wird - CodeChange bekommt Timestamp, damit beim
	 * Synchronisieren die Reihenfolge der Änderungen an gleichen Klassen
	 * desselben Features rekonstruiert werden kann
	 * 
	 * Beim Synchronisieren: - Änderungen an Features (Context) Klassenweise
	 * aufführen - Änderungen an gleichen Klassen in unterschiedlichen Projekten
	 * des gleiches Features nach zeitlicher Reihenfolge synchronisieren mit
	 * jdime - Automatisierung der Änderungen durchführen, außer es tritt ein
	 * Konflikt auf: Git-Vergleichsdialog für User öffnen
	 */

	public void setBaseVersion() {
		actualChange = new CodeChange();
		List<CodeLine> baseVersion = new ArrayList<CodeLine>();
		for (CodeLine cl : this.codeLines) {
			baseVersion.add(cl.clone());
		}
		List<CodeLine> baseVersionWholeClass = new ArrayList<CodeLine>();
		this.wholeClass = new ContextProvider().getLinesOfActualFile(getName());
		if (this.wholeClass != null) {
			for (CodeLine cl : this.wholeClass) {
				CodeLine baseLine = cl.clone();
				baseLine.setMapped(false);
				baseVersionWholeClass.add(baseLine);
			}
		}
		actualChange.setBaseVersion(baseVersion);
		actualChange.setBaseVersionWholeClass(baseVersionWholeClass);
	}

	public void addChange(List<CodeLine> newLines, List<String> list) {
		List<CodeLine> newVersion = new ArrayList<CodeLine>();
		for (CodeLine cl : newLines) {
			newVersion.add(cl.clone());
		}
		List<CodeLine> newVersionWholeClass = new ArrayList<CodeLine>();
		if (this.wholeClass == null || this.wholeClass.isEmpty()) {
			this.wholeClass = new ContextProvider()
					.getLinesOfActualFile(getName());
		}
		int i = 1;
		for (String s : list) {
			CodeLine newLine = new CodeLine(s, i, false, false);
			newVersionWholeClass.add(newLine);
			i++;
		}
		if (actualChange == null) {
			actualChange = new CodeChange();
			List<CodeLine> baseVersionWholeClass = new ArrayList<CodeLine>();
			if (this.wholeClass != null) {
				for (CodeLine cl : this.wholeClass) {
					CodeLine baseLine = cl.clone();
					baseLine.setMapped(false);
					baseVersionWholeClass.add(baseLine);
				}
			}
			actualChange.setBaseVersionWholeClass(baseVersionWholeClass);
		}
		actualChange.setNewVersion(newVersion);
		actualChange.setNewVersionWholeClass(newVersionWholeClass);
		actualChange.createTimeStamp();
		changes.add(actualChange);
		System.out.println(changes.toString());
	}

	public void addChange(List<CodeLine> newLines) {
		List<CodeLine> newVersion = new ArrayList<CodeLine>();
		for (CodeLine cl : newLines) {
			newVersion.add(cl.clone());
		}
		List<CodeLine> newVersionWholeClass = new ArrayList<CodeLine>();
		if (this.wholeClass != null) {
			for (CodeLine cl : this.wholeClass) {
				CodeLine newLine = cl.clone();
				newLine.setMapped(false);
				newVersionWholeClass.add(newLine);
			}
		}
		if (actualChange == null) {
			actualChange = new CodeChange();
		}
		actualChange.setNewVersion(newVersion);
		actualChange.setNewVersionWholeClass(newVersionWholeClass);
		actualChange.createTimeStamp();
		if (!changes.isEmpty()
				&& timestampToString(actualChange.getTimestamp()).equals(
						timestampToString(changes.get(changes.size() - 1)
								.getTimestamp()))) {
			List<CodeLine> base = changes.get(changes.size() - 1)
					.getBaseVersion();
			changes.set(changes.size() - 1, actualChange);
			changes.get(changes.size() - 1).setBaseVersion(base);
		} else
			changes.add(actualChange);
		System.out.println(changes.toString());
	}

	private String timestampToString(long timestamp) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"hh:mm:ss 'at' dd.MM.yyyy");
		return formatter.format(new Date(timestamp));
	}

	public JavaClass(String name, String path, JavaElement member) {
		super(name, path, JavaElements.CLASS);
		addChild(member);
		init();
	}

	public JavaClass(String name, String path, List<JavaElement> members) {
		super(name, path, JavaElements.CLASS);
		setChildren(members);
		init();
	}

	public JavaClass(String name, String path, List<String> code,
			int numberOfCodeLines) {
		super(name, path, JavaElements.CLASS);
		addCode(new CodeFragment(code, 0, numberOfCodeLines, 0));
		init();
	}

	public JavaClass(String name, String path, CodeFragment code) {
		super(name, path, JavaElements.CLASS);
		addCode(code);
		init();
	}

	public JavaClass(String name, String path) {
		super(name, path, JavaElements.CLASS);
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("JavaClass [name=" + getName() + ", codeLines=");
		List<CodeLine> listCl = codeLines;
		for (CodeLine cl : listCl) {
			sb.append("\n\t");
			sb.append(cl.toString());
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	protected JavaElement getConcreteType(String name, String path) {
		return new JavaClass(name, path);
	}

	public List<CodeLine> getClonedCodeLines() {
		List<CodeLine> tmpList = new ArrayList<CodeLine>();
		for (CodeLine cl : codeLines) {
			tmpList.add(cl.clone());
		}
		return tmpList;
	}

	public List<CodeLine> getClonedCodeLinesWholeClass() {
		List<CodeLine> tmpList = new ArrayList<CodeLine>();
		for (CodeLine cl : wholeClass) {
			tmpList.add(cl.clone());
		}
		return tmpList;
	}

	public void addCode(CodeFragment code) {
		codeLines = UtilOperations.getInstance().addCode(code, codeLines);
	}

	public void addCode(List<CodeLine> code) {
		codeLines.addAll(code);
	}

	public void removeCode(int start, int end) {
		codeLines = UtilOperations.getInstance().removeCode(start, end,
				codeLines);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public JavaElement clone() throws CloneNotSupportedException {
		List<JavaElement> children = getChildren();
		List<JavaElement> clonedChildren = new ArrayList<JavaElement>();
		if (children != null && !children.isEmpty() && children.get(0) != null) {
			for (JavaElement element : children) {
				clonedChildren.add(element.clone());
			}
		}
		JavaElement copy = getConcreteType(getName(), getPath());
		if (clonedChildren != null) {
			copy.setChildren(clonedChildren);
		}
		List<CodeLine> clonedCodeFragments = new CopyOnWriteArrayList<CodeLine>();
		List<CodeLine> code = getClonedCodeLines();
		if (code != null) {
			for (CodeLine fragment : code) {
				clonedCodeFragments.add(fragment.clone());
			}
			((JavaClass) copy).setCodeLines(clonedCodeFragments);
		}
		List<CodeChange> changes = this.changes;
		List<CodeChange> clonedChanges = new LinkedList<CodeChange>();
		if (changes != null) {
			for (CodeChange change : changes) {
				clonedChanges.add(change.clone());
			}
			((JavaClass) copy).setChanges(clonedChanges);
		}
		return copy;
	}

	@XmlElementWrapper(name = "code")
	@XmlElement(name = "codeline")
	public List<CodeLine> getCodeLines() {
		return codeLines;
	}

	/**
	 * @param codeLines
	 *            the codeFragments to set
	 */
	public boolean setCodeLines(List<CodeLine> codeLines) {
		this.codeLines.clear();
		for (CodeLine cl : codeLines) {
			this.codeLines.add(cl.clone());
		}
		return true;
	}

	public void setLinesOfWholeClass(List<String> lines) {
		wholeClass = new ArrayList<CodeLine>();
		int i = 1;
		for (String line : lines) {
			wholeClass.add(new CodeLine(line, i));
			i++;
		}
	}

	/**
	 * @return the wholeClass
	 */
	@XmlElementWrapper(name = "wholeClass")
	@XmlElement(name = "codeline")
	public List<CodeLine> getWholeClass() {
		return wholeClass;
	}

	public void setWholeClass(List<CodeLine> wholeClass) {
		this.wholeClass = wholeClass;
	}

	@XmlElementWrapper(name = "changes")
	@XmlElement(name = "change")
	public List<CodeChange> getChanges() {
		return changes;
	}

	/**
	 * @param changes
	 *            the changes to set
	 */
	public void setChanges(List<CodeChange> changes) {
		this.changes = changes;
	}
	
	public List<CodeChange> getClonedChanges() {
		List<CodeChange> clonedChanges = new ArrayList<CodeChange>();
		for (CodeChange ch : changes) {
			clonedChanges.add(ch.clone());
		}
		return clonedChanges;
	}


	public Object removeChange(int selectedChange) {
		changes.remove(selectedChange);
		return null;
	}

	public void removeContent() {
		codeLines.clear();
		wholeClass.clear();
		changes.clear();
		actualChange = new CodeChange();
	}
}
