package verwaltung.model;

import java.util.Date;

/**
 * Modelklasse f�r die Darstellung eines Eintrags
 * 
 * @author 0xflotus
 *
 */
public class Entry {
	private long key;
	private long datum;
	private double betrag;
	private String marke;
	private String text;

	public Entry() {
		this(0.0, "Marke", "Text");
	}

	public Entry(long key, long date, double betrag, String marke, String text) {
		this.key = key;
		this.datum = date;
		this.betrag = betrag;
		this.marke = marke;
		this.text = text;
	}

	public Entry(double betrag, String marke, String text) {
		this.datum = new Date().getTime();
		this.betrag = betrag;
		this.marke = marke;
		this.text = text;
	}

	public Entry(long date, double betrag, String marke, String text) {
		this.datum = date;
		this.betrag = betrag;
		this.marke = marke;
		this.text = text;
	}

	public long getDate() {
		return datum;
	}

	public void setDate(long date) {
		this.datum = date;
	}

	public double getBetrag() {
		return betrag;
	}

	public void setBetrag(double betrag) {
		this.betrag = betrag;
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
}
