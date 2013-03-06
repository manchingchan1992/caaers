/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import org.apache.commons.collections15.functors.InstantiateFactory;

/**
 * @author Rhett Sutphin
 */
public class SAEReportPriorTherapyFactory<T> extends InstantiateFactory<T> {
    private SAEReportPriorTherapy saeReportPriorTherapy;

    public SAEReportPriorTherapyFactory(Class<T> classToInstantiate, SAEReportPriorTherapy parent) {
        super(classToInstantiate);
        this.saeReportPriorTherapy = parent;
    }

    @Override
    public T create() {
        T child = super.create();
        ((PriorTherapyAgent) child).setSaeReportPriorTherapy(saeReportPriorTherapy);
        return child;
    }
}
