package org.android.menorcabeaches.model;

public class Post {
    private String id;
    private String img_path;
    private String description;
    private String user_id;
    private String beach_id;
    private Double rating;

    public Post() {
    }

    public Post(String id, String img_path, String description, String user_id, String beachId, Double rating) {
        this.id = id;
        this.img_path = img_path;
        this.description = description;
        this.user_id = user_id;
        this.beach_id = beachId;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBeach_id() {
        return beach_id;
    }

    public void setBeach_id(String beach_id) {
        this.beach_id = beach_id;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", img_path='" + img_path + '\'' +
                ", description='" + description + '\'' +
                ", userName='" + user_id + '\'' +
                ", beachId='" + beach_id + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}

