const fixBaseUrl = () => {
    if (window.API_BASE_URL === '{API_BASE_URL}') {
        return '/';
    }
    return window.API_BASE_URL;
};
const fixUrl = (oriUrl) => {
    return (fixBaseUrl() + oriUrl).replace("//", "/");
};
// 通用查 配置
const ApiUrl = {
    apiInfo: fixUrl(`/api/api-info`),
    apiList: fixUrl(`/api/api-list`),
    apiDetail: fixUrl(`/api/api-detail`),
    apiHistory: fixUrl(`/api/api-history`),
    apiHistoryInfo: fixUrl(`/api/get-history`),
    //
    execute: fixUrl(`/api/execute`),
    checkPath: fixUrl(`/api/check-path`),
    apiSave: fixUrl(`/api/save-api`),
    perform: fixUrl(`/api/perform`),
    smokeTest: fixUrl(`/api/smoke`),
    publish: fixUrl(`/api/publish`),
    disable: fixUrl(`/api/disable`),
};

export {
    ApiUrl,
};
