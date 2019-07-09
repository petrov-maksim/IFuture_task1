package gui;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel mainPanel = new JPanel(new GridLayout(1,2));
    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel(new BorderLayout());

    private JPanel btnPanel = new JPanel();
    private JPanel inputPanel = new JPanel();
    private JPanel treePanel = new JPanel();

    private JFileChooser fileChooser;
    private JButton choseDirectoryBtn;
    private JButton searchBtn;
    private JTextField inputText;
    private JTextField extension;

    private String pathToDirectory;
    private String ext;
    private String searchingText;

    private ContentTree contentTree;
    private boolean isFirstSearchClick = true;


    public MainFrame() throws HeadlessException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        init();

        setContentPane(mainPanel);
        setSize(400,500);
        setVisible(true);
    }

    private void init(){
        choseDirectoryBtn = new JButton("Выбрать директорию");
        searchBtn = new JButton("Искать");
        btnPanel.add(searchBtn);
        btnPanel.add(choseDirectoryBtn);

        fileChooser = new JFileChooser();

        inputText = new JTextField(15);
        inputText.setToolTipText("Введите искомый текст");
        inputPanel.add(inputText);

        extension = new JTextField(15);
        extension.setToolTipText("Введите расширение файла");
        inputPanel.add(extension);

        leftPanel.add(inputPanel);
        leftPanel.add(btnPanel);
        leftPanel.add(treePanel);

        mainPanel.add(leftPanel);

        contentTree = new ContentTree(treePanel, rightPanel, mainPanel,this);

        setBtnListeners();
    }

    private void setBtnListeners(){
        choseDirectoryBtn.addActionListener((event ->{
            fileChooser.setDialogTitle("Выбор директории");
            // Определение режима - только каталог
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(MainFrame.this);
            if (result == JFileChooser.APPROVE_OPTION )
                pathToDirectory = fileChooser.getSelectedFile().getAbsolutePath();
        }));

        searchBtn.addActionListener(event ->{
            ext = extension.getText().isEmpty() ? ".log" : extension.getText();
            searchingText = inputText.getText();

            contentTree.setExt(ext);
            contentTree.setPathToDirectory(pathToDirectory);
            contentTree.setSearchingText(searchingText);

            if (pathToDirectory != null && !pathToDirectory.isEmpty() && !inputText.getText().isEmpty()) {
                if (isFirstSearchClick){
                    isFirstSearchClick = false;
                    contentTree.buildTree();
                } else
                    contentTree.reBuildTree();
            }
        });
    }
}
