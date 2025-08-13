package com.service;

import com.repository.CompanyUserRepository;
import com.repository.PatientNoteRepository;
import com.repository.PatientProfileRepository;
import com.component.ConnectionToOldServer;
import com.model.CompanyUser;
import com.model.PatientNote;
import com.model.PatientProfile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



@Service
public class ServerForImport {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Logger logger = LoggerFactory.getLogger(ServerForImport.class);

    private final ConnectionToOldServer connectionToOldServer;
    private final PatientProfileRepository patientProfileRepository;
    private final CompanyUserRepository companyUserRepository;
    private final PatientNoteRepository patientNoteRepository;

    private static final List<Integer> ACTIVE_STATUS_IDS = Arrays.asList(200,210,230);

    public ServerForImport(ConnectionToOldServer connectionToOldServer,
                           PatientProfileRepository patientProfileRepository,
                           CompanyUserRepository companyUserRepository,
                           PatientNoteRepository patientNoteRepository) {
        this.connectionToOldServer = connectionToOldServer;
        this.patientProfileRepository = patientProfileRepository;
        this.companyUserRepository = companyUserRepository;
        this.patientNoteRepository = patientNoteRepository;
    }

    @Scheduled(cron = "0 41 0/2 * * *")                  /// 0 15 1/2 * * *
    public void runImport() {
        logger.info("Начало импорта заметок");
        try {
            importNotes();
        } catch (Exception e) {
            logger.error("Ошибка при импорте заметок", e);
        }
        logger.info("Завершение импорта");
    }

    @Transactional
    public void importNotes() {
        List<Map<String,Object>> clientsResponse =
                connectionToOldServer.getClients();

        List<PatientProfile> activePatients = new ArrayList<>();

        for (Integer id : ACTIVE_STATUS_IDS) {
            activePatients.addAll(patientProfileRepository.findByStatusId(id));
        }

        logger.info("Обнаружено активных пациентов: {}", activePatients.size());

        for (PatientProfile patient : activePatients) {

            List<String> guidsArray = new ArrayList<>(patientProfileRepository.findAllGuid(patient.getId()));

            for (String clientGuid : guidsArray) {
                clientGuid = clientGuid.trim();
                if (clientGuid.isEmpty()) continue;

                String finalClientGuid = clientGuid;
                Optional<Map<String,Object>> oldClientWGuid =
                        clientsResponse.stream()
                                .filter(c -> finalClientGuid.equalsIgnoreCase((String)c.get("guid")))
                                .findFirst();

                if (oldClientWGuid.isEmpty()) {
                    logger.warn("Клиент с guid {} не найден в старой системе", clientGuid);
                    continue;
                }

                Map<String,Object> oldClientGuidSet = oldClientWGuid.get();

                String agency = (String)oldClientGuidSet.get("agency");

                String dateFromStr = "2019-09-18";
                String dateToStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                List<Map<String,Object>> notesResponse =
                        connectionToOldServer.getNotes(agency, dateFromStr, dateToStr, clientGuid);

                for (Map<String,Object> noteData : notesResponse) {

                    String comments = (String)noteData.get("comments");
                    String guid = (String)noteData.get("guid");
                    String modifiedDateTime= noteData.get("modifiedDateTime").toString();
                    String clientGuid_ = (String)noteData.get("clientGuid");
                    String dateTime = (String)noteData.get("datetime");
                    String loggedUserLogin = (String)noteData.get("loggedUser");
                    String createdDateTime = (String)noteData.get("createdDateTime");

                    LocalDateTime lastModifiedDT = LocalDateTime.parse(modifiedDateTime, formatter);
                    LocalDateTime createdDT= LocalDateTime.parse(createdDateTime, formatter);

                    CompanyUser user =
                            companyUserRepository.findByLogin(loggedUserLogin).orElseGet(() -> {
                                CompanyUser newUser = new CompanyUser();
                                newUser.setLogin(loggedUserLogin);
                                return companyUserRepository.save(newUser);
                            });

                    PatientNote noteEntity = patientNoteRepository.findByGuid(guid);

                    if(Objects.nonNull(noteEntity)) {
                        boolean updated=false;

                        if(lastModifiedDT.isAfter(noteEntity.getLastModifiedDateTime())) {
                            noteEntity.setCreatedDateTime(createdDT);
                            noteEntity.setLastModifiedDateTime(lastModifiedDT);
                            noteEntity.setNote(comments);
                            noteEntity.setLastModifiedByUserId(user);
                            updated=true;
                        }
                        if(updated) patientNoteRepository.save(noteEntity);

                    } else {
                        noteEntity = new PatientNote();
                        noteEntity.setCreatedDateTime(createdDT);
                        noteEntity.setLastModifiedDateTime(lastModifiedDT);
                        noteEntity.setNote(comments);
                        noteEntity.setCreatedByUserId(user);
                        noteEntity.setLastModifiedByUserId(user);
                        noteEntity.setOldNotesGuid(guid);
                        noteEntity.setPatientProfileId(patient);

                        Optional<PatientProfile> patientOpt =
                                patientProfileRepository.findById(patient.getId());

                        PatientNote finalNoteEntity = noteEntity;
                        patientOpt.ifPresent(p -> {
                            finalNoteEntity.setPatientProfileId(p);
                            try {
                                patientNoteRepository.save(finalNoteEntity);
                            } catch (Exception e) {
                                logger.error("Ошибка сохранения");
                            }
                        });
                    }
                }
            }
        }
    }
}