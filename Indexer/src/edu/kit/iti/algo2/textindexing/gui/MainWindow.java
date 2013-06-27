package edu.kit.iti.algo2.textindexing.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import edu.kit.iti.algo2.textindexing.alexdomge.index.InvertedIndex;
import edu.kit.iti.algo2.textindexing.alexdomge.index.Occurrence;
import edu.kit.iti.algo2.textindexing.searchengine.DefaultSearchEngine;
import edu.kit.iti.algo2.textindexing.searchengine.SearchEngine;
import edu.kit.iti.algo2.textindexing.searchengine.SearchResult;
import edu.kit.iti.algo2.textindexing.searchengine.expr.Expr;
import edu.kit.iti.algo2.textindexing.searchengine.expr.LispSyntaxParser;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextField txtSearch;
	private JList<Occurrence> resultList = new JList<>();
	private JLabel lblStatus = new JLabel("Loading ...");
	private SearchEngine searchEngine;
	private SearchResultModel resultModel = new SearchResultModel();
	private AbstractAction actSearch = new SearchAction();

	public MainWindow() {
		buildFrame();
		setSize(500, 500);
		InvertedIndex ii = new InvertedIndex();
		ii.loadXML("test.xml");
		searchEngine = new DefaultSearchEngine(ii);
		status("text.xml loaded");
	}

	private void status(String string) {
		lblStatus.setText(string);
	}

	private void buildFrame() {
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

		JButton btn = new JButton(actSearch);
		box.add(lblSearch);
		box.add(txtSearch);
		box.add(btn);
		return box;
	}

	private Component centralPanel() {
		JScrollPane jsp = new JScrollPane(resultList);
		return jsp;
	}

	public static void main(String[] args) {
		new MainWindow().setVisible(true);
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
