/* This is a basic version. It does not include Event to Event_Attribute relationships. 
It only looks at each Event_Attribute separately for the sake of quick vertical prototyping*/
public with sharing class EventGraphPrototype {
   public boolean isIPhone {get;set;}
   //public boolean isIPad {get;set;}
   public boolean isAndroid {get;set;}
   public boolean isDesktop {get;set;}
  
   public EventGraphPrototype() {
      String userAgent = System.currentPageReference().getHeaders().get('User-Agent');
      String device = System.currentPageReference().getParameters().get('device');
      isIPhone = (userAgent.contains('iPhone'));
      //isIPad = (userAgent.contains('iPad') ||
               //(userAgent.contains('AppleWebKit') && device != 'mobile'));
      isAndroid = (userAgent.contains('Android')); 
      isDesktop = (userAgent.contains('Chrome') || userAgent.contains('Safari')) && !userAgent.contains('mobile');         
   }

   
   public List<Data> getData() {
      return EventGraphPrototype.getChartData();
   }
   
   
   public List<Data> getData2() {
      EventGraphPrototype2 proto2 = new EventGraphPrototype2(); 
      return proto2.getData();
   }
   
   public static List<Data> getRemoteData() {
      return EventGraphPrototype.getChartData();
   }
   
   public static List<Data> getChartData() {
      List<Data> data = new List<Data>();
      //List<Integer> counts = EventGraphPrototype.getCounts();
      data.add(new Data('Female', getFemales()));
      data.add(new Data('Male', getMales()));
      return data;
   }
   
  public static Integer getFemales() {
     return [select count() from EventAttribute__c where EventAttribute__c.Key__c = 'Gender'
        AND EventAttribute__c.Value__c = 'Female'];
  }
  
  public static Integer getMales() {
     return [select count() from EventAttribute__c where EventAttribute__c.Key__c = 'Gender'
        AND EventAttribute__c.Value__c = 'Male'];
  }   
  
    /*public class Data {
    public String name { get; set; }
    public Integer data { get; set; }
    public Data(String name, Integer data) {
       this.name = name;
       this.data = data;
    }
  }*/
      /*
   public static Integer getFemales() {
        Integer females;
        List<event__c> events = [SELECT ID FROM event__c];
        for (event__c event : events)
        {
            for (eventAttribute__c attrib : [SELECT key__c,value__c FROM eventAttribute__c WHERE parentEvent__c=:event.ID])
            {
                if(attrib.key__c == 'Gender' && attrib.value__c == 'Female')
                    females++;
            }
        }  
        return females; 
   }


  public static List<Integer> getCounts() {
        List<Integer> counts = new List<Integer>();
        Integer females;
        Integer males;
        List<event__c> events = [SELECT name FROM event__c WHERE name='test_login'];
        
        for (event__c event : events)
        {
            for (eventAttribute__c attrib : [SELECT key__c,value__c FROM eventAttribute__c WHERE parentEvent__c=:event.ID])
            {
                if(attrib.key__c == 'Gender' && attrib.value__c == 'Female')
                    females++;
                else if (attrib.key__c == 'Gender' && attrib.value__c == 'Male')
                    males++;
            }
        }
        counts.add(females);
        counts.add(males); 
        return counts;
    }
  */
}