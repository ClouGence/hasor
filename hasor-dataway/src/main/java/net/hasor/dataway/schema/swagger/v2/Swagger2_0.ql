//
// see  -> https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md

import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;
import "net.hasor.dataql.fx.basic.StringUdfSource" as string;
import "net.hasor.dataql.fx.basic.CollectionUdfSource" as collect;

var apiDataList = ${apiDataList};

var converRequestSchema = (apiMethod, apiSchema) -> {
    var requestSchema = apiSchema.requestSchema;
    var headerSchema = apiSchema.headerSchema;
    if (requestSchema == null && headerSchema == null) {
        return null;
    }
    //
    var headerParams = collect.map2list(headerSchema.properties) => [
        {
            "name"       : key,
            "in"         : "header",
            "description": "a header",
            "required"   : false,
            "type"       : value.type
        }
    ];

    if ("get" != string.toLowerCase(apiMethod)) {
        return collect.merge(headerParams, [
            {
                "in"     : "body",
                "name"   : "mainBody",
                "schema" : requestSchema
            }
        ])
    } else {
        return collect.merge(headerParams, collect.map2list(requestSchema.properties) => [
            {
                "name"       : key,
                "in"         : "query",
                "description": "a param",
                "required"   : false,
                "type"       : value.type
            }
        ])
    }
    // {
    //     "description": "OK",
    //     "schema": requestSchema
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
        "parameters": converRequestSchema(apiData.apiMethod, apiData.apiSchema),
        "responses": {
            "200": converResponseSchema(apiData.apiSchema.responseSchema)
        }
    }
}

return {
  "swagger": "2.0",
  "info": {
    "title": "Interface Document for Dataway",
    "description": "Dataway OpenAPI 3.0.0 Document",
    "termsOfService": "https://www.hasor.net/web/dataway/index.html",
    "contact": {
      "name": "Dataway Support",
      "url": "https://www.hasor.net/web/dataway/index.html",
      "email": "zyc@hasor.net"
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