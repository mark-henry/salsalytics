global with sharing class GraphDeleter {
    @RemoteAction
    global static void deleteGraph(String graphId) {
        String query = 'SELECT Id from Graph__c where Id = \'' + graphId + '\' LIMIT 1';
        Graph__c existing = Database.query(query);
        delete existing;
        
        //Graph__c newGraph = new Graph__c();
        //newGraph.name = 'ERRORRRRR AREAOUREAOREARAE';
        //upsert(newGraph); 
    }
    
    @IsTest(SeeAllData=true) 
    static void testGraphDeleter()
    {
        String correctString, chartString, graphID;
        List<Graph__c> queries;
        
        //Graph__c someGraph = new Graph__c();
        //someGraph.name = 'MOO';
        
        //upsert(someGraph);
        //String query = 'SELECT count(ID) FROM Graph__c where name = \'MOO\'';
        //SObject sobj = Database.query(query);
        //Integer recordCount = ((Database.query(query)).get(0));
        
        //get all graphs
         queries = ([SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, Name, 
            salsaforce__ChartType__c FROM salsaforce__Graph__c LIMIT 500]);
            
        //remove all graphs
         system.debug('num graphs before: ' + queries.size());
         for (Graph__c g : queries) {
             GraphDeleter.deleteGraph(g.id);
         }
         
          //get all graphs
        queries = ([SELECT id, salsaforce__Attribute__c, salsaforce__Query__c, Name, 
           salsaforce__ChartType__c FROM salsaforce__Graph__c LIMIT 500]);
        system.debug('num graphs after: ' + queries.size());
        //Integer recordCount = Database.countQuery(query);
        System.assert(queries.size() == 0);
    }
}