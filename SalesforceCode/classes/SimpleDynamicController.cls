public class SimpleDynamicController {
    public String getSomeChart() {
        return '<div id="chart1" class="chart" ' + 
        'field1="hello" ' +
        'field2="world" ' +
        'value1=2 ' +
        'value2=3 ' +
        'chartType="pie" ' +
        '></div>' +
        '<div id="chart2" class="chart" ' + 
        'field1="hello1" ' +
        'field2="world1" ' +
        'value1=3 ' +
        'value2=7 ' +
        'chartType="pie" ' +
        '></div>';
    }
}