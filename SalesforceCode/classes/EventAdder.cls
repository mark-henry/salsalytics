@RestResource(urlMapping='/EventAdder/*')
global with sharing class EventAdder
{
    //@HttpPost
    // Attributes in format "key:value,key:value"
    global static void addEvent(string name, string attributes)
    {
        Event__c event = new Event__c();
        event.Name = name;
        event.timestamp__c = DateTime.now();
        insert event;
        
        if (attributes == '')
            return;
        
        attributes = jsonifyAttributes(attributes);
        
        map<string,string> attributesMap = (map<string,string>) System.JSON.deserialize(attributes, map<string,string>.class);
        for (string key : attributesMap.keySet())
        {
            EventAttribute__c attribute = new EventAttribute__c();
            if (key == 'AppName') {
                event.AppName__c = attributesMap.get(key);
                upsert event;
            }
            else {
                attribute.key__c = key;
                attribute.value__c = attributesMap.get(key);
                attribute.ParentEvent__c = event.ID;
                insert attribute;
            }
        }
    }

    @HttpPost
    global static void addEvents(List<EventWithAttrs> events)
    {
        for (EventWithAttrs event : events) {
            Event__c newEvent = new Event__c();
            newEvent.Name = event.title;
            newEvent.timestamp__c = DateTime.now();
            insert newEvent;
            for (String key : event.attrs.keySet()) {
                if (key == 'AppName') {
                    newEvent.AppName__c = event.attrs.get(key);
                    upsert newEvent;
                }
                else {
                    EventAttribute__c attr = new EventAttribute__c();
                    attr.key__c = key;
                    attr.value__c = event.attrs.get(key);
                    attr.ParentEvent__c = newEvent.ID;
                    insert attr;
                }
            }
        }
    }
    
    @IsTest
    static public void testAddEvents()
    {
        String sEvents = '[{"title":"eventName","attrs":{"attr1":"val1","attr2":"val2"}},{"title":"eventName2","attrs":{"attr1":"val1","attr2":"val2"}}]';
        List<EventWithAttrs> events = (List<EventWithAttrs>) System.JSON.deserialize(sEvents, List<EventWithAttrs>.class);
        
        System.assert(events.get(0).title == 'eventName');
        System.assert(events.get(0).attrs.get('attr1') == 'val1');
        System.assert(events.get(0).attrs.get('attr2') == 'val2');
        System.assert(events.get(1).title == 'eventName2');
        System.assert(events.get(1).attrs.get('attr1') == 'val1');
        System.assert(events.get(1).attrs.get('attr2') == 'val2');
        
        addEvents(events);
        
        List<event__c> eventsTest1 = [SELECT name FROM event__c WHERE name='eventName'];
        System.assert(eventsTest1[0].name == 'eventName');
        
        for (eventAttribute__c attrib : [SELECT key__c,value__c FROM eventAttribute__c WHERE parentEvent__c=:eventsTest1[0].ID])
        {
            if (attrib.key__c == 'attr1')
                System.assert(attrib.value__c == 'val1');
            else if (attrib.key__c == 'attr2')
                System.assert(attrib.value__c == 'val2');
        }
        
        List<event__c> eventsTest2 = [SELECT name FROM event__c WHERE name='eventName2'];
        System.assert(eventsTest2[0].name == 'eventName2');
        
        for (eventAttribute__c attrib : [SELECT key__c,value__c FROM eventAttribute__c WHERE parentEvent__c=:eventsTest2[0].ID])
        {
            if (attrib.key__c == 'attr1')
                System.assert(attrib.value__c == 'val1');
            else if (attrib.key__c == 'attr2')
                System.assert(attrib.value__c == 'val2');
        }
    }
    
    static private string jsonifyAttributes(string attribs)
    {
        attribs = '{"' + attribs + '"}';
        attribs = attribs.replaceAll(':', '":"');
        attribs = attribs.replaceAll(',', '","');
        return attribs;
    }
    
  /*  @IsTest
    static public void testAddSingleEvent()
    {
        //Code may not actually be used in actual product
        EventAdder adder = new EventAdder();
        
        //----------------------------------------------
        
        
        addEvent('test_login', 'user:jdoe');
        event__c event = [SELECT name FROM event__c WHERE name='test_login'];
        System.assert(event.name == 'test_login');
        
        eventAttribute__c attrib = [SELECT value__c FROM eventAttribute__c WHERE key__c='user' AND parentEvent__c=:event.ID];
        System.assert(attrib.value__c == 'jdoe');
    }*/
    
    @IsTest
    static public void testAddManyAttributes()
    {
        addEvent('test_login', 'user:jdoe,gender:male,age:20');
        
        List<event__c> events = [SELECT name FROM event__c WHERE name='test_login'];
        System.assert(events[0].name == 'test_login');
        
        for (eventAttribute__c attrib : [SELECT key__c,value__c FROM eventAttribute__c WHERE parentEvent__c=:events[0].ID])
        {
            if (attrib.key__c == 'user')
                System.assert(attrib.value__c == 'jdoe');
            else if (attrib.key__c == 'gender')
                System.assert(attrib.value__c == 'male');
            else if (attrib.key__c == 'age')
                System.assert(attrib.value__c == '20');
        }
    }
    
    @isTest(seeAllData=true)
    static void testEventWithApp() 
    {
        
    }
        
}