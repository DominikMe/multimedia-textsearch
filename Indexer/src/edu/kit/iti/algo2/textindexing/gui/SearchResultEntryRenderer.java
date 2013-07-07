package edu.kit.iti.algo2.textindexing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.util.Collection;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import edu.kit.iti.algo2.textindexing.TimeSlot;
import edu.kit.iti.algo2.textindexing.searchengine.SearchResultEntry;

public class SearchResultEntryRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = -2362287630886093145L;

    private JPanel comp, north;
    private JLabel lblFileName, lblTime;
    private JEditorPane preview;

    public SearchResultEntryRenderer() {
	comp = new JPanel(new BorderLayout());
	north = new JPanel(new BorderLayout());

	preview = new JEditorPane();
	lblFileName = new JLabel();
	lblTime = new JLabel();

	preview.setContentType("text/html");

	north.add(lblFileName);
	north.add(lblTime, BorderLayout.EAST);
	comp.add(north, BorderLayout.NORTH);
	comp.add(preview);

	preview.setPreferredSize(new Dimension(0, 200));
	preview.setSize(new Dimension(0, 200));
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
	    int index, boolean isSelected, boolean cellHasFocus) {

	JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value,
		index, isSelected, cellHasFocus);

	if (isSelected) {
	    preview.setBackground(Color.LIGHT_GRAY);
	} else {
	    preview.setBackground(Color.WHITE);
	}

//	copyColor(lbl, comp, lblFileName, lblTime);

	SearchResultEntry sre = (SearchResultEntry) value;
	TimeSlot ts = sre.getTimeSlot();
	lblTime.setText(humanReadableTime(ts));
	lblFileName.setText(ts.getTimedDocument().getMultimediaFile());

	preview.setText(generatePreview(ts.getContent(), sre));
	return comp;
    }

    private void copyColor(JLabel lbl, JComponent... c) {
	for (JComponent j : c) {
	    j.setBackground(lbl.getBackground());
	    j.setForeground(lbl.getForeground());
	}
    }

    private String generatePreview(Collection<String> content,
	    SearchResultEntry sre) {
	Set<String> mw = sre.getMatchedWords();
	StringBuffer sb = new StringBuffer();
	for (String string : content) {
	    if (mw.contains(string)) {
		sb.append("<font color='red'>");
		sb.append(string);
		sb.append("</font>");
		sb.append(" ");
	    } else {
		sb.append(string);
		sb.append(" ");
	    }
	}
	return sb.toString();
    }

    private String humanReadableTime(TimeSlot ts) {
	// TODO better min:sec
	return time(ts.getStartTime()) + " - " + time(ts.getEndTime());
    }

    private String time(int i) {

	final int m = i / 60;
	final int s = i % 60;
	return String.format("%02d:%02d", m, s);
    }

}
