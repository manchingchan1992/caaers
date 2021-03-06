/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.admin;

import gov.nih.nci.cabig.caaers.tools.configuration.Configuration;
import gov.nih.nci.cabig.caaers.web.listener.Event;

import java.io.File;
import java.util.Collection;
import java.util.List;


public class DiagnosticsCommand extends ConfigurationCommand{
	
	private boolean smtpTestResult;
	private String caExchangeHandShakeResult;
	private String labViewerUrl;
	private String pscUrl;
	private String caExchangeUrl;
	private String smtpHost;
	private String smtpPort;
	private Throwable smtpException;
	private String smtpError;
	private boolean serviceMixUp;
	private String serviceMixUrl;
    private Collection<Event> events;
	
	
	public DiagnosticsCommand(Configuration configuration){
		super(configuration);
	}

	public boolean isSmtpTestResult() {
		return smtpTestResult;
	}

	public void setSmtpTestResult(boolean smtpTestResult) {
		this.smtpTestResult = smtpTestResult;
	}


	public String getCaExchangeHandShakeResult() {
		return caExchangeHandShakeResult;
	}

	public void setCaExchangeHandShakeResult(String caExchangeHandShakeResult) {
		this.caExchangeHandShakeResult = caExchangeHandShakeResult;
	}
	
	public String getLabViewerUrl() {
		labViewerUrl =  super.configuration.get(Configuration.LABVIEWER_BASE_URL);
		return labViewerUrl;
	}

	public String getCaExchangeUrl() {
		caExchangeUrl =  configuration.get(Configuration.CAEXCHANGE_URL);
		return caExchangeUrl;
	}
	
	public String getSmtpHost(){
		smtpHost =  configuration.get(Configuration.SMTP_ADDRESS);
		return smtpHost;
	}
	
	public String getSmtpPort(){
		smtpPort =  "" +configuration.get(Configuration.SMTP_PORT);
		return smtpPort;
	}

	public String getPscUrl() {
		pscUrl = configuration.get(Configuration.PSC_BASE_URL);
		return pscUrl;
	}

	public Throwable getSmtpException() {
		return smtpException;
	}

	public void setSmtpException(Throwable smtpException) {
		this.smtpException = smtpException;
	}

	public String getSmtpError() {
		if(smtpException != null){
			smtpError = smtpException.getMessage();
		}
		return smtpError;
	}

	public boolean isServiceMixUp() {
		return serviceMixUp;
	}

	public void setServiceMixUp(boolean serviceMixUp) {
		this.serviceMixUp = serviceMixUp;
	}

	public String getServiceMixUrl() {
		serviceMixUrl = super.configuration.get(Configuration.ESB_URL);
		return serviceMixUrl;
	}

    public String getLogFolder(){
        return configuration.get(Configuration.ESB_LOG_LOCATION);
    }

    public Collection<Event> getEvents() {
        return events;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }

    public boolean isLogFolderValid(){

        try{
            File f = new File(getLogFolder());
            return f.exists();
        }catch (Exception ignore){

        }
        return false;

    }
    public String getLogFolderException(){

        try{
            File f = new File(getLogFolder());
        }catch (Exception e){
             return e.getMessage();
        }
        return "";

    }
    
}
