import Vue from 'vue';


/***/
const tagInfo = (status) => {
    if (status === 0) {
        return {'css': 'info', 'title': 'Editor'};
    }
    if (status === 1) {
        return {'css': 'success', 'title': 'Published'};
    }
    if (status === 2) {
        return {'css': 'warning', 'title': 'Changes'};
    }
    if (status === 3) {
        return {'css': 'danger', 'title': 'Disable'};
    }
    return {'css': '', 'title': ''};
};

const errorBox = (content) => {
    Vue.prototype.$alert(content, 'Error', {confirmButtonText: 'OK'});
};

export {
    tagInfo, errorBox
};