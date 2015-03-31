package domain;
import java.util.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
/**
 * Created by aravind on 2/27/15.
 */
public class Moderator {

    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private String created_at;

    public static List<Moderator> ModeratorList = new ArrayList<Moderator>();
    @JsonIgnore
    public List<Poll> PollList = new ArrayList<Poll>();
    public static List<Poll>PollGlobal = new ArrayList<Poll>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
