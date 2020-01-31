var bool = true;
var bool = false;
var bool = true
var bool = false

var a = 123;
var a = -123;
var a = 12.30;
var a = -12.30;
var a = 1.23e234;
var a = -1.23e234;
var a = 123
var a = -123
var a = 12.30
var a = -12.30
var a = 1.23e234
var a = -1.23e234
var a = 1.234E-2345
var a = -1.234E+2345
var a = 0X0123456789abcdefABCDEF
var a = 0X0123456789abcdefABCDEF;
var a = 0O01234567
var a = 0O01234567;
var a = 0b1010100101
var a = 0B1010100101
var a = 0b1010100101;
var a = 0B1010100101;

var a = "aaaa";
var a = "cn lang";
var a = 'sas as ';
var a = ""
var a =''
var a = "\uffc7\uffc7\uffc7\uffc7\uffc7\uffc7\uffc7\uffc7\uffc7";
var a = "\t\f\\'adsfasf'\r\b\n\r\t\b\\'\n\r\t\f"
var a = '\n\t\f\\"adsfasf"\r\b\n\r\t\b\\"\n\r\t\f'
var a = "var a = \"sdf\""
var a = "var a = 'sdf'"
var a = 'var a = \"sdf\"';
var a = 'var a = \'sdf\'';
var a = 'var a = "sdf"';
var a = "\uffc7\n\t\f\\'adsfasf\'\r\b\"";
var a = "aaaa"
var a = "cn lang"
var a = 'sas as '
var a = '中文'

//  test test 中午呢


var a = abc
var a = abccccccccccccccccccccccccccccccccccccccccccccccccc
var a = abccccccccccccccccccccccccccccccccccccccccccccccccc.cccc
var a = abc.cc.cccc.xx
var a = ${abcccccccxx}
var a = #{abcccccccxx}
var a = ${aaa}
var a = #{aaa}
var a = #{aaacc}
var a = ${aaacc}

var a = #{aaaa}
var a = ${s.a1.a2}

var a = this[0].sss.a1.a2.a3_1qw
var a = a[0][1][1][1]
var a = b[0][1][1][1];
var a = c[0][1][1][1]

var a = null;
var a = null

var b = []
var b = {}
var b = [];
var b = {};
var b = {
    "aaaa" : ff,
    'vvvv' : fff
}
var b = {
    "aaaa",
    'vvvv'
}
var b = {
    "aaaa" : ffff,
    'vvvv',
    "aaaa" : "ffff",
    'vvvv' : true,
    'vvvv' : 1.234E-2345,
    'vvvv' : ${abc},
    "aaaa" : "ffff",
    'cc'   : abc,
    'vvvv' : abc,
    'cc'   : ${abc},
    'vvvv' : #{abc}
}
var b = {
    "aaaa" : ffff,
    'vvvv',
    'ccc'  : {
        "aaaa" : "ffff",
        'vvvv' : true,
        'vvvv' : 1.234E-2345,
        'vvvv' : abc,
        "dddd" : [
            {
                'cc'   : abc,
                'vvvv' : abc,
                'cc'   : ${abc},
                'vvvv' : #{abc}
            }
        ]
    },
    "eee"  : filter ("aaa" , bbbb ,true ,12345 , 1.2345 , -1)
};
var b = [
    123,123,123
]
var b = [
    123,cur,123
]
var b = [
    123,{ "abc" },123
]
var b = [
    123,
    {
        "aaaa" : ffff,
        'vvvv',
        'ccc'  : {
            "aaaa" : "ffff",
            'vvvv' : true,
            'vvvv' : 1.234E-2345,
            'vvvv' : abc,
            "dddd" : [
                {
                    'cc'   : abc,
                    'cc'   : #{abc}
                }
            ]
        },
        "eee"  : filter ("aaa" , bbbb ,true ,12345 , 1.2345 , -1)
    },
    123
]

var f = filter()
var f = filter() => []
var f = filter() => {}
var f = filter() ;
var f = filter() => [];
var f = filter() => {};
var f = filter({})
var f = filter([])
var f = filter(a)
var f = filter("abc"    ,'abc'      ,true       ,false,
               1234     ,-1234      ,1.234      ,-1.234,
               1.2e12   ,-1.2e12    ,1.2E12     ,-1.2E12,
               1.2e-12  ,-1.2e-12   ,1.2E+12    ,-1.2E+12,
               {}       ,[]
         );
var f = filter("abc"    ,'abc'      ,true       ,false,
               1234     ,-1234      ,1.234      ,-1.234,
               1.2e12   ,-1.2e12    ,1.2E12     ,-1.2E12,
               1.2e-12  ,-1.2e-12   ,1.2E+12    ,-1.2E+12,
               {}       ,[]
         ) => [];
var f = filter("abc"    ,'abc'      ,true       ,false,
               1234     ,-1234      ,1.234      ,-1.234,
               1.2e12   ,-1.2e12    ,1.2E12     ,-1.2E12,
               1.2e-12  ,-1.2e-12   ,1.2E+12    ,-1.2E+12,
               {}       ,[]
         ) => {};
var f = filter("abc"    ,'abc'      ,true       ,false,
               1234     ,-1234      ,1.234      ,-1.234,
               1.2e12   ,-1.2e12    ,1.2E12     ,-1.2E12,
               1.2e-12  ,-1.2e-12   ,1.2E+12    ,-1.2E+12,
               {}       ,[]
         ) => [
    123,
    {
        "aaaa" : ffff,
        'vvvv',
        'ccc'  : {
            "aaaa" : "ffff",
            'vvvv' : true,
            'vvvv' : 1.234E-2345,
            'vvvv' : abc,
            "dddd" : [
                {
                    'cc'   : abc,
                    'vvvv' : abc,
                    'cc'   : ${abc},
                    'vvvv' : #{abc}
                }
            ]
        },
        "eee"  : filter ("aaa" , bbbb ,true ,12345 , 1.2345 , -1)
    },
    123
]
var f = filter("abc"    ,'abc'      ,true       ,false,
               1234     ,-1234      ,1.234      ,-1.234,
               1.2e12   ,-1.2e12    ,1.2E12     ,-1.2E12,
               1.2e-12  ,-1.2e-12   ,1.2E+12    ,-1.2E+12,
               {"aa" : bbb , 'bbb' , 'cc' : true , "ee" : 123},
               [{},{},{}]
         ) => {
            "aaaa" : ffff,
            'vvvv',
            'ccc'  : {
                "aaaa" : "ffff",
                'vvvv' : true,
                'vvvv' : 1.234E-2345,
                'vvvv' : abc,
                "dddd" : [
                    {
                        'cc'   : abc,
                        'vvvv' : abc,
                        'cc'   : ${abc},
                        'vvvv' : #{abc}
                    }
                ]
            },
            "eee"  : filter ("aaa" , bbbb ,true ,12345 , 1.2345 , -1)
        };

var f = fun(fun1()       ,fun2()       )
var f = fun(fun1() => {} ,fun2() => [] ) => {}
var f = fun(fun1() => {} ,fun2() => [] ) => []
var f = fun(fun1()       ,fun2()       );
var f = fun(fun1() => {} ,fun2() => [] ) => {};
var f = fun(fun1() => {} ,fun2() => [] ) => [];

var f = () -> return ""
var f = () -> return true;
var f = () -> { return true }
var f = () -> { return true };
var f = () -> {
    var a = true;
    return a;
}
var f = (obj) -> {
    var a = true;
    return a;
};
var f = f("");

throw 1;
throw 1,1;
throw 1,"ssss";
throw -23,"ssss";
throw 222, 1 + -2;
throw 000, ["",""]

// 一元表达式
var a = -10
var a = 0x10
var a = 0o10
var a = 0b10
var a = 0b10
var a = -abc.das.eqw
var a = -a[0][0][0][9]
var a = !abc
var a = !true
var a = !false
var a = 10 + 1
var a = 0x10  + 1
var a = 0o10 + 1
var a = 0b10 + 1
var a = 0b10 + 1
var a = abc.das.eqw + 1
var a = a[0][0][0][9] + 1
var a = true + 1
var a = false + 1
var a = !abc
var a = !true

// 二元运算符
var f = '' + '';
var f = '' + "";
var f = '' + "ssfdsf"
var f = 'sdfsdf' + "ssfdsf";
var f = 123 + 456 + 0x234 + 0b11100101;
var f = '' - '';
var f = '' - "";
var f = '' - "ssfdsf"
var f = 'sdfsdf' - "ssfdsf";
var f = 123 - 456 + 0x234 - 0b11100101;

var f = a + b * c / d % e \ f
var f = a > b >= c < d <= e == f != g
var f = a & b | c ^ d
var f = a << b >> c >>> d
var f = a && b || c

var f = a({"abc":cc}) && b([1,2,3,4]) || c(d())
var f = (obj.amount > markAmount) && (markAmount > 0)

return f
var a = (agr) -> { return 2};
var b = (agr) -> { return 1};

var f = (arg) -> {
    var b = false;
    var a = (arg) -> {
        return count(arg);
    };
    return a;
};