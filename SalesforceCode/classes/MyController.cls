public with sharing class MyController {
    public boolean isIPhone {get;set;}
    public boolean isIPad {get;set;}
  

    //My code
    public MyController() {
         String userAgent = System.currentPageReference().getHeaders().get('User-Agent');
         String device = System.currentPageReference().getParameters().get('device');
         isIPhone = (userAgent.contains('iPhone') || device == 'mobile');
         isIPad = (userAgent.contains('iPad') ||
                  (userAgent.contains('AppleWebKit') && device != 'mobile'));
    }
    
    //getPieData set the data for the pie graph
    public List<PieWedgeData> getPieData() {
        List<PieWedgeData> data = new List<PieWedgeData>();
        data.add(new PieWedgeData('Martin', 15.3));
        data.add(new PieWedgeData('Brandon', 13.3));
        data.add(new PieWedgeData('Stephanie', 10.34));
        data.add(new PieWedgeData('Cory', 25));
        data.add(new PieWedgeData('Mark', 20));
        data.add(new PieWedgeData('BillyZane', 5));
        return data;
    }

    // Wrapper class
    public class PieWedgeData {

        public String name { get; set; }
        public Decimal data { get; set; }

        public PieWedgeData(String name, Decimal data) {
            this.name = name;
            this.data = data;
        }
    }

}