package civicloop.gui;

import civicloop.model.CommunityPost;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


/**
 * Community Feed where users can post messages and like posts.
 */
public class FeedPanel extends JPanel {
    private MainFrame parent;
    private DefaultListModel<String> postListModel;
    private JList<String> postList;
    private JTextArea postInput;

    

    public FeedPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        // List of posts (newest first)
        postListModel = new DefaultListModel<>();
        postList = new JList<>(postListModel);
        add(new JScrollPane(postList), BorderLayout.CENTER);

        // Bottom: input area and buttons
        JPanel bottom = new JPanel(new BorderLayout());
        postInput = new JTextArea(3, 30);
        postInput.setLineWrap(true);
        JScrollPane inputScroll = new JScrollPane(postInput);
        bottom.add(inputScroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton postBtn = new JButton("Post to Feed");
        JButton likeBtn = new JButton("Like Selected Post");
        btnPanel.add(postBtn);
        btnPanel.add(likeBtn);
        bottom.add(btnPanel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        postBtn.addActionListener(e -> addPost());
        likeBtn.addActionListener(e -> likePost());
        refresh();
    }

    private void addPost() {
        String content = postInput.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cannot post an empty message.");
            return;
        }
        parent.getDataStore().addPost(parent.getCurrentUser().getUserId(), content);
        postInput.setText("");
        parent.refreshAll();  // refresh feed and other tabs
    }

    
    private void likePost() {
        String selected = postList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a post to like.");
            return;
        }
        // Extract postId from the beginning of the string (format: [id] ...)
        // For simplicity we find the post object by matching the displayed text.
        // A better approach: store post IDs in a parallel list. Here we search in DataStore.
        ArrayList<CommunityPost> posts = parent.getDataStore().getPosts();
        // The list is newest-first; find by matching toString()
        for (CommunityPost p : posts) {
            if (p.toString().equals(selected)) {
                parent.getDataStore().likePost(p.getPostId());
                parent.refreshAll();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Post not found.");
    }

    public void refresh() {
        postListModel.clear();
        // Display posts newest first (reverse order of insertion)
        ArrayList<CommunityPost> posts = parent.getDataStore().getPosts();
        for (int i = posts.size() - 1; i >= 0; i--) {
            postListModel.addElement(posts.get(i).toString());
        }
    }
}