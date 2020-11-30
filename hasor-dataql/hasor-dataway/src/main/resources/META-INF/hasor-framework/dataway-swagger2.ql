//
// see  -> https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md

import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;
import "net.hasor.dataql.fx.basic.StringUdfSource" as string;
import "net.hasor.dataql.fx.basic.CollectionUdfSource" as collect;

var apiDataList = ${apiDataList};

var convertRequestSchema = (apiMethod, bodySchema, headerSchema) -> {
    if (bodySchema == null && headerSchema == null) {
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
                "schema" : bodySchema
            }
        ])
    } else {
        return collect.merge(headerSchema, collect.map2list(bodySchema.properties) => [
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

var convertResponseSchema = (bodySchema, headerSchema) -> {
    return {
        "description": "OK",
        "schema": bodySchema//,
        // "headers" : {},
        // "examples" : {
        //     "application/json" : sample
        // }
    }
}

var convertApi = (apiData) -> {
    return {
        "tags": [],
        "summary": apiData.comment,
        "operationId": ("api" + apiData.id + "_" + apiData.method),
        "consumes": [
            "application/json"
        ],
        "produces": [
            "*/*"
        ],
        "parameters": convertRequestSchema( apiData.method,
                                            json.fromJson(apiData.reqBodySchema),
                                            json.fromJson(apiData.reqHeaderSchema)),
        "responses": {
            "200": convertResponseSchema(   json.fromJson(apiData.resBodySchema),
                                            json.fromJson(apiData.resHeaderSchema)
                                            )
        }
    }
}

return {
  "swagger": "2.0",
  "info": {
    "title": "Interface Document for Dataway",
    "description": "Dataway Swagger2 Document",
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
  "basePath": ${serverBasePath},
  "schemes" : ["http", "https"],
  "consumes":["application/json"],
  "produces":["*/*"],
  "paths" : collect.list2map(
    apiDataList,
    "path",
    (idx, val) -> {
        var newResult = collect.mapKeyReplace(
            { "value" : val },
            (key, val) -> { return string.toLowerCase(val.method) }
        );
        var newResult = collect.mapValueReplace(
            newResult,
            (key, val) -> { return convertApi(val); }
        );
        return newResult;
    }
  ),
  "definitions" : [
  ],
  "tags" : [
  ]
}