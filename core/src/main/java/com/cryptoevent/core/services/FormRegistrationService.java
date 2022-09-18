package com.cryptoevent.core.services;

import com.cryptoevent.core.models.Register;
import com.cryptoevent.exception.RegistrationException;

public interface FormRegistrationService {
    public void processRegistration(Register register) throws RegistrationException;
}
