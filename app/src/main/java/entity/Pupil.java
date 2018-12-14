package entity;

public class Pupil {
    private String hashID;
    private String name;
    private String SSID;

    public Pupil(String hashID, String name, String SSID) {
        this.hashID = hashID;
        this.name = name;
        this.SSID = SSID;
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
}
