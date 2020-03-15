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
const apiDetailData = {
    "id": 1,
    "path": "/demos/db/databases/",
    "apiComment": "",
    "status": 1,
    "select": "POST",
    "codeType": 'SQL',
    "codeInfo": {
        "codeValue": '<div>请编辑html内容</div>',
        "requestBody": '{"abc":true}',
        "responseBody": '"result data."',
        "headerData": [
            {"checked": true, "name": "name1", "value": "value1"},
            {"checked": false, "name": "name2", "value": "value2"},
            {"checked": false, "name": "name3", "value": "value3"},
            {"checked": true, "name": "name4", "value": "value4"},
            {"checked": true, "name": "name5", "value": "value5"}
        ]
    }
};
//
// POST 类
const executeData = {
    "id": 1,
    "paramMap": {
        "aaa": 123
    },
    "headerData": {
        "name1": "value",
        "name4": "value",
        "name5": "value"
    }
};
const modifyPath_failed = {
    "result": false,
    "message": 'Another API is already in use.'
};
const modifyPath_ok = {
    "result": true,
    "message": 'ok.'
};

const proxy = {
    'GET /api/api-info.json': apiInfoData,
    'GET /api/api-list.json': apiListData,
    'GET /api/api-detail.json': apiDetailData,

    'POST /api/execute': (req, res) => {
        res.send(executeData);
    },
    'POST /api/modify-path': (req, res) => {
        res.send(modifyPath_failed);
    },
};
module.exports = proxy;