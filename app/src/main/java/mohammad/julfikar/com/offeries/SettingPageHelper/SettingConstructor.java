package mohammad.julfikar.com.offeries.SettingPageHelper;
import com.google.firebase.database.IgnoreExtraProperties;
/**
 * Created by mobiletwo on 12/01/2017.
 */
@IgnoreExtraProperties
public class SettingConstructor {

    private String name;
    private String location;
    private String gender;

    public SettingConstructor(){

    }
//    public SettingConstructor(String name, String location,String gender){
//        this.name = name;
//        this.location = location;
//        this.gender = gender;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
