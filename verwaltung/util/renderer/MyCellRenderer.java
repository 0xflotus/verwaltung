package verwaltung.util.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import verwaltung.view.Verwaltung;

public class MyCellRenderer extends JLabel implements TableCellRenderer {
	private Verwaltung parent;

	public MyCellRenderer(Component parent) {
		this.parent = (Verwaltung) parent;
		this.setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		table.getTableHeader().setPreferredSize(new Dimension(120, 30));

		Font fontCB = parent.getContentPane().getFont();
		table.getTableHeader().setFont(fontCB.deriveFont(24f).deriveFont(Font.BOLD));

		if (value != null)
			this.setText(value.toString());

		this.setHorizontalAlignment(SwingConstants.CENTER);

		if (row == table.getSelectedRow()) {
			this.setBackground(Color.BLUE);
			this.setForeground(Color.WHITE);
		} else {
			this.setBackground(Color.WHITE);
			this.setForeground(Color.BLACK);
			if (column == 2 && Double.parseDouble(value.toString().replaceAll(",", ".")) < 0)
				this.setForeground(Color.RED);
		}

		if (column == 2)
			this.setHorizontalAlignment(SwingConstants.RIGHT);

		return this;
	}

}
