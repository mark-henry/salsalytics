global with sharing class GraphEditor
{
    Graph__c existingGraph;
    ApexPages.StandardController stdController;
    
    global GraphEditor.GraphEditor(ApexPages.StandardController controller)
    {
        stdController = controller;
    }

    global void init() {
        String graphID = Apexpages.currentPage().getParameters().get('graphID');
        
        String query = 'SELECT Name, salsaforce__ChartType__c, salsaforce__attribute__c, ' +
            'salsaforce__query__c, salsaforce__appname__c, salsaforce__eventNameFilter__c, ' +
            'salsaforce__startdate__c, salsaforce__enddate__c from ' +
            'salsaforce__Graph__c where ID = \'' + graphID + '\' LIMIT 1';
        existingGraph = Database.query(query);
    }

    global void setQuery() {
        String filterClause = getFilterClause(existingGraph);
        String firstHalf = '';
        String secondHalf = ''; 
        String fullLengthQuery = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c WHERE Key__c = \'' + existingGraph.Attribute__c + '\'';
        if (filterClause != null && filterClause != '') {
            fullLengthQuery += ' AND parentevent__c in (select ID from Event__c WHERE ' + filterClause + ')';
        }
        fullLengthQuery += ' GROUP BY Value__c';
        
        if (fullLengthQuery.length() > 255) {
           firstHalf = fullLengthQuery.subString(0,255);
           secondHalf = fullLengthQuery.subString(255,fullLengthQuery.length());
           existingGraph.Query__c = firstHalf;
           existingGraph.Query2__c = secondHalf;
        } 
        else {
           existingGraph.Query__c = fullLengthQuery;
           existingGraph.Query2__c = '';
        }   
        upsert(existingGraph);
    }
    
    global static void getStaticQuery(Graph__c g) {
        String filterClause = getFilterClause(g);
        String firstHalf = '';
        String secondHalf = ''; 
        String fullLengthQuery = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c WHERE Key__c = \'' + g.Attribute__c + '\'';
        if (filterClause != null && filterClause != '') {
            fullLengthQuery += ' AND parentevent__c in (select ID from Event__c WHERE ' + filterClause + ')';
        }
        fullLengthQuery += ' GROUP BY Value__c';
        
        if (fullLengthQuery.length() > 255) {
           firstHalf = fullLengthQuery.subString(0,255);
           secondHalf = fullLengthQuery.subString(255,fullLengthQuery.length());
           g.Query__c = firstHalf;
           g.Query2__c = secondHalf;
           upsert(g);
        } 
        else {
           g.Query__c = fullLengthQuery;
           g.Query2__c = '';
           upsert(g);
        }   
    }
    
    public static String getFilterClause(Graph__c graph)
    {
        List<String> filterItems = new List<String>();
        String filterClause = '';
        if (graph.appName__c != NULL && graph.appName__c != '' && graph.appName__c != 'all') {
           filterItems.add('AppName__c = \'' + graph.appName__c + '\'');
        }        
        // Create items
        if (graph.startDate__c != null) {
            String startDateString;
            startDateString = '' + graph.startDate__c;
            startDateString = startDateString.replace(' ','T');
            startDateString = startDateString + 'Z';
            filterItems.add('Timestamp__c > ' + startDateString);
        }
        if (graph.endDate__c != null) {
            String endDateString;
            endDateString = '' + graph.endDate__c;
            endDateString = endDateString.replace(' ','T');
            endDateString = endDateString + 'Z';
            filterItems.add('Timestamp__c < ' + endDateString);
        }
        if (graph.eventNameFilter__c != null) {
            filterItems.add('Name=\'' + graph.eventNameFilter__c + '\'');
        }
        
        if (!filterItems.isEmpty()) {
            // Construct string
            filterClause = filterItems.get(0);
            filterItems.remove(0);
            for (String item : filterItems) {
                filterClause += ' AND ' + item;
            }
        }
        
        return filterClause;
    }
    
    global Graph__c getGraph()
    {
        init();
        return existingGraph;
    }
        
    global PageReference save()
    {
        setQuery();
        upsert(existingGraph);
        return Page.salsalyticsdashboard; //should never be called from mobile so this is OK
    }
    
    global PageReference cancel()
    {
        return Page.salsalyticsdashboard;
    }
    
    public static List<SelectOption> getAttributes()
    {
      Set<SelectOption> options = new Set<SelectOption>();
      List<SelectOption> listOptions = new List<SelectOption>();

       List<EventAttribute__c> attributes = [select key__c, value__c from eventAttribute__c];
       for(EventAttribute__c attributeName : attributes)
       {
          options.add(new SelectOption(attributeName.key__c, attributeName.key__c));
       }       
       
       for (SelectOption option : options) 
       {
           listOptions.add(option);
       }
       return listOptions;
    }
    
    public List<SelectOption> getEventNames()
    {
        Set<SelectOption> options = new Set<SelectOption>();
        
        for (Event__c event : [select name from event__c])
        {
            options.add(new SelectOption(event.name, event.name));
        }
        List<SelectOption> optionsList = new List<SelectOption>(options);
        optionsList.sort();
        optionsList.add(0, new SelectOption('', '<no filter>'));
        
        return optionsList;
    }

    public List<SelectOption> getApps()
    {
      Set<SelectOption> options = new Set<SelectOption>();
      List<SelectOption> listOptions = new List<SelectOption>();
            
      // GET ATTRIBUTES SOQL
       List<EventAttribute__c> attributes = [select key__c, value__c from eventAttribute__c where key__c = 'AppName'];

       options.add(new SelectOption('all', 'All Apps'));
       for(EventAttribute__c attributeName : attributes)
       {
          options.add(new SelectOption(attributeName.value__c, attributeName.value__c));
       }       
       
       for (SelectOption option : options) 
       {
           listOptions.add(option);
       }
       return listOptions;
    }

    // ///
    // TESTS
    
    @isTest(seeAllData=true)
    static void testGetGraph() 
    {
        GraphEditor ge = new GraphEditor(null);
        // GET FIRST GRAPH, EDIT NAME, ASSERT THAT IT HAS CHANGED
        String query = 'select id, name from salsaforce__Graph__c';
        List<Graph__c> testGraphs = Database.query(query);
        for (Graph__c testGraph : testGraphs) {
            ApexPages.currentPage().getParameters().put('graphID', testGraph.id);
            ge.getGraph();
            ge.existingGraph.name = 'Hi there';
            ge.save();  
            String resultQuery = 'select name from salsaforce__Graph__c where ID = \'' + testGraph.ID + '\' LIMIT 1';
            Graph__c resultGraph = Database.query(resultQuery);
            System.assertEquals(resultGraph.name, 'Hi there'); 
        }  
    }
    
    @isTest(seeAllData=true)
    static void testCancel() // check that nothing happens
    {
        GraphEditor ge = new GraphEditor(null);
        // GET FIRST GRAPH, EDIT NAME, ASSERT THAT IT HAS CHANGED
        String query = 'select id, name from salsaforce__Graph__c LIMIT 1';
        Graph__c testGraph = Database.query(query);
        ApexPages.currentPage().getParameters().put('graphID', testGraph.id);
        ge.init();
        ge.existingGraph.name = 'Hi there';
        ge.cancel();  
        String resultQuery = 'select name from salsaforce__Graph__c where ID = \'' + testGraph.ID + '\' LIMIT 1';
        Graph__c resultGraph = Database.query(resultQuery);
        System.assertEquals(resultGraph.name, testGraph.name);
    }
    
   @IsTest
   static public void testGetAttributes() //needs more
   {
       GraphEditor editor = new GraphEditor(null);
       List<SelectOption> attr = GraphEditor.getAttributes(); 
       Integer count = [select count() from EventAttribute__c];
       System.assert(count == 0);
   }    
   
   @IsTest(seeAllData=true)
   static public void testGetAtLeastOneAttribute() 
   {
       GraphEditor editor = new GraphEditor(null);
       List<SelectOption> attr = GraphEditor.getAttributes(); 
       Integer count = [select count() from EventAttribute__c];
       System.assert(count > 0);   
   }
   
   @IsTest(seeAllData=true)
   static public void testGetApps() 
   {
       GraphEditor editor = new GraphEditor(null);
       List<SelectOption> app = editor.getApps(); 
       Integer count = [select count() from EventAttribute__c where key__c <> ''];
       System.assert(count > 0);   
       System.assert(app.size() > 0);
   }
}