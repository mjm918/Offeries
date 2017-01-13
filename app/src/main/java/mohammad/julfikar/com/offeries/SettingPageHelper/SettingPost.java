package mohammad.julfikar.com.offeries.SettingPageHelper;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mobiletwo on 12/01/2017.
 */

@IgnoreExtraProperties
public class SettingPost {

    public String uid;
    public String name;
    public String location;
    public String gender;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public SettingPost() {

    }

    public SettingPost(String uid, String name, String location, String gender) {
        this.uid = uid;
        this.name = name;
        this.location = location;
        this.gender = gender;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", name);
        result.put("title", location);
        result.put("body", gender);
        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }

}