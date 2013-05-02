global class EventWithAttrs {
    public String title;
    public Map<String, String> attrs;

    @IsTest
    static public void testEventWithAttrs() {
        EventWithAttrs ewt = new EventWithAttrs();
        map<String, String> newmap = new Map<String, String>();
        ewt.title = 'hi';
        ewt.attrs = newmap;
        ewt.attrs.put('hello', 'world');
        System.assertEquals('hi', ewt.title);
        System.assertEquals('world', ewt.attrs.get('hello'));
    }
}