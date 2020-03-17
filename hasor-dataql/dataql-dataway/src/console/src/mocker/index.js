const Mock = require('mockjs');
const Random = Mock.Random;
// GET 类
const apiInfoData = {
    "id": 1,
    "path": "/demos/db/databases/",
    "status": 1,
    "requestBody": '{"abc":true}',
    "headerData": [
        {"checked": true, "name": "name1", "value": "value"},
        {"checked": false, "name": "name2", "value": "value"},
        {"checked": false, "name": "name3", "value": "value"},
        {"checked": true, "name": "name4", "value": "value"},
        {"checked": true, "name": "name5", "value": "value"}
    ]
};
const apiListData = [
    {"id": 1, "checked": false, "path": "/demos/db/databases/", "status": 0, "comment": "现实所有表。"},
    {"id": 2, "checked": false, "path": "/demos/db/tables/", "status": 1, "comment": "现实所有表。"},
    {"id": 3, "checked": false, "path": "/demos/db/select/", "status": 2, "comment": "现实所有表。"},
    {"id": 4, "checked": false, "path": "/demos/user/user-list/", "status": 3, "comment": "现实所有表。"},
    {"id": 5, "checked": false, "path": "/demos/user/add-user/", "status": 0, "comment": "现实所有表。"},
    {"id": 6, "checked": false, "path": "/demos/user/delete-user/", "status": 1, "comment": "现实所有表。"},
    {"id": 7, "checked": false, "path": "/demos/role/role-list/", "status": 2, "comment": "现实所有表。"},
    {"id": 8, "checked": false, "path": "/demos/role/add-role/", "status": 3, "comment": "现实所有表。"},
    {"id": 9, "checked": false, "path": "/demos/role/delete-role/", "status": 0, "comment": "现实所有表。"},
    {"id": 0, "checked": false, "path": "/demos/role/update-role/", "status": 1, "comment": "现实所有表。"},
    {"id": 11, "checked": false, "path": "/demos/power/poser-list/", "status": 2, "comment": "现实所有表。"},
    {"id": 12, "checked": false, "path": "/demos/power/power-id/", "status": 3, "comment": "现实所有表。"},
    {"id": 13, "checked": false, "path": "/demos/power/check/", "status": 0, "comment": "现实所有表。"}
];
//
const proxy = {
    'GET /api/api-info.json': apiInfoData,
    'GET /api/api-list.json': apiListData,
    'GET /api/api-detail.json': (req, res) => {
        res.send({
            "id": 1,
            "path": "/demos/db/databases/",
            "apiComment": "",
            "status": 3,//Random.integer(0, 3),
            "select": "POST",
            "codeType": 'SQL',
            "codeInfo": {
                "codeValue": '<div>请编辑html内容</div>',
                "requestBody": '{"abc":true}',
                "headerData": [
                    {"checked": true, "name": "name1", "value": "value1"},
                    {"checked": false, "name": "name2", "value": "value2"},
                    {"checked": false, "name": "name3", "value": "value3"},
                    {"checked": true, "name": "name4", "value": "value4"},
                    {"checked": true, "name": "name5", "value": "value5"}
                ]
            }
        });
    },

    'POST /api/execute': (req, res) => {
        res.send({
            "success": Random.boolean(3, 5, false),
            "code": 500,
            "executionTime": -1,
            "value": {
                "body": req.body,
                "headers": req.headers
            },
            "message": 'Another API is already in use.',
        });
    },
    'POST /api/modify-path': (req, res) => {
        res.send(Mock.mock({
            "result": Random.boolean(),
            "message": 'Another API is already in use.'
        }));
    },
    'POST /api/api-save': (req, res) => {
        res.send(Mock.mock({
            "result": true,//Random.boolean(),
            "id": 1,
            "message": 'Save Failed.'
        }));
    },
    'POST /api/perform': (req, res) => {
        res.send({
            "success": true,//Random.boolean(3, 5, false),
            "code": 500,
            "executionTime": -1,
            "value": {
                "body": req.body,
                "headers": req.headers
            },
            "message": 'Another API is already in use.',
        });
    },
    'POST /api/smoke-test': (req, res) => {
        res.send({
            "success": true,//Random.boolean(3, 5, false),
            "result": {
                "body": req.body,
                "headers": req.headers
            },
            "message": 'Another API is already in use.',
        });
    },
    'POST /api/publish': (req, res) => {
        res.send({
            "success": true,//Random.boolean(3, 5, false),
            "result": {
                "body": req.body,
                "headers": req.headers
            },
            "message": 'Another API is already in use.',
        });
    },
    // 'GET /api/mock': (req, res) => {
    //     res.send(Mock.mock({
    //         'number1|1-100.1-10': 1,
    //         'number2|123.1-10': 1,
    //         'number3|123.3': 1,
    //         'number4|123.10': 1.123
    //     }));
    // }

};
module.exports = proxy;