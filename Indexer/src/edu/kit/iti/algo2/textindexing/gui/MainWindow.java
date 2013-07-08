package edu.kit.iti.algo2.textindexing.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import edu.kit.iti.algo2.textindexing.TimeSlot;
import edu.kit.iti.algo2.textindexing.alexdomge.index.InvertedIndex;
import edu.kit.iti.algo2.textindexing.alexdomge.index.InvertedIndexPickle;
import edu.kit.iti.algo2.textindexing.searchengine.DefaultSearchEngine;
import edu.kit.iti.algo2.textindexing.searchengine.SearchEngine;
import edu.kit.iti.algo2.textindexing.searchengine.SearchResult;
import edu.kit.iti.algo2.textindexing.searchengine.SearchResultEntry;
import edu.kit.iti.algo2.textindexing.searchengine.expr.Expr;
import edu.kit.iti.algo2.textindexing.searchengine.expr.LispSyntaxParser;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtSearch;
    private JList<SearchResultEntry> resultList = new JList<>();
    private JLabel lblStatus = new JLabel("Loading ...");
    private SearchEngine searchEngine;
    private SearchResultModel resultModel = new SearchResultModel();
    private AbstractAction actSearch = new SearchAction();

    public MainWindow(File indexFile) throws IOException {
	buildFrame();
	setSize(500, 500);
	InvertedIndex ii = new InvertedIndex();
	ii = InvertedIndexPickle.loadFrom(indexFile);
	searchEngine = new DefaultSearchEngine(ii);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	status("Index " + indexFile.getAbsolutePath() + " loaded");

	// debugging
	txtSearch.setText("'dient'");
	actSearch.actionPerformed(null);

    }

    private void status(String string) {
	lblStatus.setText(string);
    }

    private void buildFrame() {
	resultList.setCellRenderer(new SearchResultEntryRenderer());

	setLayout(new BorderLayout());
	add(northPanel(), BorderLayout.NORTH);
	add(centralPanel());
	add(statusPanel(), BorderLayout.SOUTH);
    }

    private Component statusPanel() {
	JPanel p = new JPanel(new GridLayout(1, 1));
	p.add(lblStatus);
	return p;
    }

    private Component northPanel() {
	Box box = new Box(BoxLayout.X_AXIS);

	JLabel lblSearch = new JLabel("Search: ");
	txtSearch = new JTextField(25);
	lblSearch.setLabelFor(txtSearch);

	actSearch.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ENTER"));
	JButton btn = new JButton(actSearch);
	btn.getActionMap().put("search", actSearch);
	btn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
	        (KeyStroke) actSearch.getValue(Action.ACCELERATOR_KEY), "search");
	
	box.add(lblSearch);
	box.add(txtSearch);
	box.add(btn);
	return box;
    }

    private Component centralPanel() {
	JScrollPane jsp = new JScrollPane(resultList);

	resultList.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
		    startVlc(resultList.getSelectedValue());
		}
	    }
	});
	return jsp;
    }

    private void startVlc(SearchResultEntry selectedValue) {
	TimeSlot ts = selectedValue.getTimeSlot();
	String file = ts.getTimedDocument().getMultimediaFile();
	
	String cmd = String.format("vlc --start-time=%d %s", ts.getStartTime(), file);
	System.out.println("exec: " + cmd);
	try {
	    Runtime.getRuntime().exec(cmd);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    class SearchAction extends AbstractAction {
	private static final long serialVersionUID = -1707731732195409399L;

	public SearchAction() {
	    putValue(Action.NAME, "Go!");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    Expr expr = new LispSyntaxParser(txtSearch.getText()).parse();
	    System.out.println(expr);
	    SearchResult sr = searchEngine.find(expr);
	    resultModel = new SearchResultModel(sr);
	    resultList.setModel(resultModel);
	}
    }
}
