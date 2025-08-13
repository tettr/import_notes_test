import com.component.ConnectionToOldServer;
import com.model.CompanyUser;
import com.model.PatientNote;
import com.model.PatientProfile;
import com.repository.CompanyUserRepository;
import com.repository.PatientNoteRepository;
import com.repository.PatientProfileRepository;
import com.service.ServerForImport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class ServerForImportTest {

    @Mock
    private ConnectionToOldServer connectionToOldServer;

    @Mock
    private PatientProfileRepository patientProfileRepository;

    @Mock
    private CompanyUserRepository companyUserRepository;

    @Mock
    private PatientNoteRepository patientNoteRepository;

    @InjectMocks
    private ServerForImport serverForImport;

    private final PatientProfile patient1 = new PatientProfile();
    private final String patient1OldClientGuid = "fixed-guid-001";

    private final PatientProfile patient2 = new PatientProfile();
    private final String patient2OldClientGuid = "fixed-guid-002";

    @BeforeEach
    void setUp() {
        patient1.setId(1L);
        patient1.setStatusId(200);
        patient1.setFirstName("Alice");
        patient1.setLastName("Smith");

        patient2.setId(2L);
        patient2.setStatusId(210);
        patient2.setFirstName("Bob");
        patient2.setLastName("Johnson");

        when(patientProfileRepository.findByStatusId(anyInt()))
                .thenAnswer(invocation -> {
                    int statusId = invocation.getArgument(0);
                    if (statusId == 200) return Collections.singletonList(patient1);
                    if (statusId == 210) return Collections.singletonList(patient2);
                    return Collections.emptyList();
                });

        when(patientProfileRepository.findAllGuid(eq(patient1.getId()))).thenReturn(Collections.singletonList(patient1OldClientGuid));
        when(patientProfileRepository.findAllGuid(eq(patient2.getId()))).thenReturn(Collections.singletonList(patient2OldClientGuid));

        CompanyUser user = new CompanyUser();
        user.setLogin("testUser");
        when(companyUserRepository.findByLogin(anyString()))
                .thenReturn(Optional.of(user));

        when(patientNoteRepository.findByGuid(anyString())).thenReturn(null);

        List<Map<String, Object>> clientsResponse = Arrays.asList(
                Map.of("guid", patient1OldClientGuid, "agency", "agencyX"),
                Map.of("guid", patient2OldClientGuid, "agency", "agencyY")
        );
        when(connectionToOldServer.getClients()).thenReturn(clientsResponse);

        List<Map<String, Object>> notesResponsePatient1 = List.of(
                Map.of(
                        "comments", "Note for Alice",
                        "guid", "note-guid-001",
                        "modifiedDateTime", "2023-10-01 12:00:00",
                        "clientGuid", patient1OldClientGuid,
                        "datetime", "2023-10-01 11:00:00",
                        "loggedUser", "userA",
                        "createdDateTime", "2023-10-01 10:00:00"
                )
        );

        List<Map<String, Object>> notesResponsePatient2 = List.of(
                Map.of(
                        "comments", "Note for Bob",
                        "guid", "note-guid-002",
                        "modifiedDateTime", "2023-10-02 12:00:00",
                        "clientGuid", patient2OldClientGuid,
                        "datetime", "2023-10-02 11:00:00",
                        "loggedUser", "userB",
                        "createdDateTime", "2023-10-02 10:00:00"
                )
        );

        when(connectionToOldServer.getNotes(anyString(), anyString(), anyString(), eq(patient1OldClientGuid)))
                .thenReturn(notesResponsePatient1);

        when(connectionToOldServer.getNotes(anyString(), anyString(), anyString(), eq(patient2OldClientGuid)))
                .thenReturn(notesResponsePatient2);
    }

    @Test
    void testImportNotes_DoesNotUpdateExistingNote_WhenNotModified() {
        PatientNote existingNote = new PatientNote();
        existingNote.setOldNotesGuid("note-guid-001");
        existingNote.setLastModifiedDateTime(LocalDateTime.of(2023, 10, 1, 12, 0));
        existingNote.setNote("Old note for Alice");
        when(patientNoteRepository.findByGuid("note-guid-001")).thenReturn(existingNote);

        List<Map<String, Object>> notesResponse = List.of(
                Map.of(
                        "comments", "Old note for Alice",
                        "guid", "note-guid-001",
                        "modifiedDateTime", "2023-10-01 11:00:00",
                        "clientGuid", "fixed-guid-001",
                        "datetime", "2023-10-01 10:00:00",
                        "loggedUser", "userA",
                        "createdDateTime", "2023-10-01 09:00:00"
                )
        );

        when(connectionToOldServer.getNotes(anyString(), anyString(), anyString(), eq("fixed-guid-001")))
                .thenReturn(notesResponse);

        serverForImport.importNotes();

        verify(patientNoteRepository, never()).save(any());

    }

    @Test
    void testUpdateExistingNote_WhenModifiedIsLater() {
        PatientNote existingNote = new PatientNote();
        existingNote.setOldNotesGuid("note-guid-001");
        existingNote.setLastModifiedDateTime(LocalDateTime.of(2023, 9, 30, 12, 0));

        when(patientNoteRepository.findByGuid("note-guid-001")).thenReturn(existingNote);

        serverForImport.importNotes();

        verify(patientNoteRepository).save(argThat(note ->
                note.getOldNotesGuid().equals("note-guid-001") &&
                        note.getLastModifiedDateTime().isAfter(LocalDateTime.of(2023,9,30,12,0))
        ));
    }

}