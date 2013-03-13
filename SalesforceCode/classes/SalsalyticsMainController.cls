public with sharing class SalsalyticsMainController {
    integer numTabs = 2;
    Component.Apex.Chart chart;
    Component.Apex.pieSeries pie;
    Component.Apex.Legend legend;
    Component.Apex.barSeries bar;
    Component.Apex.Axis axis;
    
    
   public static List<Data> getData() {
      return EventGraphPrototype2.getChartData();
   }
    
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
    
    public Boolean getRendered(){
        return true;
    }
    
    public Map<String, String> getGraphIDs() {
        Map<String, String> result = new Map<String, String>();
        List<Graph__c> graphs = [SELECT name,ID from Graph__c];
        for (Graph__c g : graphs) {
            result.put(g.Name, g.ID);
        }
        return result;
    }
}