/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.dwr;

public class IndexChange {
    private Integer original, current;

    private String currentDisplayName;

    public IndexChange(Integer original, Integer current) {
        this.original = original;
        this.current = current;
    }

    public Integer getOriginal() {
        return original;
    }

    public Integer getCurrent() {
        return current;
    }

    public String getCurrentDisplayName() {
        return currentDisplayName;
    }

    public void setCurrentDisplayName(String currentDisplayName) {
        this.currentDisplayName = currentDisplayName;
    }

    @Override
    public String toString() {
        return String.format("%d => %d", original, current);
    }
}
