/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.dao;

import edu.nwu.bioinformatics.commons.CollectionUtils;
import gov.nih.nci.cabig.caaers.dao.query.AbstractQuery;
import gov.nih.nci.cabig.caaers.dao.query.HQLQuery;
import gov.nih.nci.cabig.caaers.dao.query.NativeSQLQuery;
import gov.nih.nci.cabig.caaers.dao.query.ajax.AbstractAjaxableDomainObjectQuery;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.Participant;
import gov.nih.nci.cabig.ctms.dao.AbstractDomainObjectDao;
import gov.nih.nci.cabig.ctms.domain.DomainObject;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Rhett Sutphin
 */
@Transactional(readOnly = true)
public abstract class CaaersDao<T extends DomainObject> extends AbstractDomainObjectDao<T> {

    protected static final int DEFAULT_MAX_RESULTS_SIZE = 0;

    /**
     * A variation of {@link #findBySubname} that does not allow for extra conditions
     */
    protected List<T> findBySubname(String[] subnames, List<String> substringMatchProperties, List<String> exactMatchProperties) {
        return findBySubname(subnames, null, null, substringMatchProperties, exactMatchProperties);
    }
    
    /**
     * A query builder for use by subclass DAOs. It makes it easy to match a fragment of a name or
     * identifier against multiple properties. This is intended for use in implementing
     * user-friendly dynamic searches; e.g., autocompleters.
     * 
     * @param subnames
     *                the name fragments to search on
     * @param extraConditions
     *                custom HQL conditions with which to constrain the fragment matches
     * @param extraParameters
     *                parameters for the custom conditions
     * @param substringMatchProperties
     *                a list of properties of the implementing object which should be matched as
     *                case-insensitive substrings
     * @param exactMatchProperties
     *                a list of properties which should be matched as case-insensitive full strings
     * @return a list of matching domain object instances
     */
    @SuppressWarnings("unchecked")
    protected List<T> findBySubname(String[] subnames, String extraConditions, List<Object> extraParameters, List<String> substringMatchProperties, List<String> exactMatchProperties) {
        if (subnames == null || subnames.length == 0) {
            return Collections.emptyList();
        }

        StringBuilder query = new StringBuilder("from ").append(domainClass().getName()).append(" o where ");
        if (extraConditions != null && extraConditions.length() > 0) {
            query.append(extraConditions).append(" and ");
        }
        
        List<Object> params = new LinkedList<Object>();
        if (extraParameters != null) {
            params.addAll(extraParameters);
        }

        for (int i = 0; i < subnames.length; i++) {
            buildSubnameQuery(subnames[i], query, params, substringMatchProperties, exactMatchProperties, true);
            if (i < subnames.length - 1) {
                query.append(" and ");
            }
        }

        if(substringMatchProperties != null && !substringMatchProperties.isEmpty()){
        	query.append(" order by o." + substringMatchProperties.get(0));
        }
        getHibernateTemplate().setMaxResults(75);
        log.debug("query::" + query.toString());
        
        List<T> result = getHibernateTemplate().find(query.toString(), params.toArray());
        getHibernateTemplate().setMaxResults(DEFAULT_MAX_RESULTS_SIZE);
        
        return result;
    }

    /**
     * Will build the sub name query. 
     * @param subnames   the name fragments to search on
     * @param extraConditions custom HQL conditions with which to constrain the fragment matches
     * @param extraParameters  parameters for the custom conditions
     * @param joins  the join conditions to use
     * @param params the constraints parameters. 
     * @param substringMatchProperties  a list of properties of the implementing object which should be matched as
     *                case-insensitive substrings
     * @param exactMatchProperties  a list of properties which should be matched as case-insensitive full strings
     * @return a String representation of the Query
     */
    protected String buildSubnameQuery( String[] subnames, String extraConditions, List<Object> extraParameters,String joins,List<Object> params,List<String> substringMatchProperties, List<String> exactMatchProperties){
    	 

          StringBuilder query = new StringBuilder(" select distinct o from ").append(domainClass().getName()).append(" o ");
          if (joins != null) {
              query.append(joins).append(" where ");
          }
          
          if (extraConditions != null) {
              query.append(extraConditions).append(" and ");
          }

          
          if (extraParameters != null) {
              params.addAll(extraParameters);
          }

          for (int i = 0; i < subnames.length; i++) {
              buildSubnameQuery(subnames[i], query, params, substringMatchProperties, exactMatchProperties, false);
              if (i < subnames.length - 1) {
                  query.append(" and ");
              }
          }

          log.debug("query::" + query.toString());
          return query.toString();
    }

    /**
     * Will find the objects, matching the sub names. 
     * @param subnames   the name fragments to search on
     * @param extraConditions custom HQL conditions with which to constrain the fragment matches
     * @param extraParameters  parameters for the custom conditions
     * @param joins  the join conditions to use
     * @param substringMatchProperties  a list of properties of the implementing object which should be matched as
     *                case-insensitive substrings
     * @param exactMatchProperties  a list of properties which should be matched as case-insensitive full strings
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<T> findBySubname(String[] subnames, String extraConditions, List<Object> extraParameters, List<String> substringMatchProperties, List<String> exactMatchProperties, String joins) {
    	 if (subnames == null || subnames.length == 0) {
             return Collections.emptyList();
         }
    	 
        getHibernateTemplate().setMaxResults(75);
        List<Object> params = new LinkedList<Object>();
        List<T> result =  getHibernateTemplate().find(buildSubnameQuery(subnames, extraConditions, extraParameters,
        		joins, params, substringMatchProperties, exactMatchProperties), params.toArray());
        getHibernateTemplate().setMaxResults(DEFAULT_MAX_RESULTS_SIZE);
        return result;
    }

    /**
     * Will build the sub name query.
     * @param subname   the name fragments to search on
     * @param query the base query to use
     * @param params the constraints parameters.
     * @param substringMatchProperties  a list of properties of the implementing object which should be matched as
     *                case-insensitive substrings
     * @param exactMatchProperties  a list of properties which should be matched as case-insensitive full strings
     * @param includeObjectReference - if true, the referenced objects are prefetched. 
     */
    private void buildSubnameQuery(String subname, StringBuilder query, List<Object> params,
                    List<String> substringMatchProperties, List<String> exactMatchProperties,
                    boolean includeObjectReference) {
        query.append('(');
        if (hasAny(substringMatchProperties)) {
            for (Iterator<String> it = substringMatchProperties.iterator(); it.hasNext();) {
                String prop = it.next();
                if (includeObjectReference) {
                    query.append("LOWER(o.").append(prop).append(") LIKE ?");
                } else {
                    query.append("LOWER(").append(prop).append(") LIKE ?");
                }

                params.add('%' + subname.toLowerCase() + '%');
                if (it.hasNext()) {
                    query.append(" or ");
                }
            }
            if (hasAny(exactMatchProperties)) {
                query.append(" or ");
            }
        }
        if (hasAny(exactMatchProperties)) {
            for (Iterator<String> it = exactMatchProperties.iterator(); it.hasNext();) {
                String prop = it.next();
                if (includeObjectReference) {
                    query.append("LOWER(o.").append(prop).append(") LIKE ?");
                } else {
                    query.append("LOWER(").append(prop).append(") LIKE ?");
                }
                params.add(subname.toLowerCase());
                if (it.hasNext()) {
                    query.append(" or ");
                }
            }
        }
        query.append(')');
    }

    public List<T> findByIds(List<Integer> ids){
        HQLQuery query = new HQLQuery("select o from " + domainClass().getName() + " o where id in (:ids)");
        query.setParameterList("ids", ids);
        return (List<T>) search(query);
    }

    /**
     * Will check if the input list is empty or not
     * @param properties - The list to be verified for emptyness
     * @return   true if empty, false otherwise. 
     */
    private boolean hasAny(List<String> properties) {
        return properties != null && properties.size() > 0;
    }

    /**
     * Searches based on an example object. For example, if you want to search for a Participant
     * based on last name:
     * <ul>
     * <li><code>Participant sample = new Participant();</code></li>
     * <li><code>sample.setLastName("last_namee");</code></li>
     * <li><code>participantDao.searchByExample(study)</code></li>
     * </ul>
     * 
     * @return list of matching instances based on your sample object
     */
    @SuppressWarnings("unchecked")
    public List<T> searchByExample(final T sample, final boolean inexactMatches) {
        return (List<T>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Example example = Example.create(sample).excludeZeroes();
                if (inexactMatches) {
                    example.ignoreCase().enableLike(MatchMode.ANYWHERE);
                }

                return session.createCriteria(domainClass()).add(example).list();
            }
        });
    }

    /**
     * Default search -- case insensitive substring matches
     * 
     * @see #searchByExample(gov.nih.nci.cabig.ctms.domain.DomainObject, boolean)
     */
    public List<T> searchByExample(T example) {
        return searchByExample(example, true);
    }
    
    /**
     * This method is to enable the case Insensitive matches.   
     * @param sample
     * @param inexactMatches
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<T> searchByExampleIgnoreCase(final T sample, final boolean inexactMatches) {
        return (List<T>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	 Example example = Example.create(sample).ignoreCase().excludeZeroes();
                 if (inexactMatches) {
                     example.ignoreCase().enableLike(MatchMode.ANYWHERE);
                 }

                 return session.createCriteria(domainClass()).add(example).list();
            }
        });
    }

    /**
     * Helper for DAOs which support domain classes with multiple identifiers.
     */
    @SuppressWarnings("unchecked")
    protected T findByIdentifier(final Identifier identifier) {
        return (T) CollectionUtils.firstElement(getHibernateTemplate().executeFind(
                        new HibernateCallback() {
                            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                                Criteria criteria = session.createCriteria(domainClass())
                                                .createCriteria("identifiers");

                                if (identifier.getType() != null) {
                                    criteria.add(Restrictions.eq("type", identifier.getType()));
                                }

                                if (identifier.getValue() != null) {
                                    criteria.add(Restrictions.eq("value", identifier.getValue()));
                                }

                                return criteria.list();
                            }
                        }));
    }
    
    /**
     * Helper for DAOs which support domain classes with multiple identifiers.
     */
    @SuppressWarnings("unchecked")
    protected T findByIgnoreCaseIdentifier(final Identifier identifier) {
        return (T) CollectionUtils.firstElement(getHibernateTemplate().executeFind(
                        new HibernateCallback() {
                            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                                Criteria criteria = session.createCriteria(domainClass())
                                                .createCriteria("identifiers");

                                if (identifier.getType() != null) {
                                    criteria.add(Restrictions.eq("type", identifier.getType()));
                                }

                                if (identifier.getValue() != null) {
                                    criteria.add(Restrictions.eq("value", identifier.getValue()).ignoreCase());
                                }

                                return criteria.list();
                            }
                        }));
    }

    /**
     * This method will reassociate the domain object to hibernate session. With a lock mode none.
     * 
     * @param o -
     *                the domain object instance that is to be reassociated
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void reassociate(T o) {
        // getHibernateTemplate().lock(o, LockMode.NONE);
        getHibernateTemplate().update(o);
    }

    /**
     * This will merge the changes, and returns a new persistant instance.
     * 
     * @param o -
     *                The domain object to be merged
     * @return a updated persistant instance
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = false)
    public T merge(T o) {
        return (T) getHibernateTemplate().merge(o);
    }
    
    /**
     * Will flush the hibernate session. 
     */
    @Transactional(readOnly = false)
    public void flush(){
    	getHibernateTemplate().flush();
    }

    /**
     * Detaches the passed-in instance (probably a domain object or a collection of domain objects)
     * from the sesison.
     * 
     * @param o
     */
    public void evict(T o) {
        getHibernateTemplate().evict(o);
    }

    /**
     * This will initialize a lazy collection, consisting of domain objects.
     * 
     * @param proxy
     */
    public void initialize(Collection<? extends DomainObject> proxy) {
        getHibernateTemplate().initialize(proxy);
    }
    
    /**
     * The default implementation will save the domain object
     * @param o
     */
    @Transactional(readOnly = false)
    public void save(T o){
    	getHibernateTemplate().saveOrUpdate(o);
    }
    @Transactional(readOnly = false)
    public void refresh(T o){
    	getHibernateTemplate().refresh(o);
    }
    
    /**
     * This will lock the object
     * @param o
     */
    @Transactional(readOnly = false)
    public void lock(T o){
    	getHibernateTemplate().lock(o, LockMode.NONE);
    }

    /**
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public Date stringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.parse(date);
    }


    /**
     * Will clear-off the content in the current Hibernate Session
     * @see org.hibernate.Session
     */
    @Transactional(readOnly = false)
    public void clearSession() {

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if(log.isDebugEnabled()){
            log.debug("Entity Count  before clear() : " + session.getStatistics().getEntityCount());
            log.debug("Collection Count before clear() : "
                    + session.getStatistics().getCollectionCount());  
        }
        
        session.clear();
        
        if(log.isDebugEnabled()){
            log.debug("Entity Count  after clear() : " + session.getStatistics().getEntityCount());
            log.debug("Collection Count after clear() : "
                    + session.getStatistics().getCollectionCount());
        }

    }


    /**
     * Will retrive all those domain objects that this DAO is respresenting from the DB. 
     * @param sortCriteria
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<T> findAll(String sortCriteria) {

        StringBuilder query = new StringBuilder(" select o from ").append(domainClass().getName()).append(" o ");
        if (sortCriteria != null && !org.apache.commons.lang.StringUtils.isEmpty(sortCriteria)) {
            query.append("order by " + sortCriteria);
        }

        log.debug("query::" + query.toString());
        getHibernateTemplate().setMaxResults(0);
        List<T> result =  getHibernateTemplate().find(query.toString());
        getHibernateTemplate().setMaxResults(DEFAULT_MAX_RESULTS_SIZE);
        return result;
    }

    /**
     * Will execute the query and return the results. 
     * @param query - An AbstractQuery instance. 
     * @return - List of objects that is fetched by the Query. 
     */
    @SuppressWarnings("unchecked")
	public List<?> search(final AbstractQuery query){
    	return search(query, null, null);
    }

    /**
     * A paginated query. 
     * @param query - The query to be executed. 
     * @param firstResult - The starting index
     * @param maxResults - The number of objects to be returned. 
     * @return - A list of objects returned by the query, matching the pagination criteria. 
     */
    @SuppressWarnings("unchecked")
	public List<?> search(final AbstractQuery query, final Integer firstResult, final Integer maxResults) {
	       String queryString = query.getQueryString();
	       if(log.isDebugEnabled()) log.debug("::: " + queryString);
	       return (List<?>) getHibernateTemplate().execute(new HibernateCallback() {

	            public Object doInHibernate(final Session session) throws HibernateException, SQLException {
	                if(query instanceof NativeSQLQuery){
	                    org.hibernate.SQLQuery nativeQuery = session.createSQLQuery(query.getQueryString());
	                    setResultSetBoundaries(nativeQuery, firstResult, maxResults);
	                    Map<String, Type> scalarMap = ((NativeSQLQuery) query).getScalarMap();
	                    for(String key : scalarMap.keySet()){
	                       nativeQuery.addScalar(key, scalarMap.get(key));
	                    }
	                    Map<String, Object> queryParameterMap = query.getParameterMap();
	                    for (String key : queryParameterMap.keySet()) {
	                        Object value = queryParameterMap.get(key);
	                        if (value instanceof Collection) {
	                            nativeQuery.setParameterList(key, (Collection) value);
	                        } else {
	                            nativeQuery.setParameter(key, value);
	                        }
	                    }
	                    return nativeQuery.list();
	                }else {
	                    org.hibernate.Query hibernateQuery = session.createQuery(query.getQueryString());
	                    setResultSetBoundaries(hibernateQuery, firstResult, maxResults);
	                    Map<String, Object> queryParameterMap = query.getParameterMap();
	                    for (String key : queryParameterMap.keySet()) {
	                        Object value = queryParameterMap.get(key);
	                        if (value instanceof Collection) {
	                            hibernateQuery.setParameterList(key, (Collection) value);
	                        } else {
	                            hibernateQuery.setParameter(key, value);
	                        }

	                    }
	                    return hibernateQuery.list();
	                }
	            }

	        });
	    }

    
    /**
     * @param query
     * @param firstResult
     * @param maxResults
     */
    private void setResultSetBoundaries(org.hibernate.Query query, Integer firstResult, Integer maxResults) {
    	if (firstResult!=null) query.setFirstResult(firstResult);
    	if (maxResults!=null) query.setMaxResults(maxResults);
	}

    /**
     * Will execute the query and return the results.
     * @param query - An AbstractQuery instance.
     * @return - List of objects that is fetched by the Query.
     */
	@SuppressWarnings("unchecked")
	public List<Object[]> search(final AbstractAjaxableDomainObjectQuery query) {
		String queryString = query.getQueryString();
        log.debug("::: " + queryString.toString());
       return (List<Object[]>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(final Session session) throws HibernateException, SQLException {
                org.hibernate.Query hibernateQuery = session.createQuery(query.getQueryString());
                Map<String, Object> queryParameterMap = query.getParameterMap();
                for (String key : queryParameterMap.keySet()) {
                    Object value = queryParameterMap.get(key);
                    if (value instanceof Collection) {
                    	hibernateQuery.setParameterList(key, (Collection) value);
                    } else {
                    	hibernateQuery.setParameter(key, value);
                    }

                }
                return hibernateQuery.list();
            }

        });

    }
	
}
