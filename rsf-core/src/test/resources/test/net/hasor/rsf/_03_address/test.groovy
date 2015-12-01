package test.net.hasor.rsf._03_address
import net.hasor.rsf.address.InterAddress
def Map<String,List<InterAddress>> evalAddress(//
        String serviceID, String scriptText, List<InterAddress> allAddress, List<InterAddress> unitAddress)  {
    //
    //[RSF]sorg.mytest.FooFacse-1.0.0 ，组别：RSF，接口：sorg.mytest.FooFacse，版本：1.0.0
    if ( serviceID == "[RSF]sorg.mytest.FooFacse-1.0.0" ) {
        def resultData = []
        resultData["insert"]        = [new InterAddress()]
        resultData["queryUserByID"] = unitAddress
        return resultData
    }
    return null;
}