package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.ProjectConfiguration;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.stereotype.Service;

@Service
public class QrCodeServiceImpl implements QrCodeService {
    private final PersonService personService;
    private final ProjectConfiguration projectConfiguration;

    public QrCodeServiceImpl(PersonService personService,
                             ProjectConfiguration projectConfiguration) {
        this.personService = personService;
        this.projectConfiguration = projectConfiguration;
    }

    @Override
    public byte[] getQrCode(Integer personId) {
        PersonObject personObject = personService.getPerson(personId);
        return getQrCodeByteArray(personObject);
    }

    @Override
    public byte[] recreateQrCode(Integer personId) {
        PersonObject personObject = personService.recreateHash(personId);
        return getQrCodeByteArray(personObject);
    }

    private byte[] getQrCodeByteArray(PersonObject personObject) {
        return QRCode.from(
                String.valueOf(personObject.getHash()))
                .withSize(projectConfiguration.getDefaultQrCodeWidth(),
                        projectConfiguration.getDefaultQrCodeHeight())
                .stream()
                .toByteArray();
    }
}
