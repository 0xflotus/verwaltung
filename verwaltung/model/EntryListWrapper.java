package verwaltung.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Eine Wrapperklasse, die f�r die Speicherung der Daten in XML ben�tigt wird.
 * 
 * @author 0xflotus
 *
 */
@XmlRootElement(name = "entries")
public class EntryListWrapper {
	private ArrayList<Entry> entries;

	@XmlElement(name = "entry")
	public ArrayList<Entry> getEntries() {
		return entries;
	}

	public void setEntries(ArrayList<Entry> entries) {
		this.entries = entries;
	}
}
