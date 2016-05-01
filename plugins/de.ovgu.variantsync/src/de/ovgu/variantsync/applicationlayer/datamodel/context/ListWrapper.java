package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;

public class ListWrapper {

	@XmlElementWrapper(name = "sync")
	public List<String> list;
	
	public ListWrapper(){
		this.list = new ArrayList<String>();
	}

	public ListWrapper(List<String> list) {
		this.list = list;
	}

	public void add(String s) {
		list.add(s);
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<String> list) {
		this.list = list;
	}
}