public class GenderRemotingController {
    Component.Apex.Chart chart;
    

    public Component.Apex.Chart getDynamicDetail() {
        Component.Apex.Chart chart = new Component.Apex.Chart();
        Component.Apex.pieSeries pie = new Component.Apex.pieSeries();
        pie.dataField = 'data';
        pie.labelField = 'name';
        chart.width = '500';
        chart.height = '500';
        chart.data = EventGraphPrototype2.getChartData();
        chart.childComponents.add(pie);
        return chart;
    }
    
    public String getSomeChart() {
        return '<div id="chart" value="h,1+d,3" />';
    }

    // Just return the first Account, for example purposes only
    public Account acct {
        get { return [SELECT Id, Name, OwnerId FROM Account LIMIT 1]; }
    }
    
    @RemoteAction
    public static Integer getFemales() {
        return [select count() from SimplePrototypeObject__c where Gender__c = 'Female'];
    }
  
    @RemoteAction
    public static Integer getMales() {
        return [select count() from SimplePrototypeObject__c where Gender__c = 'Male'];
    }
}