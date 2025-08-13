package com.controller;

import com.models.Note;
import com.models.NotesRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class NoteController {
    private static final List<Note> notes = new ArrayList<>();

    static {
        notes.add(new Note(
                "Patient Care Coordinator, reached out to patient caregiver is still in the hospital.",
                "20CBCEDA-3764-7F20-0BB6-4D6DD46BA9F8",
                "2021-11-15 14:51:59",
                "01588E84-D45A-EB98-F47F-716073A4F1EF",
                "2021-09-16 12:02:26 CDT",
                "p.vasya",
                "2021-11-15 11:51:59"
        ));
    }

    @PostMapping("/notes")
    public List<Note> getNotes(@RequestBody NotesRequest request) {
        List<Note> result = new ArrayList<>();
        for (Note note : notes) {
            if (note.getClientGuid().equals(request.getClientGuid())) {
                if (note.getCreatedDateTime().compareTo(request.getDateFrom()) >= 0 &&
                        note.getModifiedDateTime().compareTo(request.getDateTo()) <= 0) {
                    result.add(note);
                }
            }
        }
        return result;
    }
}