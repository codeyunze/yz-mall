import request from '@/utils/request'

export function login(data) {
    return request({
        url: '/web-pc-admin/user/login',
        method: 'post',
        data
    })
}

export function getInfo(token) {
    return request({
        url: '/web-pc-admin/user/info',
        method: 'get',
        params: { token }
    })
}

export function logout() {
    return request({
        url: '/web-pc-admin/user/logout',
        method: 'post'
    })
}
