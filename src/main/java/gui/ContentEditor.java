package gui;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.*;
import javax.swing.text.*;

public class ContentEditor {
    private JFrame frame;
    private JTextPane textEditor;
    private JPanel rightPanel, mainPanel;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private String matchingText;
    private String fileName;
    private static int index = 0;

    private  Style normal;
    private  Style matching;

    public ContentEditor(JPanel rightPanel, JPanel mainPanel, JFrame frame){
        this.rightPanel = rightPanel;
        this.mainPanel = mainPanel;
        this.frame = frame;
    }

    public void buildEditor(){
        JButton remove = new JButton("remove tab");
        remove.addActionListener(e -> {
            int select = tabbedPane.getSelectedIndex();
            if (select >= 0) {
                tabbedPane.removeTabAt(select);
                index--;
            }
        });
        rightPanel.add(remove,BorderLayout.NORTH);
        rightPanel.add(tabbedPane);

        addNewTab();

        mainPanel.add(new JScrollPane(rightPanel));
        mainPanel.updateUI();
    }

    public void addNewTab(){
        textEditor = new JTextPane();

        createStyles();
        loadText();
        addDocumentStyle();
        frame.setSize(700, 600);

        tabbedPane.addTab(fileName.substring(fileName.lastIndexOf("\\") + 1),textEditor);
        tabbedPane.setSelectedIndex(index++);
        mainPanel.updateUI();
    }

    public void removeTabs(){
        tabbedPane.removeAll();
        index = 0;
    }

    private void createStyles() {
        String STYLE_normal = "normal";
        normal = textEditor.addStyle(STYLE_normal, null);
        String FONT_style = "Times New Roman";
        StyleConstants.setFontFamily(normal, FONT_style);
        StyleConstants.setFontSize(normal, 16);

        matching = textEditor.addStyle(STYLE_normal,null);
        StyleConstants.setFontSize(matching,16);
        StyleConstants.setBackground(matching,Color.green);
    }

    private void loadText(){
        try(BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
            String line;
            while ((line = reader.readLine()) != null){
                if (line.contains(matchingText))
                    insertText(textEditor,line,matching);
                else
                    insertText(textEditor, line, normal);
                insertText(textEditor,System.lineSeparator(),normal);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addDocumentStyle() {
        // Изменение стиля части текста
        SimpleAttributeSet blue = new SimpleAttributeSet();
        StyleConstants.setForeground(blue, Color.blue);
        StyledDocument doc = textEditor.getStyledDocument();
        doc.setCharacterAttributes(10, 9, blue, false);
    }

    private void insertText(JTextPane editor, String string, Style style) {
        try {
            Document doc = editor.getDocument();
            doc.insertString(doc.getLength(), string, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMatchingText(String matchingText) {
        this.matchingText = matchingText;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}