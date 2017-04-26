package verwaltung.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import verwaltung.config.VerwaltungConfig;
import verwaltung.view.Eintrag;

/**
 * Eine KLasse um Testdaten zu erstellen
 * 
 * @author 0xflotus
 *
 */
public class TestDB {
	private final String DB_FILE = VerwaltungConfig.DB_FILE;
	private Connection connection;

	Random random = new Random();
	String[] marken = { "Essen", "Tanken", "Unterhaltung", "Software", "Geschaeftlich" };
	String[] text = { "Aral", "PS", "Kaugummi", "Winter", "Auto" };

	public TestDB() {
		loadDBDriver();
		initDataBase();
		testTableInDatabase();
	}

	private void initDataBase() {
		try {
			if (connection != null)
				return;
			System.out.println("Verbindung wird hergestellt.");
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
			System.out.println("Verbindung wurde hergestellt.");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	private void loadDBDriver() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	private void testTableInDatabase() {
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS verwaltung (" + "Datum       LONG  DEFAULT( 0 ),"
					+ "Betrag         DECIMAL  DEFAULT( 0 )," + "Marke      TEXT DEFAULT( '' ),"
					+ "Text      Text DEFAULT( '' )" + ");");

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public void clearTabelle() {
		try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM verwaltung;");

			ps.addBatch();

			connection.setAutoCommit(false);
			ps.executeBatch();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void fuellTabelle(int anzahl) {
		System.out.println("Gestartet um " + new Date());
		Date d = null;
		for (int i = 0; i < anzahl; i++) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, random.nextInt(19) + 1999);
			// c.set( Calendar.YEAR, 2017 );
			c.set(Calendar.DAY_OF_YEAR, random.nextInt(366) + 1);
			// c.set( Calendar.DAY_OF_YEAR, random.nextInt( 18 ) );
			c.set(Calendar.MINUTE, random.nextInt(60));
			c.set(Calendar.HOUR, random.nextInt(24));
			d = c.getTime();

			try {
				PreparedStatement ps = connection.prepareStatement("INSERT INTO verwaltung VALUES ( ?, ?, ?, ? );");

				ps.setLong(1, d.getTime());
				ps.setDouble(2,
						Double.parseDouble(String
								.format("%.2f",
										random.nextDouble() * random.nextInt(200) * (random.nextBoolean() ? -1 : 1))
								.replace(",", ".")));
				ps.setString(3,
						Eintrag.getDefaultMarkenListe().get(random.nextInt(Eintrag.getDefaultMarkenListe().size())));
				ps.setString(4, text[random.nextInt(text.length)]);
				ps.addBatch();

				connection.setAutoCommit(false);
				ps.executeBatch();
				connection.setAutoCommit(true);

			} catch (NumberFormatException | SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
