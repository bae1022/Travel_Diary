package ddwu.mobile.finalproject.ma02_20180972.dto;

import java.io.Serializable;

public class TravelDto implements Serializable {
    private long id;
    private String date;
    private String place;
    private float stars;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }
}
