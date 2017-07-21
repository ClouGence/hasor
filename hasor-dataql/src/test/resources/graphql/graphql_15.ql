option aa = true;

var foo = lambda : (a,b,c) -> {
    if (a == 1)
        var a = b;
        throw 123 , c
    end
    exit 12 ,a;
}

var res = foo(1,2,3,4,5)~;

return 54321;
