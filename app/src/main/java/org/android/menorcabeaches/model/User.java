package org.android.menorcabeaches.model;

public class User {
    private String id;
    private String user_id;
    private String name;
    private String img_path;
    private String description;

    public User(){}

    public User(String id, String user, String name, String img_path, String description) {
        this.id = id;
        this.user_id = user;
        this.name = name;
        this.img_path = img_path;
        this.description = description;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", name='" + name + '\'' +
                ", img_path='" + img_path + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
