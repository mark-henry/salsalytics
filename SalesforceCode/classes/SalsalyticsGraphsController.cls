public class SalsalyticsGraphsController {
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
        return [select count() from EventAttribute__c where Value__c = 'Female'];
    }
  
    @RemoteAction
    public static Integer getMales() {
        return [select count() from EventAttribute__c where Value__c = 'Male'];
    }
    
   public List<Data> getData() {
      return EventGraphPrototype2.getChartData();
   }
   
   @RemoteAction
   public static List<Data> getRemoteData() {
      return EventGraphPrototype2.getChartData();
   }
   
   @RemoteAction
   public static List<Data> getChartData() {
      List<Data> data = new List<Data>();
      //List<Integer> counts = EventGraphPrototype.getCounts();
      List<EventAttribute__c> attributes = new List<EventAttribute__c>();
      List<String> values = new List<String>();
      attributes = [select Value__c,Key__c from EventAttribute__c 
         where EventAttribute__c.Key__c = 'Level' order by systemmodstamp];
      for (EventAttribute__c attribute : attributes)
      {
          values.add(attribute.value__c);
      }
      values = getDistinct(values);
      //values.sort();
      for (String value : values)
      {
          data.add(new Data(value, getCount(value)));
      }
      
      return data;
   }

   public static List<Data> getChartData(Graph__c graph) {
      List<Data> data = new List<Data>();
      //List<Integer> counts = EventGraphPrototype.getCounts();
      List<EventAttribute__c> attributes = new List<EventAttribute__c>();
      List<String> values = new List<String>();
      //Connection connection = new Connection();
      Database.query('select Value__c,Key__c from EventAttribute__c where EventAttribute__c.Key__c = "Level" order by systemmodstamp');
      String query = graph.query__c;
      
      //attributes = [query];
      for (EventAttribute__c attribute : attributes)
      {
          values.add(attribute.value__c);
      }
      values = getDistinct(values);
      //values.sort();
      for (String value : values)
      {
          data.add(new Data(value, getCount(value)));
      }
      
      return data;
   }
   
  public static Integer getCount(String level) {
     return [select count() from EventAttribute__c where EventAttribute__c.Key__c = 'Level'
        AND EventAttribute__c.Value__c = :level];
  }   
  
  /*public class Data {
    public String name { get; set; }
    public Integer data { get; set; }
    public Data(String name, Integer data) {
       this.name = name;
       this.data = data;
    }
  }*/
  
  /*Workaround because SOQL does not have distinct op*/
      public static List<String> getDistinct(List<String> duplicates){
          List<String> distinctResults = new List<String>();
          for(String entry: duplicates){
              Boolean found = false;
              for(Integer i=0; i< distinctResults.size(); i++){
                  if(entry.equalsIgnoreCase(distinctResults[i])){ 
                     found=true;
                     break;
                  }
              }
              if(!found)
                 distinctResults.add(entry);
          }
       return distinctResults;
    }
    
     public String getGraphTypeTest() {
        return 'pieSeries';
    }
    
    @RemoteAction
    public static List<Graph__c> getGraphs() {
        return [select query__c, name from Graph__c order by systemmodstamp limit 5];
    }    
}