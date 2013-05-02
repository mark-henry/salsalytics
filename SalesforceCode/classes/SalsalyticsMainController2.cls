public with sharing class SalsalyticsMainController2 {
    integer numTabs = 2;
    Component.Apex.Chart chart;
    Component.Apex.pieSeries pie;
    Component.Apex.Legend legend;
    Component.Apex.barSeries bar;
    Component.Apex.Axis axis;
    
    public Component.Apex.TabPanel getTabs() {
        Component.Apex.TabPanel tabPanel = new Component.Apex.TabPanel();
        
        for (integer i=0; i<numTabs; i++) {
            Component.Apex.Tab tab = new Component.Apex.Tab();
            tab.Label = 'Tab ' + string.valueOf(i+1);
        
            pie = new Component.Apex.pieSeries();
            pie.dataField = 'EventGraphPrototype2.getChartData()';  // write getSeries in this class then '{!Series}'
            //pie.dataField = '{Series}';
            pie.rendered = true;
            chart = new Component.Apex.Chart();
            chart.width = '500';
            chart.height = '500';
            chart.data = EventGraphPrototype2.getChartData();
            chart.rendered = true;
            
            chart.childComponents.add(pie);
            
            Component.Apex.Form form = new Component.Apex.Form();
            tab.childComponents.add(chart);
            tabPanel.childComponents.add(tab);
        }
        
        return tabPanel;
    }
    
    public Component.Apex.Chart getGraphs() {
        //Component.Apex.Form form = new Component.Apex.Form();
        
        pie = new Component.Apex.pieSeries();
        pie.dataField = 'data';
        pie.labelField = 'name';
        pie.rendered = true;
        
        //bar = new Component.Apex.barSeries();
        //bar.dataField = 'data';
        //bar.labelField = 'name';
        //bar.rendered = true;
        
        legend = new Component.Apex.Legend();
        legend.position = 'right';
        
        chart = new Component.Apex.Chart();
        chart.hidden = false;
        chart.width = '100';
        chart.height = '100';
        chart.data = '{!Data()}';
        chart.rendered = true;
        
        chart.childComponents.add(pie);
        //form.childComponents.add(chart);
    
        return chart;
    }
    
    public void addTab(){
        numTabs++;
    }
    
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
        return false;
    }
    

    public Component.Apex.Detail getDynamicDetail() {
        Component.Apex.Detail detail = new Component.Apex.Detail();
        detail.expressions.subject = '{!acct.OwnerId}';
        detail.relatedList = false;
        detail.title = false;
        return detail;
    }

    // Just return the first Account, for example purposes only
    public Account acct {
        get { return [SELECT Id, Name, OwnerId FROM Account LIMIT 1]; }
    }    
}