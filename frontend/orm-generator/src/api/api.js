import request from '@/utils/request.js'

export function getConnection(form) {
	return request({
		url:'/mybatis/gen/conn',
		method:'post',
		data: form
	}) 
}
export function sendLists(form) {
	return request({
		url:'/mybatis/gen/gen',
		method:'post',
		data: form
	})
}