var treeData = [
        {
            "id":1,
            "label":"t1",
            "children":[
                {
                    "id":2,
                    "label":"t2",
                    "children":[
                        {
                            "id":4,
                            "label":"t4",
                            "children":[]
                        }
                    ]
                },
                {
                    "id":3,
                    "label":"t3",
                    "children":[]
                }
            ]
        },
        {
            "id":5,
            "label":"t5",
            "children":[]
        }
    ]

var treeFmt = (dat) -> {
        return {
            "id"       : dat.id,
            "parent_id": ((@[-3] !=null)? @[-3].id : null),
            "label"    : dat.label,
            "children" : dat.children => [ treeFmt(#) ]
        }
}

return treeData => [ treeFmt(#) ]