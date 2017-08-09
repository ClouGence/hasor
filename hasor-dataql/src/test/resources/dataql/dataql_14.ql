var a = {};
var a = 123;
var a = -a;
exit a;

var abs = lambda : (arg1) -> {
    return arg1;
}

var foo = lambda : (arg1,arg2) -> {
    if (arg1 > arg2)
        return arg1 + arg3;
    else
        return arg2 + arg3;
    end
};

return abs(123.5)~ + foo(1,2)~