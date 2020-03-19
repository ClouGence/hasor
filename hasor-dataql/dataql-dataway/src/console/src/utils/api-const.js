const BASE_URL = 'http://127.0.0.1:8080';
// 通用查 配置
const ApiUrl = {
    apiInfo: `${BASE_URL}/api/api-info`,
    apiList: `${BASE_URL}/api/api-list`,
    apiDetail: `${BASE_URL}/api/api-detail`,
    apiHistory: `${BASE_URL}/api/api-history`,
    //
    execute: `${BASE_URL}/api/execute`,
    modifyPath: `${BASE_URL}/api/modify-path`,
    apiSave: `${BASE_URL}/api/save-api`,
    perform: `${BASE_URL}/api/perform`,
    smokeTest: `${BASE_URL}/api/smoke`,
    publish: `${BASE_URL}/api/publish`,
};

export {
    ApiUrl,
};
