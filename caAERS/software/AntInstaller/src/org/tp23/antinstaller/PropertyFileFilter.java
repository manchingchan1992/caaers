/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package org.tp23.antinstaller;

import java.io.File;
import java.io.FileFilter;

public class PropertyFileFilter implements FileFilter{

	public boolean accept(File pathname) {
		 boolean acceptance = false;
		if(pathname.getName().endsWith(".properties")){
			acceptance = true;
		}
		return acceptance;
	}
	
}
