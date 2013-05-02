global with sharing class GraphAdder2
{
    Graph__c newGraph = new Graph__c();
    ApexPages.StandardController stdController;
    
    global GraphAdder2.GraphAdder2(ApexPages.StandardController controller)
    {
        stdController = controller;
    }
    global void init() {
        
        newGraph.name = 'Hello';
        newGraph.chartType__c = 'pie';
        newGraph.attribute__c = 'hi';
        newGraph.query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + 
            newGraph.Attribute__c + '\' group by Value__c';
    }
    global void setQuery() {
        newGraph.Query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + newGraph.Attribute__c + '\' AND parentid in ' + 
        '(select ID from Event__c where TimeStamp__c > ' + newGraph.startDate__c + ' AND TimeStamp__c < ' + newGraph.endDate__c + ') group by Value__c';
    }
    global Graph__c getGraph()
    {
        init();
        return newGraph;
    }
    global void save()
    {
        setQuery();
        upsert(newGraph);
    }
    
    public Set<SelectOption> getAttributes()
    {
      Set<SelectOption> options = new Set<SelectOption>();
            
      // GET ATTRIBUTES SOQL
       List<EventAttribute__c> attributes = [select key__c, value__c from eventAttribute__c];
       
       
       for(EventAttribute__c attributeName : attributes)
       {
          options.add(new SelectOption(attributeName.key__c, attributeName.key__c));
       }       
       return options;
    }

}