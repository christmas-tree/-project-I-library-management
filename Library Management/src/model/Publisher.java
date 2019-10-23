package model;

public class Publisher {
    private String pubId;
    private String pubName;

    public Publisher(String pubId, String pubName) {
        this.pubId = pubId;
        this.pubName = pubName;
    }

    public String getPubId() {
        return pubId;
    }

    public void setPubId(String pubId) {
        this.pubId = pubId;
    }

    public String getPubName() {
        return pubName;
    }

    public void setPubName(String pubName) {
        this.pubName = pubName;
    }
}

