package gov.nih.nci.cabig.caaers2adeers;

/**
 * @author Biju Joseph
 */
public class ToCaaersSynchronousRouteBuilder {


    private String xslBase = "xslt/caaers/response/";

    private Caaers2AdeersRouteBuilder routeBuilder;

    public ToCaaersSynchronousRouteBuilder(Caaers2AdeersRouteBuilder routeBuilder) {
        this.routeBuilder = routeBuilder;
    }

    private String xpathPredicate(String entity, String operation){

        return new StringBuilder("/payload/response/operation/data ")
                .append(" and ")
                .append("not(/payload/response/operation/errors)")
                .append(" and ")
                .append("/payload/response/operation/@name = '").append(operation).append("' ")
                .append("and ")
                .append("/payload/response/entity = '").append(entity).append("'").toString();
    }

    public void configure(){
        //content based router
        routeBuilder.from("direct:caAERSSynchronousRequestSink")
                .to("log:caaers.caaers-sync-request?showHeaders=true")
                .choice()
                .when().xpath(xpathPredicate("study", "searchStudy")).to("direct:caaers-study-search-sync")
                .otherwise().to("direct:morgue");

        //caAERS - searchStudy
        this.routeBuilder.configureTransformationRoute("direct:caaers-study-search-sync", xslBase + "study_search_sync.xsl");
    }
}