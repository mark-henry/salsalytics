global with sharing class GraphAdder
{
    Graph__c newGraph;
    ApexPages.StandardController stdController;
    
    global GraphAdder() {
    
    }
 
    global GraphAdder.GraphAdder(ApexPages.StandardController controller) {
        stdController = controller;
    }
 
    global void init() {
        newGraph = new Graph__c();
    }
    
    global void setQuery() {
        String filterClause = GraphEditor.getFilterClause(newGraph);
        String firstHalf = '';
        String secondHalf = ''; 
        String fullLengthQuery = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c WHERE Key__c = \'' + newGraph.Attribute__c + '\'';
        if (filterClause != null && filterClause != '') {
            fullLengthQuery += ' AND parentevent__c in (select ID from Event__c WHERE ' + filterClause + ')';
        }
        fullLengthQuery += ' GROUP BY Value__c';
        
        if (fullLengthQuery.length() > 255) {
           firstHalf = fullLengthQuery.subString(0,255);
           secondHalf = fullLengthQuery.subString(255,fullLengthQuery.length());
           newGraph.Query__c = firstHalf;
           newGraph.Query2__c = secondHalf;
        } 
        else {
           newGraph.Query__c = fullLengthQuery;
           newGraph.Query2__c = '';
        }   
        upsert(newGraph);
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
      return GraphEditor.getAttributes();
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
       List<Event__c> apps = [select AppName__c from Event__c];
       List<SelectOption> listOptions = new List<SelectOption>(options);
       
       for(Event__c app : apps)
       {
          if (app.AppName__c != null) 
              options.add(new SelectOption(app.AppName__c, app.AppName__c));
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
    
   @IsTest(seeAllData=true)
   static public void testGetEventNames() 
   {
       GraphAdder adder = new GraphAdder(null);
       List<SelectOption> app = adder.getEventNames(); 
       Integer count = [select count() from Event__c];
       System.assert(count > 0);   
       System.assert(app.size() > 0);
   }   
   
   @IsTest
   static public void testLongQuery() 
   {
       GraphAdder adder = new GraphAdder();
       Graph__c newGraph = adder.getGraph(); 
       newGraph.query__c = 'Really really really really really really really long query where ' +
          'there are a lot of lines and stuff and this query is too long and now the line is really long' +
          ' and so the query will have to be split!';
       adder.setQuery();
       newGraph.name = 'Long Graph Name';
       adder.save();
   }
   
      @IsTest(seeAllData=true)
   static public void testGetApps() 
   {
       GraphAdder editor = new GraphAdder();
       List<SelectOption> app = editor.getApps(); 
       Integer count = [select count() from EventAttribute__c where key__c <> ''];
       System.assert(count > 0);   
       System.assert(app.size() > 0);
   }
}