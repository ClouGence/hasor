const contextPath = () => {
    return window.CONTEXT_PATH;
};
const apiBaseUrl = (oriUrl) => {
    return (window.API_BASE_URL + oriUrl).replace('//', '/');
};
const adminBaseUrl = (oriUrl) => {
    return (window.location.pathname + oriUrl).replace('//', '/');
};

// 通用查 配置
const ApiUrl = {
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
    //
    analyzeSchema: adminBaseUrl(`/api/analyze-schema`),
};

export {
    ApiUrl, apiBaseUrl, contextPath
};
