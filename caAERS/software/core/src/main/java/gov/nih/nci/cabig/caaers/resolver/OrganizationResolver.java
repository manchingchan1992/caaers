package gov.nih.nci.cabig.caaers.resolver;import edu.duke.cabig.c3pr.esb.Metadata;import edu.duke.cabig.c3pr.esb.OperationNameEnum;import edu.duke.cabig.c3pr.esb.ServiceTypeEnum;import edu.duke.cabig.c3pr.esb.impl.CaXchangeMessageBroadcasterImpl;import gov.nih.nci.cabig.caaers.domain.Organization;import gov.nih.nci.cabig.caaers.domain.RemoteOrganization;import gov.nih.nci.cabig.caaers.utils.XMLUtil;import gov.nih.nci.coppa.po.IdentifiedOrganization;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import java.util.Map;import org.apache.log4j.Logger;import org.iso._21090.II;import com.semanticbits.coppa.infrastructure.service.RemoteResolver;import com.semanticbits.coppasimulator.util.CoppaObjectFactory;public class OrganizationResolver extends BaseResolver implements RemoteResolver{ 	private static Logger logger = Logger.getLogger(OrganizationResolver.class);	//private MessageBroadcastService coppaMessageBroadcastService;/*	public Organization populateRemoteOrganization(gov.nih.nci.coppa.po.Organization coppaOrganization){				// using coppa organization identier and previously obtained id of CTEP (hard coded in CoppaObjectFactory.getIIOfCTEP) get Identified organization 		IdentifiedOrganization identifiedOrganization = CoppaObjectFactory.getCoppaIdentfiedOrganizationSearchCriteriaForCorrelation(coppaOrganization.getIdentifier());				String identifiedOrganizationXml = CoppaObjectFactory.getCoppaIdentfiedOrganization(identifiedOrganization);				Metadata mData = new Metadata(OperationNameEnum.search.getName(), "externalId", ServiceTypeEnum.IDENTIFIED_ORGANIZATION.getName());		String resultXml = broadcastCOPPA(identifiedOrganizationXml, mData);				List<String> results = XMLUtil.getObjectsFromCoppaResponse(resultXml);		if (results.size()>0) {			System.out.println(results.get(0));			identifiedOrganization = CoppaObjectFactory.getCoppaIdentfiedOrganization(results.get(0));		}		Organization remoteOrganization = new RemoteOrganization();		if (identifiedOrganization.getAssignedId() != null ) {			// CTEP ID 			remoteOrganization.setNciInstituteCode(identifiedOrganization.getAssignedId().getExtension());			} else {			return null;		}		remoteOrganization.setName(CoppaObjectFactory.getName(coppaOrganization.getName()));		remoteOrganization.setCity(CoppaObjectFactory.getCity(coppaOrganization.getPostalAddress()));		remoteOrganization.setCountry(CoppaObjectFactory.getCountry(coppaOrganization.getPostalAddress()));		remoteOrganization.setExternalId(coppaOrganization.getIdentifier().getExtension());		return remoteOrganization;	}*/		public Organization populateRemoteOrganization(gov.nih.nci.coppa.po.Organization coppaOrganization,IdentifiedOrganization identifiedOrganization){		Organization remoteOrganization = new RemoteOrganization();		remoteOrganization.setNciInstituteCode(identifiedOrganization.getAssignedId().getExtension());			remoteOrganization.setName(CoppaObjectFactory.getName(coppaOrganization.getName()));		remoteOrganization.setCity(CoppaObjectFactory.getCity(coppaOrganization.getPostalAddress()));		remoteOrganization.setCountry(CoppaObjectFactory.getCountry(coppaOrganization.getPostalAddress()));		remoteOrganization.setExternalId(coppaOrganization.getIdentifier().getExtension());		return remoteOrganization;	}		public Object getRemoteEntityByUniqueId(String externalId) {		logger.info("Entering OrganizationResolver.getRemoteEntityByUniqueId()");		System.out.println("Entering OrganizationResolver.getRemoteEntityByUniqueId()");				gov.nih.nci.coppa.po.Organization coppaOrganization = null;		gov.nih.nci.coppa.po.IdentifiedOrganization identifiedOrganization = null;				// using external id (coppa id ) 		II ii = CoppaObjectFactory.getIISearchCriteria(externalId);		try {			String iiXml = CoppaObjectFactory.getCoppaIIXml(ii);			Metadata mData = new Metadata(OperationNameEnum.getById.getName(), "externalId", ServiceTypeEnum.ORGANIZATION.getName());			String resultXml = broadcastCOPPA(iiXml, mData);					//System.out.println(resultXml);			List<String> results = XMLUtil.getObjectsFromCoppaResponse(resultXml);			if (results.size()>0) {				coppaOrganization = CoppaObjectFactory.getCoppaOrganization(results.get(0));				II ident =  coppaOrganization.getIdentifier();				String playerId = CoppaObjectFactory.getCoppaIIXml(ident);				// refactored to get by getByPlayerIds				mData = new Metadata("getByPlayerIds", "externalId", ServiceTypeEnum.IDENTIFIED_ORGANIZATION.getName());				String identifiedOrgsResultXml = broadcastCOPPA(playerId,mData);				List<String> identifiedOrgsResult = XMLUtil.getObjectsFromCoppaResponse(identifiedOrgsResultXml);				if (identifiedOrgsResult.size()>0) {					identifiedOrganization = CoppaObjectFactory.getCoppaIdentfiedOrganization(identifiedOrgsResult.get(0));								}				if (identifiedOrganization == null) {					return null;				}			}		} catch (Exception e) {			// TODO Auto-generated catch block			e.printStackTrace();		}		logger.info("Exiting OrganizationResolver.getRemoteEntityByUniqueId()");		System.out.println("Exiting OrganizationResolver.getRemoteEntityByUniqueId()");		return populateRemoteOrganization(coppaOrganization,identifiedOrganization);	}	private List<Object> getRemoteOrganizationByNciIdentifier(String nciInstituteCode){			// get by nci-id			List<Object> remoteOrganizations = new ArrayList<Object>();			IdentifiedOrganization identifiedOrganizationSearchCriteria = CoppaObjectFactory.getCoppaIdentfiedOrganizationSearchCriteriaOnCTEPId(nciInstituteCode);			String payload = CoppaObjectFactory.getCoppaIdentfiedOrganization(identifiedOrganizationSearchCriteria);			Metadata mData = new Metadata(OperationNameEnum.search.getName(), "externalId", ServiceTypeEnum.IDENTIFIED_ORGANIZATION.getName());			String results =broadcastCOPPA(payload, mData);//			List<String> resultObjects = XMLUtil.getObjectsFromCoppaResponse(results);			for (String resultObj:resultObjects) {				IdentifiedOrganization coppaIdOrganization = CoppaObjectFactory.getCoppaIdentfiedOrganization(resultObj);				II organizationIdentifier = coppaIdOrganization.getPlayerIdentifier();				String iiXml = CoppaObjectFactory.getCoppaIIXml(organizationIdentifier);				//Get Organization based on player id of above.				mData = new Metadata(OperationNameEnum.getById.getName(), "externalId", ServiceTypeEnum.ORGANIZATION.getName());				String organizationResults = broadcastCOPPA(iiXml, mData);//				List<String> organizationResultObjects = XMLUtil.getObjectsFromCoppaResponse(organizationResults);				for (String organizationResultObject:organizationResultObjects) {					gov.nih.nci.coppa.po.Organization coppaOrganizationResult = CoppaObjectFactory.getCoppaOrganization(organizationResultObject);					Organization remoteOrganization = populateRemoteOrganization(coppaOrganizationResult,coppaIdOrganization);					remoteOrganizations.add(remoteOrganization);				}			}			return remoteOrganizations;	}		private List<Object> getRemoteOrganizationByNameAndMore(Organization remoteOrgExample){		List<Object> remoteOrganizations = new ArrayList<Object>();		// get by name		String payLoad = 			CoppaObjectFactory.getCoppaOrganizationXml(remoteOrgExample.getName(), null, remoteOrgExample.getCity(),					null, null, remoteOrgExample.getCountry());			//System.out.println(payLoad);				// call caXchange and get results ... 		Metadata mData = new Metadata(OperationNameEnum.search.getName(), "externalId", ServiceTypeEnum.ORGANIZATION.getName());		String resultXml =broadcastCOPPA(payLoad, mData);//		//System.out.println(resultXml);				List<String> results = XMLUtil.getObjectsFromCoppaResponse(resultXml);		List<gov.nih.nci.coppa.po.Organization> coppaOrganizations = new ArrayList<gov.nih.nci.coppa.po.Organization>();		List<String> searchStrs = new ArrayList<String>();		for (String result:results) {			gov.nih.nci.coppa.po.Organization coppaOrganization = CoppaObjectFactory.getCoppaOrganization(result);			coppaOrganizations.add(coppaOrganization);			II ident =  coppaOrganization.getIdentifier();			String xml1 = CoppaObjectFactory.getCoppaIIXml(ident);			searchStrs.add(xml1);		}		OrganizationResolverContext context = OrganizationResolverContext.getSubmissionContext();		//		 refactored to get by getByPlayerIds		mData = new Metadata("getByPlayerIds", "externalId", ServiceTypeEnum.IDENTIFIED_ORGANIZATION.getName());		String identifiedOrgsResultXml = broadcastCOPPA(searchStrs,mData);				List<String> identifiedOrgsResults = XMLUtil.getObjectsFromCoppaResponse(identifiedOrgsResultXml);		List<gov.nih.nci.coppa.po.IdentifiedOrganization> coppaIdentifiedOrganizations = new ArrayList<gov.nih.nci.coppa.po.IdentifiedOrganization>();		for (String identifiedOrgsResult:identifiedOrgsResults) {			gov.nih.nci.coppa.po.IdentifiedOrganization coppaIdentifiedOrganization = CoppaObjectFactory.getCoppaIdentfiedOrganization(identifiedOrgsResult);			coppaIdentifiedOrganizations.add(coppaIdentifiedOrganization);		}				cacheSearchResults(context, coppaOrganizations, coppaIdentifiedOrganizations);		for (gov.nih.nci.coppa.po.Organization corg:coppaOrganizations) {			IdentifiedOrganization identifiedOrganization = context.identifiedOrganizationMap.get(corg.getIdentifier().getExtension());			if (identifiedOrganization != null) {				Organization remoteOrganization = populateRemoteOrganization(corg,identifiedOrganization);					remoteOrganizations.add(remoteOrganization);			}		}		return remoteOrganizations;}		@SuppressWarnings("unchecked")	public List<Object> find(Object example) {			logger.info("Entering OrganizationResolver.find()");		System.out.println("Entering OrganizationResolver.find()");		List<Object> remoteOrganizations = new ArrayList<Object>();		Organization remoteOrgExample = (RemoteOrganization)example;		//get by nci-id		if (remoteOrgExample.getNciInstituteCode() != null) {			remoteOrganizations = getRemoteOrganizationByNciIdentifier(remoteOrgExample.getNciInstituteCode());			return remoteOrganizations;		}		// get by everything else 		remoteOrganizations = getRemoteOrganizationByNameAndMore(remoteOrgExample);		logger.info("Entering OrganizationResolver.find()");		System.out.println("Entering OrganizationResolver.find()");		return remoteOrganizations;	}	/*	 * StringBuffer fileData = new StringBuffer();		try {				        BufferedReader reader = new BufferedReader(	                new FileReader("/Users/sakkala/tech/coppa/caX.xml"));	        char[] buf = new char[1024];	        int numRead=0;	        while((numRead=reader.read(buf)) != -1){	            String readData = String.valueOf(buf, 0, numRead);	            fileData.append(readData);	            buf = new char[1024];	        }	        reader.close();	        	       // System.out.println(fileData.toString());		} catch (Exception e) {			// TODO Auto-generated catch block			e.printStackTrace();		}	 *//*	public void setCoppaMessageBroadcastService(			MessageBroadcastService coppaMessageBroadcastService) {		this.coppaMessageBroadcastService = coppaMessageBroadcastService;	}*/	//	public String broadcastCOPPA(String message,Metadata metaData) throws gov.nih.nci.cabig.caaers.esb.client.BroadcastException {    	//        String result = null;//        try {//        	CaXchangeMessageBroadcasterImpl broadCaster = new CaXchangeMessageBroadcasterImpl();//        //	System.out.println("ca exchage URL + " + configuration.get(Configuration.CAEXCHANGE_URL));//            broadCaster.setCaXchangeURL("https://ncias-c278-v.nci.nih.gov:58445/wsrf-caxchange/services/cagrid/CaXchangeRequestProcessor");////        	result = broadCaster.broadcastCoppaMessage(message, metaData);//		} catch (edu.duke.cabig.c3pr.esb.BroadcastException e) {////            throw new gov.nih.nci.cabig.caaers.esb.client.BroadcastException(e);//		}//    	return result;//    }////	public String broadcastCOPPA(List<String> messages,Metadata metaData) throws gov.nih.nci.cabig.caaers.esb.client.BroadcastException {    	//        String result = null;//        try {//        	CaXchangeMessageBroadcasterImpl broadCaster = new CaXchangeMessageBroadcasterImpl();//        //	System.out.println("ca exchage URL + " + configuration.get(Configuration.CAEXCHANGE_URL));//            broadCaster.setCaXchangeURL("https://ncias-c278-v.nci.nih.gov:58445/wsrf-caxchange/services/cagrid/CaXchangeRequestProcessor");////        	result = broadCaster.broadcastCoppaMessage(messages, metaData);//		} catch (edu.duke.cabig.c3pr.esb.BroadcastException e) {////            throw new gov.nih.nci.cabig.caaers.esb.client.BroadcastException(e);//		}//    	return result;//    }	public Object saveOrUpdate(Object arg0) {		// TODO Auto-generated method stub		return null;	}		private void cacheSearchResults(OrganizationResolverContext context,List<gov.nih.nci.coppa.po.Organization> coppaOrganizations,List<IdentifiedOrganization> identifiedOrgs) {		/*		Map<String,gov.nih.nci.coppa.po.Organization> orgMap = new HashMap<String,gov.nih.nci.coppa.po.Organization>();		for (gov.nih.nci.coppa.po.Organization corg:coppaOrganizations) {			orgMap.put(corg.getIdentifier().getExtension(), corg);		}		context.organizationMap=orgMap;		*/		Map<String,gov.nih.nci.coppa.po.IdentifiedOrganization> idOrgMap = new HashMap<String,gov.nih.nci.coppa.po.IdentifiedOrganization>();		for (gov.nih.nci.coppa.po.IdentifiedOrganization identifiedOrg:identifiedOrgs) {			idOrgMap.put(identifiedOrg.getPlayerIdentifier().getExtension(), identifiedOrg);		}			context.identifiedOrganizationMap = idOrgMap;					}	/**	 * This class maintains the  context, across various  methods.	 * 	 *	 */	public static class OrganizationResolverContext {		//public Map<String,gov.nih.nci.coppa.po.Organization> organizationMap;		public Map<String,gov.nih.nci.coppa.po.IdentifiedOrganization> identifiedOrganizationMap; 				private OrganizationResolverContext() {		}				public static OrganizationResolverContext getSubmissionContext(){			return new OrganizationResolverContext();		}	}}