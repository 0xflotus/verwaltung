package verwaltung.util.json;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import verwaltung.config.VerwaltungConfig;
import verwaltung.model.Entry;
import verwaltung.model.EntryListWrapper;
import verwaltung.view.Verwaltung;

public class JSONHandler {
	private ObjectMapper mapper;
	private static String sqlStorage = "";
	private Connection connection;
	private Verwaltung verw;

	/**
	 * Erstellt ein Objekt der Klasse JSONHandler, das ben�tigt wird, um die
	 * Daten aus der Datenbank im JSON-Format zu speichern und andersherum.
	 * 
	 * @param verw
	 *            Das Verwaltungsobjekt
	 */
	public JSONHandler(Verwaltung verw) {
		sqlStorage = verw.getSqlStorage();
		connection = verw.getConnection();
		this.verw = verw;
		mapper = new ObjectMapper();
		JaxbAnnotationModule module = new JaxbAnnotationModule();
		mapper.registerModule(module);
	}

	/**
	 * speichert die Daten aus der gespeicherten SQL-Abfrage vom
	 * Verwaltungsobjekt im JSON-Format
	 */
	public void exportToJSON() {
		JFileChooser chooser = new JFileChooser(new File(VerwaltungConfig.ROOTPATH));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("JSON", "json"));
		File jsonFile = null;
		if (chooser.showSaveDialog(verw) == JFileChooser.APPROVE_OPTION)
			jsonFile = new File(chooser.getSelectedFile().getAbsolutePath() + ".json");
		else
			return;

		EntryListWrapper wrapper = new EntryListWrapper();
		ArrayList<Entry> aey = new ArrayList<Entry>();
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sqlStorage);
			while (rs.next()) {
				Entry entry = new Entry(rs.getLong(1), rs.getLong(2), rs.getDouble(3), rs.getString(4),
						rs.getString(5));
				aey.add(entry);
			}
		} catch (Exception e) {
			if (VerwaltungConfig.DEBUG)
				System.out.println(e.getMessage());
		}
		try {
			wrapper.setEntries(aey);
			write(wrapper, jsonFile);
		} catch (Exception e) {
			if (VerwaltungConfig.DEBUG)
				System.out.println(e.getMessage());
		}
		JOptionPane.showMessageDialog(verw, jsonFile.getAbsolutePath() + " wurde erstellt.");
	}

	/**
	 * Importiert aus dem JSON-Format die Daten in die Datenbank.
	 */
	public void importFromJSON() {
		JFileChooser chooser = new JFileChooser(new File(VerwaltungConfig.ROOTPATH));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("JSON", "json"));
		File jsonFile = null;
		if (chooser.showOpenDialog(this.verw) == JFileChooser.APPROVE_OPTION)
			jsonFile = chooser.getSelectedFile();
		else
			return;

		if (!jsonFile.exists()) {
			JOptionPane.showMessageDialog(verw, "Es existiert keine JSON-Datei");
			return;
		}

		try (PreparedStatement ps = connection.prepareStatement("INSERT INTO verwaltung VALUES ( ?, ?, ?, ? );");) {
			EntryListWrapper wrapper = read(jsonFile);

			ArrayList<Entry> aey = wrapper.getEntries();

			for (Entry e : aey) {
				if (isInDataBase(e.getDate()))
					continue;
				ps.setLong(1, e.getDate());
				ps.setDouble(2, e.getBetrag());
				ps.setString(3, e.getMarke());
				ps.setString(4, e.getText());
				ps.addBatch();
			}
			connection.setAutoCommit(false);
			ps.executeBatch();
			connection.setAutoCommit(true);
			verw.showTable();
		} catch (Exception e) {
			if (VerwaltungConfig.DEBUG)
				System.out.println(e.getMessage());
		}
	}

	/**
	 * schreibt die Daten in die Datei
	 * 
	 * @param elw
	 *            Die Daten als EntryListWrapper
	 * @param file
	 *            Die Datei in die geschrieben wird
	 * @throws IOException
	 */
	private void write(EntryListWrapper elw, File file) throws IOException {
		mapper.writeValue(file, elw);
	}

	/**
	 * liest die Daten aus der Datei
	 * 
	 * @param file
	 *            Die Datei, die die Daten enth�lt
	 * @return die Daten als EntryListWrapper Objekt.
	 * @throws IOException
	 */
	private EntryListWrapper read(File file) throws IOException {
		return mapper.readValue(file, EntryListWrapper.class);
	}

	/**
	 * pr�ft ob ein Datensatz mit dem �bergebenen long-Wert schon vorahnden ist
	 * 
	 * @param timestamp
	 *            die Datum-Representation in long
	 * @return true, wenn der Datensatz vorhanden ist
	 */
	private boolean isInDataBase(long timestamp) {
		long identifier = 0;
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM verwaltung WHERE" + " Datum = " + timestamp + ";");
			rs.next();
			identifier = rs.getLong(1);

			rs.close();
		} catch (Exception e) {
			if (VerwaltungConfig.DEBUG)
				System.out.println(e.getMessage());
		}
		if (identifier == timestamp)
			return true;
		return false;
	}
}
