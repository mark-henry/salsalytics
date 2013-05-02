global with sharing class SingleGraphController
{
    public String getGraph()
    {
        String chartString = '';
        String graphID = ApexPages.currentPage().getParameters().get('graphid');
        Graph__c g = getGraph(graphID);
        
        // Create the string for graph g
        chartString += '<div id="chart" class="chart" ';
        
        chartString += 'graphName="' + g.name + '"';
        
        // Get the slices of the pie graph
            String concat = g.Query2__c;
            if (g.Query2__c == null) {
                concat = '';
            }
            String concatQuery = g.Query__c + concat;
            List<AggregateResult> slices = Database.query(concatQuery);
        
        // Iterate over the slices (label-count pairs)
        for (Integer i = 0; i < slices.size(); i++) {
            chartString += 'field' + i + '="' + slices[i].get('salsaforce__value__c') + '" ';
            chartString += 'value' + i + '='  + slices[i].get('expr0') + ' ';
        }
        
        chartString += 'chartType="' + g.chartType__c + '"> </div>';
        
        /*chartString += '<input type="button" name="' + graphID + '" value="Delete"/>';
        chartString += '<input type="button" name="' + graphID + '" value="Edit"/>';*/
        
        return chartString;
    }
    
    private Graph__c getGraph(String recordID)
    {
        String query = 'SELECT Name,ChartType__c,EndDate__c,Query__c,Query2__c,' +
            'StartDate__c FROM Graph__c WHERE Id=\'' + recordID + '\'';
        return Database.query(query);
    }
    
    @RemoteAction
    global static void deleteGraph(String graphId) {
        String query = 'SELECT Id from Graph__c where Id = \'' + graphId + '\' LIMIT 1';
        Graph__c existing = Database.query(query);
        delete existing;
    }    
}