import request from '@/utils/request.js'

export function getConnection(form) {
	return request({
		url:'/mybatis/gen/conn',
		method:'post',
		data: form
	}) 
}