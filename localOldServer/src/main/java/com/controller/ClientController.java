package com.controller;

import com.models.Client;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ClientController {

    private static final List<Client> clients = new ArrayList<>();

    static {
        clients.add(new Client("vhh4",
                "01588E84-D45A-EB98-F47F-716073A4F1EF",
                "Ne",
                "Abr",
                "INACTIVE",
                "10-15-1999",
                "2021-11-15 11:51:59"));
        clients.add(new Client("vhh4",
                "01588E84-D45A-EB98-F47F-716073A4F1EF4",
                "Ne",
                "Abr",
                "ACTIVE",
                "10-15-1999",
                "2021-11-15 14:51:59"));
        clients.add(new Client("vhh4",
                "01588E84-D45A-EB98-F47F-716073A4F1EF64",
                "Ne",
                "Abr",
                "ACTIVE",
                "10-15-1999",
                "2021-11-15 12:51:59"));
    }

    @PostMapping("/clients")
    public List<Client> getAllClients() {
        return clients;
    }
}