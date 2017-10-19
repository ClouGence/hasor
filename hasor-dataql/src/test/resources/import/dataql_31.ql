import "@/import/dataql_30.ql" as udfLib

var lib = udfLib()~

var foo1 = lib -> "foo1" ~
var foo2 = lib -> "foo2" ~

return foo1()~ + foo2()~