package life.oleg.vkoauth;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    long id;
    String first_name;
    String last_name;
    String city;
    String photoUrl;
    String online;

    public User(long id, String first_name, String last_name, String city, String photoUrl, String online) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.city = city;
        this.photoUrl = photoUrl;
        this.online = online;
    }

    public User() {
    }

    public User parser(JSONObject jsonObject) {
        try {
            long id = jsonObject.getLong("id");
            String first_name = jsonObject.getString("first_name");
            String last_name = jsonObject.getString("last_name");
//            String city = jsonObject.getJSONObject("city").getString("title");
            String city;
            if (jsonObject.has("city")) {
                city = jsonObject.getJSONObject("city").getString("title");
            } else {
                city = "";
            }
            String photoUrl = jsonObject.getString("photo_200");
            String online;
            if (jsonObject.getInt("online") == 0) {
                online = "";
            } else {
                online = "online";
            }
            return new User(id, first_name, last_name, city, photoUrl, online);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getCity() {
        return city;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getOnline() {
        return online;
    }
}
