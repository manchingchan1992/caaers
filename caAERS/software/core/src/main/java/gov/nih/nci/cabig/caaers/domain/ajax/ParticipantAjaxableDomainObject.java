/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain.ajax;

import java.util.*;

/**
 *
 */
public class ParticipantAjaxableDomainObject extends AbstractAjaxableDomainObject {


    private String lastName;
    private String firstName;
    private String gender;
    private String race;
    private String ethnicity;
    private String primaryIdentifierValue;
    private Set<String> studySubjectIdentifiers;
    private String studySubjectIdentifiersString;

  //  List<StudySiteAjaxableDomainObject> studySites = new ArrayList<StudySiteAjaxableDomainObject>();
    List<StudySearchableAjaxableDomainObject> studies = new ArrayList<StudySearchableAjaxableDomainObject>();

    //FIXME : this logic belongs to participant
    public String getDisplayName() {

        String primaryIdentifier = this.getPrimaryIdentifierValue() == null ? "" : " ( " + this.getPrimaryIdentifierValue() + " ) ";
        StringBuilder name = new StringBuilder(primaryIdentifier);
        boolean hasLastName = getLastName() != null;
        if (getFirstName() != null) {
            name.append(getFirstName());
            if (hasLastName) {
                name.append(' ');
            }
        }
        if (hasLastName) {
            name.append(getLastName());
        }
        if (getStudySubjectIdentifiersString() != null)
            name.append(" - (" + getStudySubjectIdentifiersString() + ")");
        return name.toString() ;

    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {

        return lastName;
    }

    public String getPrimaryIdentifierValue() {
        return primaryIdentifierValue;
    }

    public void setPrimaryIdentifierValue(String primaryIdentifierValue) {
        this.primaryIdentifierValue = primaryIdentifierValue;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<StudySearchableAjaxableDomainObject> getStudies() {
        return studies;
    }

	public void addStudy(StudySearchableAjaxableDomainObject studySearchableAjaxableDomainObject) {
        if (getObjectById(this.getStudies(), studySearchableAjaxableDomainObject.getId()) == null) {
        	getStudies().add(studySearchableAjaxableDomainObject);
        } else {
        	getStudies().remove(studySearchableAjaxableDomainObject);
        	getStudies().add(studySearchableAjaxableDomainObject);
        }

    }
	/*
    public List<StudySiteAjaxableDomainObject> getStudySites() {
        return studySites;
    }

	public void addStudySite(StudySiteAjaxableDomainObject studySiteAjaxableDomainObject) {
        if (getObjectById(this.getStudySites(), studySiteAjaxableDomainObject.getId()) == null) {
            getStudySites().add(studySiteAjaxableDomainObject);
        }

    }   
    */
    protected AbstractAjaxableDomainObject getObjectById(List<? extends AbstractAjaxableDomainObject> ajaxableDomainObjects, Integer id) {
        for (AbstractAjaxableDomainObject object : ajaxableDomainObjects) {
            if (object.getId().equals(id)) {
                return object;
            }
        }
        return null;
    }

	public String getEthnicity() {
		return ethnicity;
	}

	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

    public Set getStudySubjectIdentifiers() {
        if (studySubjectIdentifiers == null) studySubjectIdentifiers = new HashSet<String>();
        return studySubjectIdentifiers;
    }

    public void setStudySubjectIdentifiers(Set studySubjectIdentifiers) {
        this.studySubjectIdentifiers = studySubjectIdentifiers;
    }

    public void collectStudySubjectIdentifier(String studySubjectIdentifier) {
        if (getStudySubjectIdentifiers() == null) setStudySubjectIdentifiers(new HashSet<String>());
        getStudySubjectIdentifiers().add(studySubjectIdentifier);
    }

    public String getStudySubjectIdentifiersCSV() {
        Iterator it = getStudySubjectIdentifiers().iterator();
        StringBuffer sb = new StringBuffer("");
        while (it.hasNext()) {
            sb.append(it.next().toString() + ", ");
        }
        if (sb.length() > 0)
            return sb.substring(0, sb.length() - 2);
        else return "";
    }

    public String getStudySubjectIdentifiersString() {
        return studySubjectIdentifiersString;
    }

    public void setStudySubjectIdentifiersString(String studySubjectIdentifiersString) {
        this.studySubjectIdentifiersString = studySubjectIdentifiersString;
    }
}
