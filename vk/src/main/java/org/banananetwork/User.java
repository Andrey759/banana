package org.banananetwork;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author karyakin dmitry
 *         date 30.07.15.
 */
public class User {

    private long id;
    private String pic;
    private String name;
    private Friend.Gender gender;
    private String workplace;
    private long universityId;
    private Timestamp lastModified;
    private Timestamp lastScan;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Friend.Gender getGender() {
        return gender;
    }

    public void setGender(Friend.Gender gender) {
        this.gender = gender;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public long getUniversityId() {
        return universityId;
    }

    public void setUniversityId(long universityId) {
        this.universityId = universityId;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public Timestamp getLastScan() {
        return lastScan;
    }

    public void setLastScan(Timestamp lastScan) {
        this.lastScan = lastScan;
    }
}
