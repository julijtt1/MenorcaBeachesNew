package org.android.menorcabeaches.model;

public class User {
    private String id;
    private String userName;
    private String name;
    private String img_path;
    private String description;

    public User(){}

    public User(String id, String user, String name, String img_path, String description) {
        this.id = id;
        this.userName = user;
        this.name = name;
        this.img_path = img_path;
        this.description = description;
    }

    public User(User value) {
        this.id = value.getId();
        this.userName = value.getUsername();
        this.name = value.getName();
        this.img_path = value.getImage();
        this.description = value.getDescription();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
