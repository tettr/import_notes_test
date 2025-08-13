package com.models;

public class Client {
    private String agency;
    private String guid;
    private String firstName;
    private String lastName;
    private String status;
    private String dob;
    private String createdDateTime;
    private String loggedUser;

    public Client() {}

    public Client(String agency, String guid, String firstName, String lastName,
                  String status, String dob, String createdDateTime) {
        this.agency = agency;
        this.guid = guid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.dob = dob;
        this.createdDateTime = createdDateTime;
    }

    public String getAgency() { return agency; }
    public void setAgency(String agency) { this.agency = agency; }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getCreatedDateTime() { return createdDateTime; }
    public void setCreatedDateTime(String createdDateTime) { this.createdDateTime = createdDateTime; }

}