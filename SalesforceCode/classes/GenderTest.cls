public with sharing class GenderTest {
   public List<Data> getData() {
      return GenderTest.getChartData();
   }
   
   public static List<Data> getRemoteData() {
      return GenderTest.getChartData();
   }
   
   public static List<Data> getChartData() {
      List<Data> data = new List<Data>();
      data.add(new Data('Female', GenderTest.getFemales()));
      data.add(new Data('Male', GenderTest.getMales()));
      return data;
   }

  public static Integer getFemales() {
     return [select count() from SimplePrototypeObject__c where Gender__c = 'Female'];
  }
  
  public static Integer getMales() {
     return [select count() from SimplePrototypeObject__c where Gender__c = 'Male'];
  }
  
  public class Data {
    public String name { get; set; }
    public Integer data { get; set; }
    public Data(String name, Integer data) {
       this.name = name;
       this.data = data;
    }
  }
}