package gui;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ContentTree {
    private JPanel treePanel;

    private boolean isFirstSelectClick = true;
    private ContentEditor contentEditor;

    private String ext;
    private String searchingText;
    private String pathToDirectory;


    public ContentTree(JPanel treePanel, JPanel rightPanel, JPanel mainPanel, JFrame frame) {
        this.treePanel = treePanel;
        contentEditor = new ContentEditor(rightPanel, mainPanel,frame);
    }

    public void buildTree(){
        DefaultMutableTreeNode root;
        DefaultMutableTreeNode currentParent = root = new DefaultMutableTreeNode(pathToDirectory);
        new Thread(() -> searchFiles(new File(pathToDirectory), currentParent)).start();

        TreeModel treeModel = new DefaultTreeModel(root);
        JTree tree = new JTree(treeModel);

        TreeSelectionModel selModel = new DefaultTreeSelectionModel();
        selModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        tree.setSelectionModel(selModel);
        tree.addTreeSelectionListener(getTreeSelectionListener());

        treePanel.add(new JScrollPane(tree));
        treePanel.updateUI();
    }

    public void reBuildTree(){
        treePanel.remove(0);
        contentEditor.removeTabs();
        buildTree();
    }

    private void searchFiles(File parent, DefaultMutableTreeNode currentParent){
        File files[] = parent.listFiles();
        HashMap<File, DefaultMutableTreeNode> folders = new HashMap<>();

        if (files == null || files.length == 0)
            return;

        for (File f : files) {
            if (f.isDirectory()) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(f.getName(),true);
                currentParent.add(node);
                folders.put(f,node);
            }
            else {
                if (!f.getName().endsWith(ext))
                    continue;
                try(BufferedReader reader = Files.newBufferedReader(f.toPath())) {
                    while (reader.ready())
                        if (reader.readLine().contains(searchingText)){
                            currentParent.add(new DefaultMutableTreeNode(f.getName(),false));
                            break;
                        }
                } catch (IOException e) {
                    // exception may occur if charset isn't maintained
                }
            }
        }

        for (Map.Entry<File,DefaultMutableTreeNode> entry : folders.entrySet())
            searchFiles(entry.getKey(),entry.getValue());
    }



    private TreeSelectionListener getTreeSelectionListener(){
        return e -> {
            JTree tr = (JTree)e.getSource();

            TreePath[] selected = tr.getSelectionPaths();
            String fileName = selected[0].toString().substring(1, selected[0].toString().length() -1).replaceAll(", ","\\\\");

            if (Files.isRegularFile(Paths.get(fileName))){
                contentEditor.setFileName(fileName);
                contentEditor.setMatchingText(searchingText);

                if (isFirstSelectClick) {
                    isFirstSelectClick = false;
                    contentEditor.buildEditor();
                }
                else
                    contentEditor.addNewTab();
            }
        };
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public void setSearchingText(String searchingText) {
        this.searchingText = searchingText;
    }

    public void setPathToDirectory(String pathToDirectory) {
        this.pathToDirectory = pathToDirectory;
    }
}
