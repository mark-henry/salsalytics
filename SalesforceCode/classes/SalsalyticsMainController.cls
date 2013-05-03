public with sharing class SalsalyticsMainController {
    integer numTabs = 2;
    Component.Apex.Chart chart;
    Component.Apex.pieSeries pie;
    Component.Apex.Legend legend;
    Component.Apex.barSeries bar;
    Component.Apex.Axis axis;
    
    public Set<String> getAppNames () {
        Set<String> appNames =  new Set<String>();
        List<EventAttribute__c> attributes = [select key__c, value__c from eventAttribute__c where key__c = 'AppName'];
        
        //Gets only distinct App names
        appNames.add('all');
        for(EventAttribute__c appName : attributes){
            appNames.add(appName.value__c);    
        }
        
        return appNames;
    }
    
    public String getMobileDashboardFormat () {
        String outputText = '';   
        Set<String> appNames =  new Set<String>();
        List<Event__c> attributes = [select AppName__c from Event__c];
        
        //Gets only distinct App names
        appNames.add('all');
        for(Event__c appName : attributes){
            if (appName != null && appName.appName__c != null) 
                appNames.add(appName.appName__c);    
        }
        
        for(String appName : appNames){
        
            outputText+= '<div data-role="collapsible">';
            
            if(appName == 'all')
                outputText+= '<h3>Miscellaneous</h3>';
            else
                outputText+= '<h3>' + appName + '</h3>';
                
            outputText+= '<div data-role="content">';
            outputText+= '<ul id="contents" data-inset="true" data-role="listview" data-theme="c" data-dividertheme="c">';
            outputText+= getGraphsForApp(appName);
            outputText+= '</ul>';
            outputText+= '</div>';
            outputText+= '</div>';        
        }
            
        return outputText;
    }
    
   //public static List<Data> getData() {
   //   return EventGraphPrototype2.getChartData();
   //}
    
    public Map<String, String> getGraphNames() {
        Map<String, String> stringList = new Map<String, String>();
        List<Graph__c> attributes = new List<Graph__c>();
        attributes = [select Name, VisualForce_Page__c from Graph__c];
        for (Graph__c attribute : attributes)
        {
           stringList.put(attribute.Name, attribute.VisualForce_Page__c);
        }
        return stringList;
    }
    
   // public Boolean getRendered(){
   //     return true;
   // }
    
    public Map<String, String> getGraphIDs() {
        Map<String, String> result = new Map<String, String>();
        List<Graph__c> graphs = [SELECT name,ID from Graph__c];
        for (Graph__c g : graphs) {
            result.put(g.Name, g.ID);
        }
        return result;
    }
    
    public String getGraphsForApp (String appName) {
        String returnStr = '';
        String query = 'SELECT name,ID from Graph__c where AppName__c = \'' + appName + '\''; 
        List<Graph__c> graphs = Database.query(query);
        for (Graph__c g : graphs) {
            returnStr+=  '<LI><a href="singlegraph?graphid=' + g.ID + '&mobile=true" data-ajax="false">' + g.Name + '</a></LI>';
        }
        
        return returnStr;
    }
    
    
    /**
     * Tests
     *
     */
     
    @IsTest
    public static void getAppNamesTest() {
        SalsalyticsMainController controller = new SalsalyticsMainController();
        Set<String> appNames = controller.getAppNames();
        
        //Only 'all' should exist as an app name
        System.assert(!appNames.isEmpty() && appNames.contains('all'));
        System.assert(appNames.size() == 1);
        
        GraphAdder adder = new GraphAdder();
        Graph__c sampleGraph = adder.getGraph();
        sampleGraph.Attribute__c = 'SampleGraph';
        sampleGraph.AppName__c='SampleApp';
        sampleGraph.Name='SampleGraph';
        adder.save();
        
        appNames = controller.getAppNames();
        //System.assert(appNames.contains('SampleApp'));
        
        
        
    }
    
    @IsTest
    public static void getMobileDashboardFormatTest() {
        SalsalyticsMainController controller = new SalsalyticsMainController();
        String expected = '<div data-role="collapsible"><h3>Miscellaneous</h3><div data-role="content"><ul id="contents" data-inset="true" data-role="listview" data-theme="c" data-dividertheme="c"></ul></div></div>';
        String result = controller.getMobileDashboardFormat();
        
        System.assertEquals(expected,result);
    }
    
    @IsTest
    public static void getGraphNamesTest() {
        SalsalyticsMainController controller = new SalsalyticsMainController();
        Map<String, String> names = controller.getGraphNames();
        Map<String, String> ids = controller.getGraphIDs();
        
        System.assert(names.isEmpty());
        System.assert(ids.isEmpty());
        
        GraphAdder adder = new GraphAdder();
        Graph__c newGraph = adder.getGraph();
        Integer graphCount = [select count() from salsaforce__Graph__c];
 
        newGraph.Attribute__c = 'Graph1';
        adder.save();
        graphCount = [select count() from salsaforce__Graph__c];
        
        names = controller.getGraphNames();
        ids = controller.getGraphIDs();
        
        //System.debug(names.containsKey('Graph1'));
        System.assert(!names.isEmpty());
        System.assert(!ids.isEmpty());
    }
}