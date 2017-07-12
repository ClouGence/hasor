var ioc = {
    conf: {
        type: "org.nutz.ioc.impl.PropertiesProxy",
        fields: {
            paths: ["custom/"]
        }
    },
    echoService: {
        type: "net.example.nutz.provider.EchoServiceImpl"
    },
    messageService: {
        type: "net.example.nutz.provider.MessageServiceImpl"
    }

}