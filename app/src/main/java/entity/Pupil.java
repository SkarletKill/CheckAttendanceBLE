package entity;

public class Pupil {
    private String hashID;
    private String name;
    private String SSID;
    private boolean isPresent;

    public Pupil(String hashID, String name, String SSID) {
        this.hashID = hashID;
        this.name = name;
        this.SSID = SSID;
        isPresent = false;
    }

    public String getHashID() {
        return hashID;
    }

    public String getName() {
        return name;
    }

    public String getSSID() {
        return SSID;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }
}
