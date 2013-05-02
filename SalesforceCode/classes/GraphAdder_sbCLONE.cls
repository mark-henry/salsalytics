global with sharing class GraphAdder_sbCLONE
{
    Graph__c newGraph;
    ApexPages.StandardController stdController;
    
    global GraphAdder_sbCLONE() {
    
    }
 
     global GraphAdder_sbCLONE.GraphAdder_sbCLONE(ApexPages.StandardController controller) {
        stdController = controller; 
     }
 
    global void init() {
        newGraph = new Graph__c();
    }
    
    global void setQuery() {
        String filterClause = getFilterClause(newGraph);
        
        newGraph.Query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c WHERE Key__c = \'' + newGraph.Attribute__c + '\'';
        if (filterClause != null && filterClause != '') {
            newGraph.Query__c += ' AND parentevent__c in (select ID from Event__c WHERE ' + filterClause + ')';
        }
        newGraph.Query__c += ' GROUP BY Value__c';
    }
    
    private String getFilterClause(Graph__c graph)
    {
        List<String> filterItems = new List<String>();
        String filterClause = '';
        
        // Create items
        if (graph.appName__c != NULL && graph.appName__c != '' && graph.appName__c != 'all') {
            String subfilter = '('; 
            String subsubquery = 'select ID from Event__c where ID in (select ParentEvent__c from EventAttributes__c '
               + 'where key__c = \'AppName\' and value__c = \'' + graph.appName__c + '\')';
            List<SObject> idObjects = Database.query(subsubquery);
            for (Integer i = 0; i < idObjects.size(); i++) {
                subFilter += 'ID = \'' + (idObjects.get(0)).ID + '\'';
                if (i < idObjects.size() - 1) {
                   subFilter += ' OR ';
                } 
            }
            filterItems.add(subFilter);
        }    
        
        if (graph.startDate__c != null) {
            String startDateString;
            startDateString = '' + newGraph.startDate__c;
            startDateString = startDateString.replace(' ','T');
            startDateString = startDateString + 'Z';
            filterItems.add('Timestamp__c > ' + startDateString);
        }
        if (graph.endDate__c != null) {
            String endDateString;
            endDateString = '' + newGraph.endDate__c;
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
        if (newGraph == null) {
            init();
        }
        return newGraph;
    }
    
    global PageReference save()
    {
        setQuery();
        upsert(newGraph);
        return Page.salsalyticsdashboard;
    }
    
    global PageReference cancel()
    {
        return Page.salsalyticsdashboard;
    }    
    
    public List<SelectOption> getAttributes()
    {
      Set<SelectOption> options = new Set<SelectOption>();
      List<SelectOption> listOptions = new List<SelectOption>();
            
      // GET ATTRIBUTES SOQL
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

       for(EventAttribute__c attributeName : attributes)
       {
          options.add(new SelectOption(attributeName.value__c, attributeName.value__c));
       }       
       options.add(new SelectOption('all', 'All Apps'));
       for (SelectOption option : options) 
       {
           listOptions.add(option);
       }
       return listOptions;
    }
    
    @IsTest
    static public void testAddGraph()
    {
        GraphAdder adder = new GraphAdder();
        Graph__c newGraph = adder.getGraph();
        Integer graphCount = [select count() from salsaforce__Graph__c];
        System.assert(graphCount == 0);
        newGraph.Attribute__c = 'Level';
        adder.save();
        graphCount = [select count() from salsaforce__Graph__c];
        System.assert(graphCount == 1);
    }
    
    @IsTest
    static public void testCancelAdd() 
    {
        GraphAdder adder = new GraphAdder();
        Graph__c newGraph = adder.getGraph();
        Integer graphCount = [select count() from salsaforce__Graph__c];
        System.assert(graphCount == 0);
        newGraph.Attribute__c = 'Level';
        adder.cancel();
        graphCount = [select count() from salsaforce__Graph__c];
        System.assert(graphCount == 0);        
    }
    
    @IsTest
    static public void testSetQuery()
    {
        GraphAdder adder = new GraphAdder();
        Graph__c newGraph = adder.getGraph();
        newGraph.attribute__c = 'Level';
        adder.setQuery();
        System.assert('SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'Level\' group by Value__c' == newGraph.query__c);
    }
    
    @IsTest
    static public void testGetAttributes()
    {
        GraphAdder adder = new GraphAdder();
        List<SelectOption> attr = adder.getAttributes(); 
        System.assert(attr.size() == 0); 
        
    /*    EventAttribute__c ea = new EventAttribute__c();
        Event__c pe = new Event__c();
        ea.key__c = 'SomeKey';
        ea.value__c = 'SomeValue';
        ea.parentEvent__c = pe.id;
        upsert(ea);
        ea = new EventAttribute__c();
        ea.key__c = 'SomeKey2';
        ea.value__c = 'SomeValue2';
        ea.parentEvent__c = pe.id;
        upsert(ea);
        System.assert(attr.size() == 2); */
    }
    
    /*if (newGraph.startDate__c != null) {
                startDateString = '' + (newGraph.startDate__c);
                spaceIndex = startDateString.indexOf(' ');
                startDateString = startDateString.replace(' ','T');
                startDateString = startDateString + 'Z';
            }
            if (newGraph.endDate__c != null) {
                endDateString = '' + (newGraph.endDate__c);
                spaceIndex = endDateString.indexOf(' ');
                endDateString = endDateString.replace(' ','T');
                endDateString = endDateString + 'Z';
            }
            */
    
    @IsTest
    static public void testValidGraph() {
        GraphAdder adder = new GraphAdder(null);
        Graph__c graph = adder.getGraph();
        
        graph.startDate__c = datetime.newInstance(2008, 12, 1);
        adder.setQuery();
        //System.assertEquals(graph.Query__c, 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + graph.Attribute__c + '\' AND parentevent__c in ' + '(select ID from Event__c where TimeStamp__c > ' + graph.startDate__c + ' AND TimeStamp__c < ' + graph.endDate__c + ') group by Value__c');
        //System.debug(graph.Query__c);
        //System.debug('SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + graph.Attribute__c + '\' AND parentevent__c in ' + '(select ID from Event__c where TimeStamp__c > ' + graph.startDate__c + ' AND TimeStamp__c < ' + graph.endDate__c + ') group by Value__c');
        
        graph.endDate__c = datetime.newInstance(2012, 12, 1);
        adder.setQuery();
        graph.startDate__c = null;
        adder.setQuery();
    }
}