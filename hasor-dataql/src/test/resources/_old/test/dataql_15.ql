
var foo = (a,b,c) -> {
    if (a == 1)
        var a = b;
        throw 123 , c

    return a + b + c;
}

var res = foo(-1,2,3,4,5);

return foo;
