global with sharing class GraphAdder
{
    Graph__c newGraph;
    ApexPages.StandardController stdController;
    
    global GraphAdder() {
    }
    global GraphAdder.GraphAdder(ApexPages.StandardController controller)
    {
        stdController = controller;
    }
    global void init() {
        newGraph = new Graph__c();
    }
    global void setQuery() {
        String startDateString, endDateString;
        Integer spaceIndex; 
        
        if (newGraph.startDate__c == null && newGraph.endDate__c == null) {
            newGraph.Query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + newGraph.Attribute__c + '\' group by Value__c';
        } 
        else { 
            if (newGraph.startDate__c != null) {
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
            
            if (newGraph.startDate__c != null && newGraph.endDate__c != null) { 
                newGraph.Query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + newGraph.Attribute__c + '\' AND parentevent__c in ' + 
                    '(select ID from Event__c where TimeStamp__c > ' + startDateString + ' AND TimeStamp__c < ' + endDateString + ') group by Value__c';
            }
            else if (newGraph.startDate__c != null && newGraph.endDate__c == null) {
                newGraph.Query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + newGraph.Attribute__c + '\' AND parentevent__c in ' + 
                    '(select ID from Event__c where TimeStamp__c > ' + startDateString + ') group by Value__c';
            }
            else if (newGraph.startDate__c == null && newGraph.endDate__c != null) {
                newGraph.Query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + newGraph.Attribute__c + '\' AND parentevent__c in ' + 
                    '(select ID from Event__c where TimeStamp__c < ' + endDateString + ') group by Value__c';
            }                
        }
    }
    global Graph__c getGraph()
    {
        if (newGraph == null) {
            init();
        }
        return newGraph;
    }
    global void save()
    {
        setQuery();
        upsert(newGraph);
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
        /*
        EventAttribute__c ea = new EventAttribute__c();
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
        System.assert(attr.size() == 2);*/
    }    
}