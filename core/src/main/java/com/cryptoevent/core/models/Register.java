package com.cryptoevent.core.models;

import com.cryptoevent.jackson.SanitizeAndSerializeString;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Register {

    private static final Logger LOGGER = LoggerFactory.getLogger(Register.class);

    @Self
    private SlingHttpServletRequest request;

    @JsonProperty("name")
    @JsonSerialize(using = SanitizeAndSerializeString.class)
    private String name;

    @JsonProperty("email")
    @JsonSerialize(using = SanitizeAndSerializeString.class)
    private String email;

    @JsonProperty("notes")
    @JsonSerialize(using = SanitizeAndSerializeString.class)
    private String notes;

    @PostConstruct
    protected void init() {
        name = request.getParameter("name");
        email = request.getParameter("email");
        notes = request.getParameter("notes");
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNotes() {
        return notes;
    }

}


