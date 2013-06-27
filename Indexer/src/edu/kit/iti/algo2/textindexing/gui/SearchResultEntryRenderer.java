package edu.kit.iti.algo2.textindexing.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class SearchResultEntryRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -2362287630886093145L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		return super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
	}
}
