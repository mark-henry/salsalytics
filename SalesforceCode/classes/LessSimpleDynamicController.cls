public class LessSimpleDynamicController {
    Component.Apex.Chart chart;
    Integer count;
    Integer numGraphs, current;
    String chartString;
        
     public String getSomeChart() {
        chartString = '';
        count = 0;
        
        List<Graph__c> queries = getQueries();
        for (Graph__c g : queries) {
            // Create a graph based on this query
            chartString += '<div id="chart" class="chart" ';
            
            String graphName = g.Name;
            String chartType = g.chartType__c;
            chartString += 'graphName="' + graphName + '"';
            
            // Get the slices of the pie graph
            List<AggregateResult> slices = Database.query(g.Query__c);
            
            // Get the (label, count) pairs by some means
            //  and iterate over them
            for (Integer i = 0; i < slices.size(); i++) {
                chartString += 'field' + i + '="' + slices[i].get('value__c') + '" ';
                chartString += 'value' + i + '='  + slices[i].get('expr0') + ' ';
            }
            
            //chartString += 'chartType="pie"></div>\n'; 
            chartString += 'chartType="' + chartType + '"> </div>';
            System.debug(chartType);
            count++; 
        }
       
        return chartString;
    }
    
    private List<Graph__c> getQueries() {
        return [SELECT Query__c, Name, ChartType__c FROM Graph__c LIMIT 500];
    }
    
    private List<EventAttribute__c> getAttributes(String query) {
        return Database.query(query);
    }


}