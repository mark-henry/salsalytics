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
        String query = 'SELECT Name, ChartType__c, attribute__c, query__c, startdate__c,' +
        ' enddate__c from Graph__c where ID = \'' + graphID + '\' LIMIT 1';
        existingGraph = Database.query(query);
    }

    global void setQuery() {
        String startDateString, endDateString;
        Integer spaceIndex; 
        
        if (existingGraph.startDate__c == null && existingGraph.endDate__c == null) {
            existingGraph.Query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + existingGraph.Attribute__c + '\' group by Value__c';
        } 
        else { 
            if (existingGraph.startDate__c != null) {
                startDateString = '' + (existingGraph.startDate__c);
                spaceIndex = startDateString.indexOf(' ');
                startDateString = startDateString.replace(' ','T');
                startDateString = startDateString + 'Z';
            }
            if (existingGraph.endDate__c != null) {
                endDateString = '' + (existingGraph.endDate__c);
                spaceIndex = endDateString.indexOf(' ');
                endDateString = endDateString.replace(' ','T');
                endDateString = endDateString + 'Z';
            }
            
            if (existingGraph.startDate__c != null && existingGraph.endDate__c != null) { 
                existingGraph.Query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + existingGraph.Attribute__c + '\' AND parentevent__c in ' + 
                    '(select ID from Event__c where TimeStamp__c > ' + startDateString + ' AND TimeStamp__c < ' + endDateString + ') group by Value__c';
            }
            else if (existingGraph.startDate__c != null && existingGraph.endDate__c == null) {
                existingGraph.Query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + existingGraph.Attribute__c + '\' AND parentevent__c in ' + 
                    '(select ID from Event__c where TimeStamp__c > ' + startDateString + ') group by Value__c';
            }
            else if (existingGraph.startDate__c == null && existingGraph.endDate__c != null) {
                existingGraph.Query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + existingGraph.Attribute__c + '\' AND parentevent__c in ' + 
                    '(select ID from Event__c where TimeStamp__c < ' + endDateString + ') group by Value__c';
            }                
        }
    }
    
    global Graph__c getGraph()
    {
        init();
        return existingGraph;
    }
        
    global void save()
    {
        setQuery();
        upsert(existingGraph);
    }
    
    public List<SelectOption> getAttributes()
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

}