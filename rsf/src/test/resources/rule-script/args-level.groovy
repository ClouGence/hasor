def Map<String, Map<String, List<String>>> evalAddress(String serviceID, List<String> allAddress) {
    //
    //[RSF]sorg.mytest.FooFacse-1.0.0 ---- Group=RSF, Name=sorg.mytest.FooFacse, Version=1.0.0
    if (serviceID == "[RSF]test.net.hasor.rsf.services.EchoService-1.0.0") {
        return [
                "sayEcho"    : [
                        "sayTo_etc1": [
                                "192.168.2.3:8000",
                                "192.168.3.4:8000",
                                "192.168.4.5:8000"
                        ],
                        "sayTo_etc2": [
                                "192.168.137.10:8000",
                                "192.168.137.11:8000"
                        ]],
                "testUserTag": [
                        "server_3": [
                                "192.168.1.3:8000"
                        ],
                        "server_4": [
                                "192.168.1.4:8000"
                        ]
                ]
        ]
    }
    return null
}