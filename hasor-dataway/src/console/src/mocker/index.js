const Mock = require('mockjs');
const Random = Mock.Random;
//
const proxy = {
    'GET /api/api-info': (req, res) => {
        res.send(Mock.mock({
            "success": true,//Random.boolean(3, 5, false),
            "code": 500,
            "message": 'Load Api failed -> Another API is already in use.',
            "result": {
                "id": 1,
                "path": "/demos/db/databases/",
                "select": "GET",
                "codeType": "DataQL",
                "status": 1,
                "requestBody": '{"abc":true}',
                "headerData": [
                    {"checked": true, "name": "name1", "value": "value"},
                    {"checked": false, "name": "name2", "value": "value"},
                    {"checked": false, "name": "name3", "value": "value"},
                    {"checked": true, "name": "name4", "value": "value"},
                    {"checked": true, "name": "name5", "value": "value"}
                ]
            }
        }));
    },
    'GET /api/api-list': (req, res) => {
        res.send(Mock.mock({
            "success": true,//Random.boolean(3, 5, false),
            "code": 500,
            "message": 'Another API is already in use.',
            "result": [
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
            ]
        }));
    },
    'GET /api/api-detail': (req, res) => {
        res.send(Mock.mock({
            "success": true,//Random.boolean(3, 5, false),
            "code": 500,
            "message": 'Another API is already in use.',
            "result": {
                "id": 1,
                "path": "/demos/db/databases/",
                "apiComment": "",
                "status": 3,//Random.integer(0, 3),
                "select": "POST",
                "codeType": 'DataQL',
                "codeInfo": {
                    "codeValue": 'return ${abc}.test',
                    "requestBody": '{"abc":true}',
                    "headerData": [
                        {"checked": true, "name": "name1", "value": "value1"},
                        {"checked": false, "name": "name2", "value": "value2"},
                        {"checked": false, "name": "name3", "value": "value3"},
                        {"checked": true, "name": "name4", "value": "value4"},
                        {"checked": true, "name": "name5", "value": "value5"}
                    ]
                },
                "optionData": {
                    //     "resultStructure": false
                }
            }
        }));
    },
    'GET /api/api-history': (req, res) => {
        res.send(Mock.mock({
            "success": true,//Random.boolean(3, 5, false),
            "code": 500,
            "message": 'Another API is already in use.',
            "result": [
                {historyId: 'sss1', time: 'sss1'},
                {historyId: 'sss2', time: 'sss2'},
                {historyId: 'sss3', time: 'sss3'},
                {historyId: 'sss4', time: 'sss4'},
                {historyId: 'sss5', time: 'sss5'},
                {historyId: 'sss6', time: 'sss5'},
                {historyId: 'sss7', time: 'sss5'},
                {historyId: 'sss8', time: 'sss5'},
                {historyId: 'sss9', time: 'sss5'},
                {historyId: 'sss10', time: 'sss5'},
                {historyId: 'sss11', time: 'sss11'},
                {historyId: 'sss12', time: 'sss11'},
                {historyId: 'sss13', time: 'sss11'},
                {historyId: 'sss14', time: 'sss11'},
                {historyId: 'sss15', time: 'sss11'},
                {historyId: 'sss16', time: 'sss11'},
                {historyId: 'sss17', time: 'sss11'},
                {historyId: 'sss18', time: 'sss11'},
            ]
        }));
    },
    'GET /api/get-history': (req, res) => {
        res.send(Mock.mock({
            "success": true,//Random.boolean(3, 5, false),
            "code": 500,
            "message": 'Another API is already in use.',
            "result": {
                "select": "POST",
                "codeType": 'DataQL',
                "codeInfo": {
                    "codeValue": 'return ${ccc}.ttt',
                    "requestBody": '{"abc":false}',
                    "headerData": [
                        {"checked": true, "name": "name1", "value": "value1"},
                        {"checked": false, "name": "name2", "value": "value2"},
                        {"checked": false, "name": "name3", "value": "value3"},
                        {"checked": true, "name": "name4", "value": "value4"},
                        {"checked": true, "name": "name5", "value": "value5"}
                    ]
                },
                "optionData": {
                    // "resultStructure": true
                }
            }
        }));
    },

    'POST /api/save-api': (req, res) => {
        res.send(Mock.mock({
            "success": Random.boolean(3, 5, false),
            "message": 'Save Failed.',
            "code": 500,
            "result": 20
        }));
    },
    'POST /api/perform': (req, res) => {
        res.send(Mock.mock({
            "success": Random.boolean(3, 5, false),
            "message": 'Save Failed.',
            "code": 500,
            "result": {
                "success": true,//Random.boolean(3, 5, false),
                "code": 500,
                "executionTime": -1,
                "value": {
                    "body": req.body,
                    "headers": req.headers
                },
            }
        }));
    },
    'POST /api/smoke': (req, res) => {
        res.send(Mock.mock({
            "success": Random.boolean(3, 5, false),
            "message": 'Save Failed.',
            "code": 500,
            "result": {
                "body": req.body,
                "headers": req.headers
            },
        }));
    },
    'POST /api/publish': (req, res) => {
        res.send(Mock.mock({
            "success": Random.boolean(3, 5, false),
            "message": 'Save Failed.',
            "code": 500,
            "result": true,
        }));
    },
    'POST /api/disable': (req, res) => {
        res.send(Mock.mock({
            "success": Random.boolean(3, 5, false),
            "message": 'Save Failed.',
            "code": 500,
            "result": true,
        }));
    },
    'POST /api/delete': (req, res) => {
        res.send(Mock.mock({
            "success": Random.boolean(3, 5, false),
            "message": 'Save Failed.',
            "code": 500,
            "result": true,
        }));
    },
};
module.exports = proxy;