package com.cryptoevent.core.services.impl;

import com.cryptoevent.core.models.Register;
import com.cryptoevent.core.services.FormRegistrationService;
import com.cryptoevent.core.servlets.FormRegistrationServlet;
import com.cryptoevent.exception.RegistrationException;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jackrabbit.oak.commons.PropertiesUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

@Component(
        immediate = true,
        service = FormRegistrationService.class,
        configurationPid = "com.cryptoevent.core.services.impl.FormRegistrationServiceImpl"
)
@Designate(
        ocd = FormRegistrationServiceImpl.Configuration.class
)
public class FormRegistrationServiceImpl implements FormRegistrationService {

    @ObjectClassDefinition(
            name = "Form Registration Service",
            description = "Service responsible to handle form registration"
    )
    @interface Configuration {
        @AttributeDefinition(
                name = "Service user",
                description = "Service user with Write permission, required to store registration content as a JSON.",
                type = AttributeType.STRING
        )
        public String service_user() default "cryptoeventuser";

        @AttributeDefinition(
                name = "Folder Path",
                description = "Path where all registration files will be stored.",
                type = AttributeType.STRING
        )
        public String folder_path() default "/content/dam/cryptoevent";
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(FormRegistrationServiceImpl.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    private static final String RESPONSE_TYPE = "application/json";
    private String folderPath;
    private String serviceUser;

    // some local variables and methods here
    public void processRegistration(Register register) throws RegistrationException {
        sendRegistration(register);
        saveContent(register);
    }

    @Activate
    protected void activate(Configuration configuration) {
        folderPath = configuration.folder_path();
        serviceUser = configuration.service_user();
    }


    private void sendRegistration(Register register) throws RegistrationException{
        InputStream input = null;
        try {
            String url = "https://crypotevent.free.beeceptor.com/register";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod("POST");
            //Data to be posted

            // Send post request to Mock API
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(register.toString());
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            LOGGER.info("response code {}", responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer responseD = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                responseD.append(inputLine);
            }

            LOGGER.info("response body {}", responseD);

            in.close();

        } catch (IOException e) {
            LOGGER.error(RegistrationException.FAIL_SEND_REGISTRATION, e);
            throw new RegistrationException(RegistrationException.FAIL_SEND_REGISTRATION);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LOGGER.error(RegistrationException.FAIL_SEND_REGISTRATION, e);
                    throw new RegistrationException(RegistrationException.FAIL_SEND_REGISTRATION);
                }
            }
        }
    }

    private  void saveContent(Register register) throws RegistrationException{
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            InputStream is = new ByteArrayInputStream(objectMapper.writeValueAsBytes(register));

            String filePath = folderPath + "/" + UUID.randomUUID() + ".json";

            LOGGER.info("save file {}", objectMapper.writeValueAsString(register));
            LOGGER.info("path {}", filePath);
            LOGGER.info("service user {}", serviceUser);

            // get resolver with service user session
            HashMap<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, serviceUser);
            ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(param);

            // save file via assetManager API
            AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
            assetManager.createAsset(filePath, is, "application/json", true);

        } catch (JsonProcessingException e) {
            LOGGER.error(RegistrationException.FAIL_SAVE_REGISTRATION, e);
            throw new RegistrationException(RegistrationException.FAIL_SAVE_REGISTRATION);
        } catch(LoginException e) {
            LOGGER.error(RegistrationException.FAIL_SAVE_REGISTRATION, e);
            throw new RegistrationException(RegistrationException.FAIL_SAVE_REGISTRATION);
        }
    }
}