package flowcontrol

def List<String> evalAddress(String serviceID,List<String> allAddress, List<String> unitAddress)  {
    //
    //[RSF]sorg.mytest.FooFacse-1.0.0 ---- Group=RSF, Name=sorg.mytest.FooFacse, Version=1.0.0
    if ( serviceID == "[RSF]sorg.mytest.FooFacse-1.0.0" ) {
        return [
            "192.168.1.2:8000",
            "192.168.1.2:8001",
            "192.168.1.3:8000"
        ]
    }
    return null
}