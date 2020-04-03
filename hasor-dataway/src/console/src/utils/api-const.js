const apiBaseUrl = (oriUrl) => {
    let baseUrl = (window.API_BASE_URL === '{API_BASE_URL}') ? '/' : window.API_BASE_URL;
    return (baseUrl + oriUrl).replace("//", "/");
};
const adminBaseUrl = (oriUrl) => {
    let baseUrl = (window.ADMIN_BASE_URL === '{ADMIN_BASE_URL}') ? '/' : window.ADMIN_BASE_URL;
    return (baseUrl + oriUrl).replace("//", "/");
};
// 通用查 配置
const ApiUrl = {
    report: "//apis.hasor.net/projects/hasor-dataway/report",
    //
    apiInfo: adminBaseUrl(`/api/api-info`),
    apiList: adminBaseUrl(`/api/api-list`),
    apiDetail: adminBaseUrl(`/api/api-detail`),
    apiHistory: adminBaseUrl(`/api/api-history`),
    apiHistoryInfo: adminBaseUrl(`/api/get-history`),
    //
    execute: apiBaseUrl(`/`),
    apiSave: adminBaseUrl(`/api/save-api`),
    perform: adminBaseUrl(`/api/perform`),
    smokeTest: adminBaseUrl(`/api/smoke`),
    publish: adminBaseUrl(`/api/publish`),
    disable: adminBaseUrl(`/api/disable`),
    deleteApi: adminBaseUrl(`/api/delete`),
};

export {
    ApiUrl, apiBaseUrl
};
