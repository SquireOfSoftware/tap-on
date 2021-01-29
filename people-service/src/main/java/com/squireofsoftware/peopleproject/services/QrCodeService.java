package com.squireofsoftware.peopleproject.services;

import java.io.IOException;

public interface QrCodeService {
    byte[] getQrCode(Integer personId) throws IOException;
    byte[] recreateQrCode(Integer personId) throws IOException;
}
