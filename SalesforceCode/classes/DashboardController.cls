global with sharing class DashboardController {
    Component.Apex.Chart chart;
    Integer count;
    Integer numGraphs, current;
    String chartString;
        
     public String getSomeChart() {
        chartString = '';
        count = 0;
        chartString += '<table border = "0" class = "table"><tr>';
        
        List<Graph__c> queries = getQueries();
        for (Graph__c g : queries) {
            // Create a graph based on this query
            
            String graphName = g.Name;
            String chartType = g.chartType__c;
            String graphId = g.id;
            
            chartString += '<td><input type = "button" class = "btn btn-danger" name = "' + graphID + '"value = "Delete" onclick = "deleteGraph(\'' +
                 graphId + '\')"/>';
            chartString += '<input type = "button" class = "btn btn-info" name = "' + graphID + '"value = "Edit" onclick = "editRedirect(\'' + graphId + '\')"/>';     
                 
            // Link to its SingleGraph page
            chartString += '<a href=singlegraph?graphid=' + graphId + '>';
            
            // Create chart div
            chartString += '<div id="chart" class="chart" key="' + g.attribute__c + '"';
            chartString += 'graphName="' + graphName + '"';
            
            // Get the slices of the pie graph
            List<AggregateResult> slices = Database.query(g.Query__c);
            
            
            // Get the (label, count) pairs by some means
            //  and iterate over them
            for (Integer i = 0; i < slices.size(); i++) {
                chartString += 'field' + i + '="' + slices[i].get('salsaforce__value__c') + '" ';
                chartString += 'value' + i + '='  + slices[i].get('expr0') + ' ';
            }
            
            chartString += 'chartType="' + chartType + '"> </div> </a> </td>';
            System.debug(chartType);
            count++; 
            if (Math.mod(count,3) == 0) {
                chartString += '</tr><tr>';
            }
        }
        for (Integer i = 0; i < Math.mod(count,3); i++) {
            chartString += '<td/>';
        }
        chartString += '</tr></table></a>';
       
        return chartString;
    }
    
    private List<Graph__c> getQueries() {
        return [SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, Name, 
            salsaforce__ChartType__c, salsaforce__Selected__c FROM salsaforce__Graph__c LIMIT 500];
    }
    
    private List<EventAttribute__c> getAttributes(String query) {
        return Database.query(query);
    }

    @RemoteAction
    global static void deleteGraph(String graphId) {
        String query = 'SELECT Id from Graph__c where Id = \'' + graphId + '\' LIMIT 1';
        Graph__c existing = Database.query(query);
        delete existing;
    }
    
    /**
     *  TESTS
     *
     */
     @IsTest(SeeAllData=true) static void basicGraph() {
         String correctString, chartString, graphID;
         List<Graph__c> queries;
         GraphAdder adder;
         Graph__c newGraph;
     
         //get all graphs
         queries = ([SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, Name, 
            salsaforce__ChartType__c, salsaforce__Selected__c FROM salsaforce__Graph__c LIMIT 500]);
            
         //remove all graphs
         system.debug('num graphs before: ' + queries.size());
         for (Graph__c g : queries) {
             DashboardController.deleteGraph(g.id);
         }
         
          //get all graphs
         queries = ([SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, Name, 
            salsaforce__ChartType__c, salsaforce__Selected__c FROM salsaforce__Graph__c LIMIT 500]);
         system.debug('num graphs after: ' + queries.size());
     
         // add an event to the empty database
         EventAdder.addEvent('a graph', 'user:Brandon');
         
         //create a graph
         adder = new GraphAdder();
         newGraph = adder.getGraph();         
         newGraph.Attribute__c = 'user';
         adder.save();
         
         //test getSomeChart
         DashboardController dashCon = new DashboardController();
         chartString = dashCon.getSomeChart();
         
         //get graph object
         queries = ([SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, Name, 
         salsaforce__ChartType__c, salsaforce__Selected__c FROM salsaforce__Graph__c LIMIT 500]);
         
         system.debug('size after adding 1: ' + queries.size());
         System.assert(queries.size() == 1);
         graphID = queries.get(0).id;
         
         correctString = '<table border = "0" class = "table"><tr>' + '<td><input type = "button" class = "btn btn-danger" name = "';
         correctString += graphID + '"value = "Delete" onclick = "deleteGraph(\'' + graphId + '\')"/>';
         correctString += '<input type = "button" class = "btn btn-info" name = "' + graphID + '"value = "Edit" onclick = "editRedirect(\'' + graphId + '\')"/>'; 
         correctString += '<a href=singlegraph?graphid=' + graphId + '>';
         correctString += '<div id="chart" class="chart" key="' + queries.get(0).attribute__c + '"';
         correctString += 'graphName="' + queries.get(0).name + '"';
         
         List<AggregateResult> slices = Database.query(queries.get(0).Query__c);
            
            
         for (Integer i = 0; i < slices.size(); i++) {
             correctString += 'field' + i + '="' + slices[i].get('salsaforce__value__c') + '" ';
             correctString += 'value' + i + '='  + slices[i].get('expr0') + ' ';
        }
            
        correctString += 'chartType="' + queries.get(0).chartType__c + '"> ';
        correctString += '</div> </a> </td><td/></tr></table></a>';
         
        System.debug('chartString: ' + chartString);
        System.debug('correctString: ' + correctString);        
        
        System.assertEquals(chartString, correctString);
     }
     
      @IsTest(SeeAllData=true) static void multipleGraphs() {
         String correctString, chartString, graphID;
         List<Graph__c> queries;
         GraphAdder adder;
         Graph__c newGraph;
     
         //get all graphs
         queries = ([SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, Name, 
            salsaforce__ChartType__c, salsaforce__Selected__c FROM salsaforce__Graph__c LIMIT 500]);
            
         //remove all graphs
         system.debug('num graphs before: ' + queries.size());
         for (Graph__c g : queries) {
             DashboardController.deleteGraph(g.id);
         }
         
          //get all graphs
         queries = ([SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, Name, 
            salsaforce__ChartType__c, salsaforce__Selected__c FROM salsaforce__Graph__c LIMIT 500]);
         
         // add an event to the empty database
         EventAdder.addEvent('a graph', 'user:Brandon');
         
         
         //SOQL System limit of ?
         for(Integer i = 0; i < 30; i++) {
             //create a graph
             adder = new GraphAdder();
             newGraph = adder.getGraph();         
             newGraph.Attribute__c = 'user';
             adder.save();
             
             System.debug('added graph ' + i);
         }
         
         //test getSomeChart
         DashboardController dashCon = new DashboardController();
         chartString = dashCon.getSomeChart();
         
         //get all graphs
         queries = ([SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, Name, 
            salsaforce__ChartType__c, salsaforce__Selected__c FROM salsaforce__Graph__c LIMIT 500]);
         system.debug('num graphs after: ' + queries.size());
       
         System.assert(queries.size() == 30);    
    }
}