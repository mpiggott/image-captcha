package ca.piggott.captcha;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class CaptchaConfiguration extends Configuration {

    @NotNull
    @JsonProperty
	private String termFile;

    @NotNull
    @JsonProperty
	private String simpleApiKey;

    @NotNull
    @JsonProperty
    private String cx;

    public String getTermFile()
    {
    	return termFile;
    }

    public String getCredentials()
    {
    	return simpleApiKey;
    }

    public String getCx() {
    	return cx;
    }
}
