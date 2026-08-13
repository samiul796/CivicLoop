package civicloop.data;

import civicloop.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * SINGLE SOURCE OF TRUTH for all data.
 * Handles storage, business logic (exchange, trust updates), and file persistence.
 */


public class DataStore implements Serializable {


 private HashMap<String, User> users;
    private ArrayList<Item> items;
    private ArrayList<Service> services;
    private ArrayList<TimeCreditTransaction> transactions;
    private ArrayList<CommunityPost> posts;
    private HashMap<String, TrustScoreManager> trustManagers; // userId -> manager

    public DataStore() {
        users = new HashMap<>();
        items = new ArrayList<>();
        services = new ArrayList<>();
        transactions = new ArrayList<>();
        posts = new ArrayList<>();
        trustManagers = new HashMap<>();
    }

    public String registerUser(String name, String area, String password) {
    // 1000 থেকে 5000 এর মধ্যে একটি অব্যবহৃত ID খুঁজে বের করো
    String newId = generateUserId();
    User u = new User(name, area, password, newId);
    users.put(u.getUserId(), u);
    trustManagers.put(u.getUserId(), new TrustScoreManager(u.getUserId()));
    return u.getUserId();
}

// নতুন মেথড: 1000-5000 রেঞ্জে একটি অব্যবহৃত ID তৈরি করে
private String generateUserId() {
    for (int id = 1000; id <= 5000; id++) {
        String idStr = String.valueOf(id);
        if (!users.containsKey(idStr)) {
            return idStr;
        }
    }
    // সব ID ব্যবহার হয়ে গেলে (5000 জন ইউজার)
    return null;
}

    public User login(String userId, String password) {
        User u = users.get(userId);
        if (u != null && u.checkPassword(password)) return u;
        return null; // login failed
    }

    public User findUser(String userId) { return users.get(userId); }
    public HashMap<String, User> getAllUsers() { return users; }

    
    public void addItem(String itemName, User owner) {
        items.add(new Item(itemName, owner.getUserId()));
    }
    


    
    public ArrayList<Item> getItems() { return items; }

    /**
     * Core exchange logic for item borrowing (follows activity diagram).
     * Borrower spends TimeCredits, owner earns them. Trust scores increase.
     */
    public String requestItem(String itemId, User borrower, double hours) {
        Item item = findItemById(itemId);
        if (item == null) return "Item not found.";
        if (!item.isAvailable()) return "Item is already borrowed.";
        if (item.getOwnerId().equals(borrower.getUserId()))
            return "You cannot borrow your own item.";

        User owner = findUser(item.getOwnerId());
        if (owner == null) return "Owner not found.";

        // Create transaction (polymorphism: Item implements Creditable)
        TimeCreditTransaction t = new TimeCreditTransaction(
                borrower.getUserId(), owner.getUserId(), hours, item);

        // Update balances
        double credit = t.getCreditAmount();
        borrower.setTimeCreditBalance(borrower.getTimeCreditBalance() - credit);
        owner.setTimeCreditBalance(owner.getTimeCreditBalance() + credit);

        
        // Update trust scores (both gain trust)
        trustManagers.get(borrower.getUserId()).increaseScore(5);
        trustManagers.get(owner.getUserId()).increaseScore(5);
        // Sync user objects
        borrower.setTrustScore(trustManagers.get(borrower.getUserId()).getScore());
        owner.setTrustScore(trustManagers.get(owner.getUserId()).getScore());

        item.markBorrowed();
        transactions.add(t);
        return "Item borrowed successfully!";
    }

    
    private Item findItemById(String id) {
        for (Item i : items) if (i.getItemId().equals(id)) return i;
        return null;
    }

    public void addService(String serviceType, User provider) {
        services.add(new Service(serviceType, provider.getUserId()));
    }

    public ArrayList<Service> getServices() { return services; }

    public String requestService(String serviceId, User seeker, double hours) {
        Service s = findServiceById(serviceId);
        if (s == null) return "Service not found.";
        if (!s.isAvailable()) return "Service is currently busy.";
        if (s.getProviderId().equals(seeker.getUserId()))
            return "You cannot request your own service.";

        User provider = findUser(s.getProviderId());
        if (provider == null) return "Provider not found.";

        // Polymorphism again: Service implements Creditable (rate 1.0)
        TimeCreditTransaction t = new TimeCreditTransaction(
                seeker.getUserId(), provider.getUserId(), hours, s);

        double credit = t.getCreditAmount();
        seeker.setTimeCreditBalance(seeker.getTimeCreditBalance() - credit);
        provider.setTimeCreditBalance(provider.getTimeCreditBalance() + credit);

        trustManagers.get(seeker.getUserId()).increaseScore(5);
        trustManagers.get(provider.getUserId()).increaseScore(5);
        seeker.setTrustScore(trustManagers.get(seeker.getUserId()).getScore());
        provider.setTrustScore(trustManagers.get(provider.getUserId()).getScore());

        s.markBusy();
        transactions.add(t);
        return "Service requested successfully!";
    }

    

    private Service findServiceById(String id) {
        for (Service s : services) if (s.getServiceId().equals(id)) return s;
        return null;
    }

    // ---- Trust & Reputation (Member 4) ----
    public void reportLateReturn(String userId) {
        TrustScoreManager tm = trustManagers.get(userId);
        if (tm != null) {
            tm.decreaseScore(3);
            findUser(userId).setTrustScore(tm.getScore());
        }
    }

    public void reportFakeRequest(String userId) {
        TrustScoreManager tm = trustManagers.get(userId);
        if (tm != null) {
            tm.decreaseScore(10);
            findUser(userId).setTrustScore(tm.getScore());
        }
    }

     public int getTrustScore(String userId) {
        TrustScoreManager tm = trustManagers.get(userId);
        return tm != null ? tm.getScore() : 0;
    }

    // ---- Community Feed (Member 5) ----
    public void addPost(String authorId, String content) {
        posts.add(CommunityPost.createPost(authorId, content));
    }

    public ArrayList<CommunityPost> getPosts() { return posts; }

    public void likePost(String postId) {
        for (CommunityPost p : posts) {
            if (p.getPostId().equals(postId)) {
                p.addLike();
                break;
            }
        }
    }

     // ---- Transactions (Member 3) ----
    public ArrayList<TimeCreditTransaction> getTransactions() { return transactions; }

    // ---- File Persistence (java.io serialisation) ----
    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }

    public static DataStore loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (DataStore) ois.readObject();
        }
    }







}





