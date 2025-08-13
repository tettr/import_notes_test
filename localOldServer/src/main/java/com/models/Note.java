package com.models;

public class Note {
    private String comments;
    private String guid;
    private String modifiedDateTime;
    private String clientGuid;
    private String datetime;
    private String createdDateTime;
    private String loggedUser;

    public Note() {}

    public Note(String comments, String guid, String modifiedDateTime,
                String clientGuid, String datetime, String loggedUser, String createdDateTime) {
        this.comments = comments;
        this.guid = guid;
        this.modifiedDateTime = modifiedDateTime;
        this.clientGuid = clientGuid;
        this.datetime = datetime;
        this.loggedUser= loggedUser;
        this.createdDateTime = createdDateTime;
    }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }

    public String getModifiedDateTime() { return modifiedDateTime; }
    public void setModifiedDateTime(String modifiedDateTime) { this.modifiedDateTime = modifiedDateTime; }

    public String getClientGuid() { return clientGuid; }
    public void setClientGuid(String clientGuid) { this.clientGuid = clientGuid; }

    public String getDatetime() { return datetime; }
    public void setDatetime(String datetime) { this.datetime = datetime; }

    public String getLoggedUser() { return loggedUser; }
    public void setLoggedUser(String loggedUser) { this.loggedUser = loggedUser; }

    public String getCreatedDateTime() { return createdDateTime; }
    public void setCreatedDateTime(String loggedUser) { this.createdDateTime = createdDateTime; }
}