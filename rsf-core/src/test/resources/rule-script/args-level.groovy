package flowcontrol

def Map<String,Map<String,List<String>>> evalAddress(String serviceID,List<String> allAddress)  {
    //
    //[RSF]sorg.mytest.FooFacse-1.0.0 ---- Group=RSF, Name=sorg.mytest.FooFacse, Version=1.0.0
    if ( serviceID == "[RSF]sorg.mytest.FooFacse-1.0.0" ) {
        return [
            "println":[
                "":[
                    "192.168.1.2:8000",
                    "192.168.1.2:8001",
                    "192.168.1.3:8000"
                ],
            ],
            "sayEcho":[
                "sayToServerA":[
                    "192.168.1.2:8000",
                ],
                "sayToServerB":[
                    "192.168.1.2:8001",
                ],

            ],
            "testUserTag":[
                "192.168.1.2:8000",
                "192.168.1.3:8000"
            ]
        ]
    }
    return null
}