/* This is a basic version. It does not include Event to Event_Attribute relationships. 
It only looks at each Event_Attribute separately for the sake of quick vertical prototyping*/
public with sharing class EventGraphPrototype2 {
   public boolean isIPhone {get;set;}
   //public boolean isIPad {get;set;}
   public boolean isAndroid {get;set;}
   public boolean isDesktop {get;set;}
  
   public EventGraphPrototype2() {
      String userAgent = System.currentPageReference().getHeaders().get('User-Agent');
      String device = System.currentPageReference().getParameters().get('device');
      isIPhone = (userAgent.contains('iPhone'));
      isAndroid = (userAgent.contains('Android')); 
      isDesktop = (!userAgent.contains('mobile'));         
   }

   
   public List<Data> getData() {
      return EventGraphPrototype2.getChartData();
   }
   
   @RemoteAction
   public static List<Data> getRemoteData() {
      return EventGraphPrototype2.getChartData();
   }
   
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
}