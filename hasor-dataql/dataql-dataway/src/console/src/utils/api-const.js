const BASE_URL = 'http://127.0.0.1:8080';
// 通用查 配置
const ApiUrl = {
    apiInfo: `${BASE_URL}/api/api-info.json`, // 保存服务
    apiList: `${BASE_URL}/api/api-list.json`, // 删除接口
    execute: `${BASE_URL}/api/execute`, // 执行
};

export {
    ApiUrl,
};
