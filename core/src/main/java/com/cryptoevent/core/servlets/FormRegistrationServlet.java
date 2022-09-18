/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.cryptoevent.core.servlets;

import com.cryptoevent.core.models.Register;
import com.cryptoevent.core.services.FormRegistrationService;
import com.cryptoevent.exception.RegistrationException;
import com.day.cq.dam.api.AssetManager;
import com.drew.lang.annotations.NotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


@Component(
        immediate = true,
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/registration",
                "sling.servlet.methods=POST"
        },
        configurationPid = "com.cryptoevent.core.servlets.FormRegistrationServlet"
)
public class FormRegistrationServlet extends SlingAllMethodsServlet {

    public static final String RESPONSE_CONTENT_TYPE = "application/json";

    @Reference
    private FormRegistrationService formRegistrationService;

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(FormRegistrationServlet.class);

    @Override
    protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        Register register = objectMapper.readValue(request.getInputStream(), Register.class);

        try {
            formRegistrationService.processRegistration(register);
        } catch (RegistrationException e) {
            if (e.getMessage().equalsIgnoreCase(RegistrationException.FAIL_SAVE_REGISTRATION)) {
                response.sendError(response.SC_INTERNAL_SERVER_ERROR, RegistrationException.FAIL_SAVE_REGISTRATION);
                return;
            } else if (e.getMessage().equalsIgnoreCase(RegistrationException.FAIL_SEND_REGISTRATION)) {
                response.sendError(response.SC_INTERNAL_SERVER_ERROR, RegistrationException.FAIL_SEND_REGISTRATION);
                return;
            }
        }

        response.setStatus(response.SC_OK);
    }
}
