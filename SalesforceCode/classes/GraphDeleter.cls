global with sharing class GraphDeleter {
    @RemoteAction
    global static void deleteGraph(String graphId) {
        String query = 'SELECT Id from Graph__c where Id = \'' + graphId + '\' LIMIT 1';
        Graph__c existing = Database.query(query);
        delete existing;
        
        Graph__c newGraph = new Graph__c();
        newGraph.name = 'ERRORRRRR AREAOUREAOREARAE';
        upsert(newGraph); 
    }
   /* 
    @IsTest
    static public void testAddSingleEvent()
    {
        Graph__c someGraph = new Graph__c();
        someGraph.name = 'MOO';
        upsert(someGraph);
        String query = 'select count(ID) from Graph__c where name = \'MOO\'';
        Integer recordCount = ((Database.query(query)).get(0)).;
        System.assert(recordCount == 1);
    } */
}