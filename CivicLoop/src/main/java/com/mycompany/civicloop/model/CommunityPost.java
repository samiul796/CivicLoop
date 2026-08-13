package civicloop.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * A post on the Community Feed.
 * Uses a STATIC FACTORY METHOD createPost() instead of a public constructor
 * to centralise ID/timestamp generation (as required by the UML diagram).
 */

public class CommunityPost implements Serializable {

private String postId;
    private String authorId;
    private String content;
    private String timestamp;
    private int likes;
    private ArrayList<String> comments;

    private CommunityPost(String authorId, String content) {
        this.postId = UUID.randomUUID().toString().substring(0, 8);
        this.authorId = authorId;
        this.content = content;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        this.likes = 0;
        this.comments = new ArrayList<>();
    }



    
    /** Static factory method (as in diagram) */
    public static CommunityPost createPost(String authorId, String content) {
        return new CommunityPost(authorId, content);
    }


     public void addLike() { likes++; }
    public void addComment(String comment) { comments.add(comment); }

    // Getters
    public String getPostId() { return postId; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public int getLikes() { return likes; }
    public ArrayList<String> getComments() { return comments; }


     @Override
    public String toString() {
        return String.format("[%s] %s: %s (♥ %d, 💬 %d)",
                timestamp, authorId, content, likes, comments.size());
    }




}



