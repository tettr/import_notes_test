package com.models;

public class NotesRequest {
    private String agency;
    private String dateFrom;
    private String dateTo;
    private String clientGuid;

    public String getAgency() { return agency; }
    public void setAgency(String agency) { this.agency = agency; }

    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String dateFrom) { this.dateFrom = dateFrom; }

    public String getDateTo() { return dateTo; }
    public void setDateTo(String dateTo) { this.dateTo = dateTo; }

    public String getClientGuid() { return clientGuid; }
    public void setClientGuid(String clientGuid) { this.clientGuid = clientGuid; }
}
