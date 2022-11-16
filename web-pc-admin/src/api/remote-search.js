import request from '@/utils/request'

export function searchUser(name) {
    return request({
        url: '/web-pc-admin/search/user',
        method: 'get',
        params: { name }
    })
}

export function transactionList(query) {
    return request({
        url: '/web-pc-admin/transaction/list',
        method: 'get',
        params: query
    })
}
