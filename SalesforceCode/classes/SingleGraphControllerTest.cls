@isTest
public class SingleGraphControllerTest
{
    static testMethod void testDeleteGraph()
    {
        Graph__c g = new Graph__c();
        g.Query__c = 'select asdf';
        insert g;
        SingleGraphController.deleteGraph('' + g.ID);
        String query = 'SELECT Id from Graph__c where Id = \'' + g.ID + '\' LIMIT 1';
        System.assert(Database.query(query).isEmpty());
    }
    
    static testMethod void testGetGraph()
    {
        SingleGraphController controller = new SingleGraphController();
        
        // Create and insert dummy event with key/value pair
        Event__c e = new Event__c();
        e.name = 'testevent';
        e.timestamp__c = datetime.now();
        insert e;
        
        EventAttribute__c ea = new EventAttribute__c();
        ea.key__c = 'testkey';
        ea.value__c = 'testvalue';
        ea.ParentEvent__c = e.id;
        insert ea;
        
        // Create and insert graph object that queries on that pair
        Graph__c g = new Graph__c();
        g.query__c = 'SELECT Value__c, count(Key__c) FROM EventAttribute__c where Key__c = \'' + ea.key__c + '\' group by Value__c';
        g.name = 'testgraph1';
        g.attribute__c = ea.key__c;
        g.charttype__c = 'pie';
        insert g;
        
        // Create fake page environment since getGraph accesses query string!
        ApexPages.currentPage().getParameters().put('graphid', g.id);
        
        // Check that output graph string has the important stuff in it
        String chartString = controller.getGraph();
        System.assert(chartString.contains('testvalue'));
        System.assert(chartString.contains('1'));
        System.assert(chartString.contains('testgraph1'));
        System.assert(chartString.contains('pie'));
    }
}