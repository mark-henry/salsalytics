global with sharing class DashboardController {
     Component.Apex.Chart chart;
     String tabChartString;
     
     public String getTabsForApps() {
        tabChartString = '<ul class="nav nav-tabs"><li class="nav-header">Applications</li><li></li>';
        Integer tabCount = 1;
        Set<String> appNames =  new Set<String>();

        
        // GET ATTRIBUTES SOQL
        List<EventAttribute__c> attributes = [select key__c, value__c from eventAttribute__c where key__c = 'AppName'];
        
        //Gets only distinct App names
        appNames.add('all');
        for(EventAttribute__c appName : attributes){
            appNames.add(appName.value__c);    
        }
        
        //Sets up the tabs for the each app
        for(String appName : appNames){
            if(appName.equals('all')) {
                tabChartString+= '<li><a href="#pane' + tabCount+ '" data-toggle="tab">Miscellaneous</a></li>';
            }
            else {
                tabChartString+= '<li><a href="#pane' + tabCount+ '" data-toggle="tab">' + appName + '</a></li>';
            }
            tabCount++;     
    

        }
        tabChartString+='</ul><div class="tab-content" style="background:#E6E6E6">';
        
        //Sets up the content for each tab pane
        tabCount = 1;
        for(String appName : appNames){      
            tabChartString+='<div id="pane' + tabCount + '" class="tab-pane">';
            tabChartString+= getSomeChart(appName);
            tabChartString+='</div>';
            tabCount++;
        
        }
        tabChartString+='</div>';
               
         
        return tabChartString;
     }
     
     public String getSomeChart(String appName) {
        String chartString = '';
        Integer count = 0;
        chartString += '<div class ="row">';
        
   /*     List<Graph__c> oldQueries = getQueries();
        for (Graph__c g : oldQueries) {
            GraphEditor.getStaticQuery(g); //update queries?? 
        } */
        
        List<Graph__c> queries = getQueries(appName);
        for (Graph__c g : queries) {
            // Create a graph based on this query
            
            String graphName = g.Name;
            String chartType = g.chartType__c;
            String graphId = g.id;
            
            chartString += '<div class="span5"><input type = "button" class = "btn btn-danger" name = "' + graphID + '"value = "Delete" onclick = "deleteGraph(\'' +
                 graphId + '\')"/>';
            chartString += '<input type = "button" class = "btn btn-info" name = "' + graphID + '"value = "Edit" onclick = "editRedirect(\'' + graphId + '\')"/>';     
                 
            // Link to its SingleGraph page
            chartString += '<a href=singlegraph?graphid=' + graphId + '>';
            
            // Create chart div
            chartString += '<div id="chart" class="chart" key="' + g.attribute__c + '"';
            chartString += 'graphName="' + graphName + '"';
            
            //modify the query slices. 
            
            // Get the slices of the pie graph
            String concat = g.Query2__c;
            if (g.Query2__c == null) {
                concat = '';
            }
            String concatQuery = g.Query__c + concat;
            List<AggregateResult> slices = Database.query(concatQuery);
            
            
            // Get the (label, count) pairs by some means
            //  and iterate over them
            for (Integer i = 0; i < slices.size(); i++) {
                chartString += 'field' + i + '="' + slices[i].get('salsaforce__value__c') + '" ';
                chartString += 'value' + i + '='  + slices[i].get('expr0') + ' ';
            }
            
            chartString += 'chartType="' + chartType + '"> </div> </a> </div>';
            System.debug(chartType);
            count++; 
            
        }
        chartString += '</div>';
       
        return chartString;
    }
    
    private List<Graph__c> getQueries(String appName) {
        String query = 'SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, salsaforce__Query2__c, Name, ' + 
            'salsaforce__ChartType__c, salsaforce__Selected__c, salsaforce__AppName__c, ' +
            'salsaforce__StartDate__c, salsaforce__EndDate__c, salsaforce__eventNameFilter__c ' +
            'FROM salsaforce__Graph__c where salsaforce__AppName__c = \'' + appName + '\'';
        return Database.query(query); 
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
         newGraph.AppName__c = 'all';
         adder.save();
         
         //test getSomeChart
         DashboardController dashCon = new DashboardController();
         chartString = dashCon.getSomeChart('all');
         
         //get graph object
         queries = ([SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, Name, 
         salsaforce__ChartType__c, salsaforce__Selected__c FROM salsaforce__Graph__c LIMIT 500]);
         
         system.debug('size after adding 1: ' + queries.size());
         System.assert(queries.size() == 1);
         graphID = queries.get(0).id;
         
//         correctString = '<table border = "0" class = "table"><tr>' + '<td><input type = "button" class = "btn btn-danger" name = "';
//         correctString += graphID + '"value = "Delete" onclick = "deleteGraph(\'' + graphId + '\')"/>';
//         correctString += '<input type = "button" class = "btn btn-info" name = "' + graphID + '"value = "Edit" onclick = "editRedirect(\'' + graphId + '\')"/>'; 
//         correctString += '<a href=singlegraph?graphid=' + graphId + '>';
//         correctString += '<div id="chart" class="chart" key="' + queries.get(0).attribute__c + '"';
//         correctString += 'graphName="' + queries.get(0).name + '"';
         
         //correctString = '<div class ="row"></div>';
         correctString = '<div class ="row"><div class="span5">';
         correctString+= '<input type = "button" class = "btn btn-danger" name = "' + graphID + '"value = "Delete" onclick = "deleteGraph(\''+graphID+'\')"/>';
         correctString+= '<input type = "button" class = "btn btn-info" name = "'+graphID+'"value = "Edit" onclick = "editRedirect(\''+graphID+'\')"/>';
         correctString+= '<a href=singlegraph?graphid='+graphID+'><div id="chart" class="chart" key="user"graphName="'+queries.get(0).name;
         correctString+= '"field0="Brandon" value0=1 field1="jdoe" value1=2 chartType="null"> </div> </a> </div></div>';

         
         List<AggregateResult> slices = Database.query(queries.get(0).Query__c);
            
            
         //for (Integer i = 0; i < slices.size(); i++) {
         //    correctString += 'field' + i + '="' + slices[i].get('salsaforce__value__c') + '" ';
         //    correctString += 'value' + i + '='  + slices[i].get('expr0') + ' ';
        //}
            
        //correctString += 'chartType="' + queries.get(0).chartType__c + '"> ';
        //correctString += '</div> </a> </td><td/></tr></table></a>';
         
        System.debug('chartString: ' + chartString);
        System.debug('correctString: ' + correctString);        
        
        System.assertEquals(correctString, chartString);
     }
     
     @IsTest
     static void testTabs() {
         DashboardController controller = new DashboardController();
         String expected = '<ul class="nav nav-tabs"><li class="nav-header">Applications</li>';
         expected+= '<li></li><li><a href="#pane1" data-toggle="tab">Miscellaneous</a></li></ul>';
         expected+= '<div class="tab-content" style="background:#E6E6E6"><div id="pane1" class="tab-pane">';
         expected+= '<div class ="row"></div></div></div>';
         String result = controller.getTabsForApps();
         
         System.assertEquals(expected, result); 
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
         chartString = dashCon.getSomeChart('all');
         
         //get all graphs
         queries = ([SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, Name, 
            salsaforce__ChartType__c, salsaforce__Selected__c FROM salsaforce__Graph__c LIMIT 500]);
         system.debug('num graphs after: ' + queries.size());
       
         System.assert(queries.size() == 30);    
    }
    
    global PageReference newGraph() {
        return Page.freshsalsanewgraphpage;
    }
    //Adding a line in here for testing purposes of the awesome build system!  Shhhhhh, don't tell Martin.  
}