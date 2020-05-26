import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;
import "net.hasor.dataql.fx.basic.StringUdfSource" as string;
import "net.hasor.dataql.fx.basic.CollectionUdfSource" as collect;

var apiDataList = ${apiDataList};

var converRequestSchema = (apiMethod, typeData) -> {
    if (typeData == null) {
        return null;
    }
    if ("get" != string.toLowerCase(apiMethod)) {
        return [
            {
                "in"     : "body",
                "name"   : "mainBody",
                "schema" : typeData
            }
        ]
    }
    return collect.map2list(typeData.properties) => [
        {
            "name"       : key,
            "in"         : "query",
            "description": "a param",
            "required"   : false,
            "type"       : value.type
        }
    ]
    // {
    //     "description": "OK",
    //     "schema": typeData
    //     // description	string	Required. A short description of the response. GFM syntax can be used for rich text representation.
    //     // schema	Schema Object	A definition of the response structure. It can be a primitive, an array or an object. If this field does not exist, it means no content is returned as part of the response. As an extension to the Schema Object, its root type value may also be "file". This SHOULD be accompanied by a relevant produces mime-type.
    //     // headers	Headers Object	A list of headers that are sent with the response.
    //     // 	Example Object	An example of the response message.
    // }
}

var converResponseSchema = (typeData, sample) -> {
    return {
        "description": "OK",
        "schema": typeData//,
        // "headers" : {},
        // "examples" : {
        //     "application/json" : sample
        // }
    }
}

var converApi = (apiData) -> {
    return {
        "tags": [],
        "summary": apiData.comment,
        "operationId": ("api" + apiData.releaseID + "_" + apiData.apiMethod),
        "consumes": [
            "application/json"
        ],
        "produces": [
            "*/*"
        ],
        "parameters": converRequestSchema(apiData.apiMethod, apiData.apiSchema.requestSchema),
        "responses": {
            "200": converResponseSchema(apiData.apiSchema.responseSchema)
        }
    }
}

return {
  "swagger": "2.0",
  "info": {
    "title": "Dataway Swagger2 Api Document",
    "description": "APIs",
    "termsOfService": "http://despairyoke.github.io/",
    "contact": {
      "name": "API Support",
      "url": "http://www.swagger.io/support",
      "email": "support@swagger.io"
    },
    "license":{
      "name": "Apache 2.0",
      "url": "http://www.apache.org/licenses/LICENSE-2.0.html"
    },
    "version": "1.0"
  },
  "host": ${serverHost},
  "basePath": "/",
  "schemes" : ["http", "https"],
  "consumes":["application/json"],
  "produces":["*/*"],
  "paths" : collect.list2map(
    apiDataList,
    "apiPath",
    (idx, val) -> {
        var newResult = collect.mapKeyReplace(
            { "value" : val },
            (key, val) -> { return string.toLowerCase(val.apiMethod) }
        );
        var newResult = collect.mapValueReplace(
            newResult,
            (key, val) -> { return converApi(val); }
        );
        return newResult;
    }
  ),
  "definitions" : [
  ],
  "tags" : [
  ]
}