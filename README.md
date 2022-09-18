# Crypo events site
This site hosts all events in the crypto space. A user can navigate, check all events, read about a specific event 
and register for the event.

## NOTES

- For this development I'm using:
    - latest WCM core components (2.21.0)
    - latest AEM project archetype (version 37) 
    - AEM as a Cloud
    - JDK 11
    - latest maven 3.8.6
    - latest node 18.9.0
    
- My main focus was to demonstrated the reusability of WCM Component Library and OOTB AEM features.
- Due time constraint I didn't focus on UI design
- I used Beeceptor to mock API request/response.

## Technical Aspects
- For forms I used WCM form components (Form container, Text and button)
- All fields are sanitized and escaped using StringEscapeUtils.escape inside Jackson custom serializer 
(SanitizeAndSerializeString class) 
- All registrations generate a JSON file stored inside a folder in JCR, to access JCR I'm using a service user with 
write permissions via resource resolver.
- For error handling, I created a custom Exception (RegistrationException class)
- For form submission there is a Servlet handling POST requests. (FormRegistrationServlet)
- For form processing I created and delegated to a OSGI Service (FormRegistrationService) 
- for form, I'm using a experience fragment shared across all crypto events page.
- Because it is a simple website I'm using a single template. One potential improvement though,
could be create a separate template for events page.

## Instructions

- create a user service with write permissions to a JCR folder
- update OSGI Config file (com.cryptoevent.core.services.impl.FormRegistrationServiceImpl.cfg), 
inside config.publish folder
OR 
update via system console configuration
- run

    mvn clean install -PautoInstallPackagePublish
    
    Homepage: http://localhost:4503/content/cryptoevent/au/en.html