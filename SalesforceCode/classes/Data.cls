public class Data {
   public String name { get; set; }
   public Integer data { get; set; }
   public Data(String name, Integer data) {
      this.name = name;
      this.data = data;
   }

   /**
    * Tests
    *
    */
    @IsTest(SeeAllData=true) static void dataTest() {
        Data testData = new Data('Prandon Bage', 7);
     
        System.assertEquals(testData.name, 'Prandon Bage');
        System.assert(testData.data == 7);
    }
}