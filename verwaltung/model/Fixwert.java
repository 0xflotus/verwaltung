package verwaltung.model;

/**
 * Modelklasse f�r monatlich wiederkehrende Kosten
 * 
 * @author 0xflotus
 *
 */
public class Fixwert {
	private double wert;
	private String marke;
	private String text;
	private boolean einnahme;
	private long rowid;

	public Fixwert(double wert, String marke, String text, boolean einnahme, long rowid) {
		this.rowid = rowid;
		this.wert = wert;
		this.marke = marke;
		this.text = text;
		this.einnahme = einnahme;
	}

	public long getRowid() {
		return rowid;
	}

	public void setRowid(long rowid) {
		this.rowid = rowid;
	}

	public double getWert() {
		return wert;
	}

	public void setWert(double wert) {
		this.wert = wert;
	}

	public String getMarke() {
		return marke;
	}

	public void setMarke(String marke) {
		this.marke = marke;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isEinnahme() {
		return einnahme;
	}

	public void setEinnahme(boolean einnahme) {
		this.einnahme = einnahme;
	}

	@Override
	public String toString() {
		return this.wert + " -> " + this.marke + ": " + this.text;
	}
}
