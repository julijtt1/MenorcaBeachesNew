package org.android.menorcabeaches.model;

public class Post {
    private String id;
    private String img_path;
    private String description;
    private String userName;

    public Post() {
    }

    public Post(String id, String img_path, String description, String userName) {
        this.id = id;
        this.img_path = img_path;
        this.description = description;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return img_path;
    }

    public void setImage(String img_path) {
        this.img_path = img_path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

